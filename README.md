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
CrudUtil crud = DataBaseUtilBuilder
    .mysql("locahost", "3306", "database", "username", "password")
    .buildCrudUtil();
    
// oracle
CrudUtil crud = DataBaseUtilBuilder
    .oracle("locahost", "1521", "oracle_service_name", "username", "password")
    .buildCrudUtil();  
    
CrudUtil crud = DataBaseUtilBuilder
    .oracle("locahost", "1521", fasle, "oracle_sid", "username", "password")
    .buildCrudUtil();  
    
// sqlserver
CrudUtil crud = DataBaseUtilBuilder
    .mssql("locahost", "1433", "database", "username", "password")
    .buildCrudUtil(); 
```

2019-10-17 新增通过连接串初始化的方式

``` java
//sqlserver 其他类似
CrudUtil crud = DataBaseUtilBuilder
    .init("jdbc:sqlserver://localhost:1433;DatabaseName=MY_DB", "username", "password")
    .buildCrudUtil(); 
```

第三步：调用增删改查

查询：
``` java
// 查询单条记录
Map<String, Object> row = crud.queryOne("select * from student where id = ?", 1);

// 查询多条记录
List<Map<String, Object>> rows = crud.queryList("select * from student where age > ?", 12);

// 查询单挑记录并转换为bean
// 在类中使用 @DatabaseColumn 注解标注转换关系
T row = crud.queryOne(T.class, "select * from student where id = 1");

// 查询并转换为bean的列表
List<T> rows = crud.queryList(T.class, "select * from student");
        
```

增加、修改、删除：
``` java

// 插入一条
crud.update("insert into student(id,age,name) values(?,?,?)", 1, 12, "小明");


// 插入多条
List<Object[]> params = new ArrayList<>();

params.add(new Object[]{1, 12, "小明"});
params.add(new Object[]{2, 14, "小红"});
params.add(new Object[]{3, 7, "小张"});

crud.updateBatch("insert into student(id,age,name) values(?,?,?)", params);

// 修改
crud.update("update student set name = '小明1' where id = 1");

// 删除
crud.update("delete from student where id = 1");

```

流式查询
``` java
// 由于jdbc 是把返回的查询结果都加载在内存中，如果查询特别大的表时，会造成java内存溢出
// 推荐大记录使用流式查询

QueryBatch queryBatch = crud.openQuery(sql, sqlParams.toArray());
while (queryBatch.next()) {
    Object[] row = queryBatch.getArray();
    System.out.println(row[0]);
}

```

# 获取表或视图元数据


首先构造元数据工具对象 DataBaseMetaData， 以下是 mysql 的例子（oracle、sqlserver与之类似）
``` java
MetaDataUtil metadata = DataBaseUtilBuilder
    .mysql("localhost", "3306", "test", "root", "root")
    .build();

```

获取所有的catalog
``` java
List<String> tables = metadata.getCatalogs();
```

获取某个catalog下所有的schema
``` java
List<String> tables = metadata.getSchemas("schemaName");
```


获取所有表的列表
``` java
// 获取所有表
List<TableMeta> tables = metadata.getTables();

// 获取所有视图
List<TableMeta> views = metadata.getViews();

// 获取某个catalog、schema下所有的表信息
List<TableMeta> tables = metadata.getTables("catalogName", "schemName");
```

获取表的详细信息，包含列的信息等
``` java
TableMeta table = metadata.getTableInfo( "catalogName", "schemName", table_name");
TableMeta table = metadata.getTableInfo( "table_name");
System.out.println(table.getColumns().size());
```

# oracle jdbc 依赖问题
由于Oracle授权问题，Maven不提供Oracle JDBC driver，为了在Maven项目中应用Oracle JDBC driver,必须手动添加到本地仓库。
从oracle 官网下载后，使用以下命令安装进本地仓库：
```bash
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=0.1 -Dpackaging=jar -Dfile=ojdbc8.jar
```
ps: 可以使用 /lib 目录下已经下好的 jar 包



