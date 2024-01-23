# 关键词
* Driver : jdk提供的java连接数据库标准
  * mysql-connector-java 是mysql官方根据java标准实现的一套解决方案
* DriverManager
  * 1、java官方用来管理application中注册的driver
  * 2、使用getConnection方法来获取连接
* Connection：数据库长连接
  * 1、线程不安全的
  * 2、autocommit=true 自动提交
  * 3、createStatement
  * 4、createPreparedStatement
* statement / preparedStatement sql执行器
  * statement : 静态SQL
  * preparedStatement : 动态SQL 
    * 可以防止SQL注入攻击
* 事务
  * statement / preparedStatement 都是自动提交事务
  * 但是他们每次执行是一次同步的完整的交互，也就是说一条SQL执行完毕就代表一个事务执行完毕
  * 如果需要把多条SQL放入一个事务，需要把自动提交关闭
  * 每个事务可以设置自己的隔离级别
* datasource 池化connection
  * c3p0
  * druid


