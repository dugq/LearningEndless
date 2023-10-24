/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dugq.db.redis;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Java Serialization Redis serializer. Delegates to the hessian serializer.
 *
 * @author hwq
 */
public class Hessian2SerializationRedisSerializer implements RedisSerializer<Object> {

    private static final Logger log = LoggerFactory.getLogger(Hessian2SerializationRedisSerializer.class);

    /**
     * flag,表示数据是压缩过的
     */
    public static final int FLAG_COMPRESSED = 1;

    private static final byte[] EMPTY_ARRAY = new byte[0];

    /**
     * Maximum data size allowed.
     */
    public static final int DEFAULT_MAX_SIZE = 1024 * 1024;

    /**
     * 序列化后的数据超过这个阈值则压缩
     */
    private int compressionThreshold = 1024;

    /**
     * 缓存的数据超过这个大小则抛出异常，防止缓存大对象
     */
    private int maxSize = DEFAULT_MAX_SIZE;

    /**
     * 数据压缩类型：gzip/zip
     */
    private String compressMode = "gzip";

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        if(bytes.length == 1){
            throw new SerializationException("Cannot deserialize, bytes.length must be > 1, maybe this key is used by other redis client，or you use RedisTemplate to incr/decr");
        }

        try {
            byte flags = bytes[0];
            byte[] data = Arrays.copyOfRange(bytes, 1, bytes.length);
            if((flags & FLAG_COMPRESSED) != 0){//如果flags里标记了数据是压缩的，则解压
                data = decompress(data);
            }
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            Hessian2Input hessian2Input = new Hessian2Input(in);
            return hessian2Input.readObject();
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize, maybe this key is used by other redis client", ex);
        }
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Hessian2Output hessian2Output = new Hessian2Output(out);
            hessian2Output.writeObject(object);
            hessian2Output.flush();
            byte[] data = out.toByteArray();
            byte flags = 0;

            if (data.length > this.compressionThreshold) {
                byte[] compressed = compress(data);
                if (compressed.length < data.length) {
                    if (log.isDebugEnabled()) {
                        log.debug("Compressed " + object.getClass().getName() + " from "
                                + data.length + " to " + compressed.length);
                    }
                    data = compressed;
                    flags |= FLAG_COMPRESSED;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Compression increased the size of "
                                + object.getClass().getName() + " from " + data.length
                                + " to " + compressed.length);
                    }
                }
            }
            if (data.length > maxSize) {
                throw new IllegalArgumentException(
                        "Cannot cache data larger than " + maxSize + " byte (you tried to cache a "
                                + data.length + " byte object)");
            }

            //把flags拼接到真正的数据之前
            out = new ByteArrayOutputStream();
            out.write(flags);
            out.write(data);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }

    /**
     * Compress the given array of bytes.
     */
    public final byte[] compress(byte[] in) {
        if("zip".equals(this.compressMode)){
            return zipCompress(in);
        }
        return gzipCompress(in);
    }

    private byte[] zipCompress(byte[] in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(in.length);
        DeflaterOutputStream os = new DeflaterOutputStream(baos);
        try {
            os.write(in);
            os.finish();

            IOUtils.closeQuietly(os);
        } catch (IOException e) {
            throw new RuntimeException("IO exception compressing data", e);
        } finally {
            IOUtils.closeQuietly(baos);
        }
        return baos.toByteArray();
    }

    private byte[] gzipCompress(byte[] in) {
        if (in == null) {
            throw new NullPointerException("Can't compress null");
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gz = new GZIPOutputStream(bos)){
            gz.write(in);
            gz.close();//一定要先close，否则bos拿到的是不完整的数据
            byte[] rv = bos.toByteArray();
            // log.debug("Compressed %d bytes to %d", in.length, rv.length);
            return rv;
        } catch (IOException e) {
            throw new RuntimeException("IO exception compressing data", e);
        }
    }

    private static int COMPRESS_RATIO = 8;

    /**
     * Decompress the given array of bytes.
     *
     * @return null if the bytes cannot be decompressed
     */
    protected byte[] decompress(byte[] in) {
        if("zip".equals(this.compressMode)){
            return zipDecompress(in);
        }
        return gzipDecompress(in);
    }

    private byte[] zipDecompress(byte[] in) {
        int size = in.length * COMPRESS_RATIO;
        ByteArrayInputStream bais = new ByteArrayInputStream(in);
        InflaterInputStream is = new InflaterInputStream(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        try {
            byte[] uncompressMessage = new byte[size];
            while (true) {
                int len = is.read(uncompressMessage);
                if (len <= 0) {
                    break;
                }
                baos.write(uncompressMessage, 0, len);
            }
            baos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Failed to decompress data", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(baos);
        }
        return null;
    }

    private byte[] gzipDecompress(byte[] in) {
        ByteArrayOutputStream bos = null;
        if (in != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(in);
            bos = new ByteArrayOutputStream();
            try (GZIPInputStream gis = new GZIPInputStream(bis)){

                byte[] buf = new byte[16 * 1024];
                int r = -1;
                while ((r = gis.read(buf)) > 0) {
                    bos.write(buf, 0, r);
                }
            } catch (IOException e) {
                log.error("Failed to decompress data", e);
                bos = null;
            } finally {
                IOUtils.closeQuietly(bis);
            }
        }
        return bos == null ? null : bos.toByteArray();
    }

}
