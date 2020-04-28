# Translator

业务中有没有遇到查询结果中经常需要进行数据字典翻译的情况，如果还在用连表查询或者for循环处理，可以来试试Translator！



## 快速开始

用一个简单的Demo来阐述Translator的功能:
现有一张`student`表，其结构如下:

| id   | name   | class_id（班级id） | sex  | age  |
| ---- | ------ | ------------------ | ---- | ---- |
| 1    | 张三   | 1                  | 0    | 18   |
| 2    | 李四   | 3                  | 1    | 20   |
| 3    | 周杰伦 | 2                  | 0    | 41   |

其中班级`class_id`和性别`sex`是存的编码，需要被翻译。

假设班级信息表`class`，结构如下：

| id   | name     |
| ---- | -------- |
| 1    | 三年一班 |
| 2    | 三年二班 |
| 3    | 三年三班 |

而性别sex的文本值一般定义在数据字典表中，假设静态数据字典表`static_dict`的结构如下：

| id   | group_code            | dict_code | dict_text |
| ---- | --------------------- | --------- | --------- |
| 1    | sex                   | 0         | 男        |
| 2    | sex                   | 1         | 女        |
| 3    | state                 | 0         | 是        |
| ...  | ...（还有很多，省略） | ...       | ...       |

因为数据字典中有很多类型，比如性别、状态等等，所以需要用`group_code`字段来进行分组区分。

我们查询出学生信息给前台展示时的数据应该是：

```json
[
{"id":"1", "name":"张三", "className":"三年一班", "sexName":"男", "age":"18" },
{"id":"2", "name":"李四", "className":"三年三班", "sexName":"女", "age":"20" },
{"id":"3", "name":"周杰伦", "className":"三年二班", "sexName":"男", "age":"38"}
]
```

> Question
>
> 通常情况如何实现翻译？



## 初始化工程

创建一个空的Spring Boot项目，项目以mysql作为数据库进行演示

> 可以使用 [Spring Initializer](https://start.spring.io/) 快速初始化一个 Spring Boot 工程



## 添加依赖

Translator托管在`jitpack`中，所以需要添加仓库，在pom文件根节点下添加：

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
  <repository>
    <id>maven</id>
    <url>https://repo.maven.apache.org/maven2</url>
  </repository>
</repositories>
```

引入 Spring Boot Starter 父工程：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.6.RELEASE</version>
    <relativePath/>
</parent>
```

引入 `spring-boot-starter`、`translator`、`spring-boot-starter-test`、`spring-boot-starter-jdbc`、`lombok`、`mysql` 依赖（注：后面4个依赖是为了演示`translator`而使用，实际使用时非必须）

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
  </dependency>
  <dependency>
    <groupId>com.github.Robot-L</groupId>
    <artifactId>translator</artifactId>
    <version>1.3</version>
  </dependency>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.11</version>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```



## 配置

在 `application.yml` 配置文件中添加数据库的相关配置：

```yml
# 数据库配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/test
    username: root
    password:
```



## 编码

编写班级类`Class.java`（使用了 [Lombok](https://www.projectlombok.org/) 简化代码）
在类名上使用`@Dictionary`注解申明这是一个字典类，并指定代表字典编码和字典文本的列名

```java
@Data
@Dictionary(codeColumn = "id", textColumn = "name")
public class Class { 
    private Long id; 
    private String name;
}
```

编写静态数据字典类`StaticDict.java`，和上面一样，使用`@Dictionary`，注意多了一个`groupColumn`属性用来指定代表组的列名

```java
@Data
@Dictionary(codeColumn = "dict_code", textColumn = "dict_text", groupColumn = "group_code")
public class StaticDict {
    private Long id;
    private String groupCode;
    private String dictCode;
    private String dictText;
}
```

编写学生实体类`Student.java`
在字段`classId`和`sex`上分别添加`@Translate`注解申明字段需要被翻译，并使用`dictClass`分别指向上面两个字典类，值的注意的是字段`sex`上额外配置了`groupValue="sex"`，这个配置和`@Dictionary`中的`groupColumn`属性对应，代表寻找的是静态数据字典中的组为`sex`的数据。

新增两个字段`className`,`sexName`用于存放翻译后的值。

```java
@Data
public class Student {
    private Long id;
    private String name;
    @Translate(dictClass=Class.class)
    private String classId;
    @Translate(dictClass=StaticDict.class, groupValue="sex")
    private String sex;
    private Integer age;
    // 存放翻译后的值
    private String className;
    private String sexName;
}
```

编写服务类`StudentService.java`，在方法上标注`@Translator`注解（注意和上面的`@Translate`的区别）

```java
@Service
public class StudentService {
    @Resource
    JdbcTemplate template;
    /**
     * 查询所有学生信息
     */
    @Translator
    public List<Student> queryAllStudents(){
      	return template.query("select * from student", new BeanPropertyRowMapper<>(Student.class));
    }
}
```


## 开始使用

添加测试类，进行功能测试：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SampleTest {
    @Resource
    StudentService studentService;
  
    @Test
    public void test() {
        List<Student> students = studentServiceImpl.queryAllStudents();
        students.forEach(System.out::println);
    }
}
```

控制台输出：

```sh
Student(id=1, name=张三, classId=1, className=三年一班, sex=0, sexName=男, age=18)
Student(id=2, name=李四, classId=3, className=三年三班, sex=1, sexName=女, age=20)
Student(id=3, name=周杰伦, classId=2, className=三年二班, sex=0, sexName=男, age=38)
```



## 总结

实际项目中，实体类、服务类都已经存在，所以只要通过以上几个简单步骤配置好注解，就实现了Student类的翻译功能，甚至没有写一句代码。

但Translator的功能还不止如此，如何根据枚举来翻译？如何自定义翻译？想要详细了解Translator的功能，就继续往下看吧！

