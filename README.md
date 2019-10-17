# 项目用途
封装 jdbc 的的增删改查，以及获取表的元数据

# CRUD (增删改查)
第一步：在 pom.xml 中加入依赖

``` xml

 <dependency>
    <groupId>me.qping.utils</groupId>
     <artifactId>DataBaseUtil</artifactId>
     <version>1.0-SNAPSHOT</version>
 </dependency>
 
```

第二步：初始化DataBase对象，配置数据库连接信息

``` java

// mysql
DataBase dataBase = DataBase
    .builder()
    .mysql("locahost", "3306", "database", "username", "password")
    .build();
    
// oracle
DataBase dataBase = DataBase
    .builder()
    .oracle("locahost", "1521", "oracle_service_name", "username", "password")
    .build();  
    
DataBase dataBase = DataBase
    .builder()
    .oracle("locahost", "1521", fasle, "oracle_sid", "username", "password")
    .build();  
    
// sqlserver
DataBase dataBase = DataBase
    .builder()
    .mssql("locahost", "1433", "database", "username", "password")
    .build(); 
```

2019-10-17 新增通过连接串初始化的方式

``` java
//sqlserver 其他类似
DataBase dataBase = DataBase
    .builder()
    .mssql("jdbc:sqlserver://localhost:1433;DatabaseName=MY_DB", "username", "password")
    .build(); 
```

第三步：调用增删改查

查询：
``` java
// 查询单条记录
Map<String, Object> row = dataBase.queryOne("select * from student where id = ?", 1);

// 查询多条记录
List<Map<String, Object>> rows = dataBase.queryList("select * from student where age > ?", 12);
        
```

增加、修改、删除：
``` java

// 插入一条
dataBase.update("insert into student(id,age,name) values(?,?,?)", 1, 12, "小明");


// 插入多条
List<Object[]> params = new ArrayList<>();

params.add(new Object[]{1, 12, "小明"});
params.add(new Object[]{2, 14, "小红"});
params.add(new Object[]{3, 7, "小张"});

dataBase.updateBatch("insert into student(id,age,name) values(?,?,?)", params);

// 修改
dataBase.update("update student set name = '小明1' where id = 1");

// 删除
dataBase.update("delete from student where id = 1");

```

# 获取表或视图元数据


首先构造元数据工具对象 DataBaseMetaData， 以下是 mysql 的例子（oracle、sqlserver与之类似）
``` java
DataBaseMetaData metadata = DataBaseMetaData
    .builder()
    .mysql("localhost", "3306", "test", "root", "root")
    .build();

```

获取所有表的列表
``` java
// 获取所有表
List<TableMeta> tables = metadata.listTable(DataBaseMetaData.TYPE_TABLE);
// 获取所有视图
List<TableMeta> views = metadata.listTable(DataBaseMetaData.TYPE_VIEW);
```

获取表的详细信息，包含列的信息等
``` java
TableMeta table = metadata.analyze( "table_name");
System.out.println(table.getColumns().size());
```

# oracle jdbc 依赖问题
由于Oracle授权问题，Maven不提供Oracle JDBC driver，为了在Maven项目中应用Oracle JDBC driver,必须手动添加到本地仓库。
从oracle 官网下载后，使用以下命令安装进本地仓库：
```bash
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=0.1 -Dpackaging=jar -Dfile=ojdbc8.jar
```
ps: 可以使用 /lib 目录下已经下好的 jar 包



