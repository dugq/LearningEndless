# IO基础
* linux系统下，所有的外部设备都被当作文件来操作
* 对文件的读写都需要调用系统命令，返回一个file descriptor

# IO 模型
[打开本地文件：netty源码跟踪.excalidraw](https://excalidraw.com/)

#### serverSocketChannel#read、write的实现
~~~java
//NioMessageUnsafe 是AbstractNioMessageChannel内部类，是channel的核心实现。为了安全考虑，netty都把核心代码放在了unsafe类中进行操作。
public abstract class AbstractNioMessageChannel_NioMessageUnsafe {
    
    private final List<Object> readBuf = new ArrayList<Object>();
    @Override
    public void read() {
        assert eventLoop().inEventLoop();
        final ChannelConfig config = config();
        final ChannelPipeline pipeline = pipeline();
        final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
        allocHandle.reset(config);

        boolean closed = false;
        Throwable exception = null;
        try {
            try {
                do {
                    //读取通道中的数据到readBuf中。
                    //返回读取的消息数量
                    int localRead = doReadMessages(readBuf);
                    if (localRead == 0) {
                        break;
                    }
                    if (localRead < 0) {
                        closed = true;
                        break;
                    }

                    allocHandle.incMessagesRead(localRead);
                } while (allocHandle.continueReading());
            } catch (Throwable t) {
                exception = t;
            }
            //读取完消息后，进行入站事件触发
            int size = readBuf.size();
            for (int i = 0; i < size; i++) {
                readPending = false;
                //每条消息都会触发一次channelRead事件
                pipeline.fireChannelRead(readBuf.get(i));
            }
            //注意：这个clear不是为了清理message缓冲区，它只是为了清理掉channel消息的缓冲区。
            //每个channel的事件终究还是单线程的，而serverSocket一般情况在服务端也只有一个，list作为私有属性是为了保证每个消息都会被消费到
            //不会一次都收到多条消息时，在第一条出现错误后，其他消息被丢弃的情况。
            readBuf.clear();
            // 这个allocHandle，暂不研究
            allocHandle.readComplete();
            //接着触发一次raedComplete，从这里可以看出，它其实和select事件频率保持一致
            pipeline.fireChannelReadComplete();

            if (exception != null) {
                closed = closeOnReadError(exception);
                // 这里可以看出，异常触发一般都不是channel发生错误，而是读取channel的消息发生错误
                pipeline.fireExceptionCaught(exception);
            }

            if (closed) {
                inputShutdown = true;
                if (isOpen()) {
                    close(voidPromise());
                }
            }
        } finally {
            // Check if there is a readPending which was not processed yet.
            // This could be for two reasons:
            // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
            // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
            //
            // See https://github.com/netty/netty/issues/2254
            if (!readPending && !config.isAutoRead()) {
                removeReadOp();
            }
        }
    }
    //此方法是NioServerSocketChannel类的具体实现。
    // 从方法的实现可以看出，对于serverSocket，它的读处理就是为了建立一个socketChannel
    // 而且它每次也就accept一个。如果accept多个，那后面就不好设计了。
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(javaChannel());

        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable t) {
            logger.warn("Failed to create a new channel from an accepted socket.", t);

            try {
                ch.close();
            } catch (Throwable t2) {
                logger.warn("Failed to close a socket.", t2);
            }
        }

        return 0;
    }
    
    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        final SelectionKey key = selectionKey();
        final int interestOps = key.interestOps();

        for (;;) {
            Object msg = in.current();
            if (msg == null) {
                // Wrote all messages.
                if ((interestOps & SelectionKey.OP_WRITE) != 0) {
                    key.interestOps(interestOps & ~SelectionKey.OP_WRITE);
                }
                break;
            }
            try {
                boolean done = false;
                for (int i = config().getWriteSpinCount() - 1; i >= 0; i--) {
                    if (doWriteMessage(msg, in)) {
                        done = true;
                        break;
                    }
                }

                if (done) {
                    in.remove();
                } else {
                    // Did not write all messages.
                    if ((interestOps & SelectionKey.OP_WRITE) == 0) {
                        key.interestOps(interestOps | SelectionKey.OP_WRITE);
                    }
                    break;
                }
            } catch (Exception e) {
                if (continueOnWriteError()) {
                    in.remove(e);
                } else {
                    throw e;
                }
            }
        }
    }
}
~~~

~~~java
public abstract class AbstractNioByteChannel extends AbstractNioChannel {

    @Override
    protected int doReadBytes(ByteBuf byteBuf) throws Exception {
        final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
        return byteBuf.writeBytes(javaChannel(), allocHandle.attemptedBytesRead());
    }
    
    protected class NioByteUnsafe extends AbstractNioUnsafe {
    @Override
    public final void read() {
        final ChannelConfig config = config();
        if (shouldBreakReadReady(config)) {
            clearReadPending();
            return;
        }
        final ChannelPipeline pipeline = pipeline();
        final ByteBufAllocator allocator = config.getAllocator();
        final RecvByteBufAllocator.Handle allocHandle = recvBufAllocHandle();
        allocHandle.reset(config);

        ByteBuf byteBuf = null;
        boolean close = false;
        try {
            do {
                byteBuf = allocHandle.allocate(allocator);
                allocHandle.lastBytesRead(doReadBytes(byteBuf));
                if (allocHandle.lastBytesRead() <= 0) {
                    // nothing was read. release the buffer.
                    byteBuf.release();
                    byteBuf = null;
                    close = allocHandle.lastBytesRead() < 0;
                    if (close) {
                        // There is nothing left to read as we received an EOF.
                        readPending = false;
                    }
                    break;
                }

                allocHandle.incMessagesRead(1);
                readPending = false;
                pipeline.fireChannelRead(byteBuf);
                byteBuf = null;
            } while (allocHandle.continueReading());

            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();

            if (close) {
                closeOnRead(pipeline);
            }
        } catch (Throwable t) {
            handleReadException(pipeline, byteBuf, t, close, allocHandle);
        } finally {
            // Check if there is a readPending which was not processed yet.
            // This could be for two reasons:
            // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
            // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
            //
            // See https://github.com/netty/netty/issues/2254
            if (!readPending && !config.isAutoRead()) {
                removeReadOp();
            }
        }
    }
}
}
~~~

## ChannelOption
TCP_NODELA ：Nagle算法。默认开启，通过配置TCP_NODELA=false可关闭。

