# netstat


#  /proc/net/tcp 文件可以截取当前时间点的TCP情况
cat /proc/net/tcp | awk '{if($4 == '01' || $4=='06' || $4=='08') print}'
![](../resource/tcp.png)
