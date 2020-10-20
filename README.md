# Translator
[![GitHub](https://img.shields.io/badge/license-MIT-green.svg)](http://opensource.org/licenses/MIT)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Robot-L/translator)]()
[![GitHub last commit](https://img.shields.io/github/last-commit/Robot-L/translator?label=Last%20commit)]()


## What is Translator
Translator是一款功能全面的数据翻译工具，只需要几个简单的注解配置，即可实现各种姿势的数据翻译，抛弃连表查询和累赘的翻译逻辑，让开发更简单。

## Features
- 多种类型的翻译（业务表翻译、数据字典表翻译、枚举翻译）
- 开箱即用，简单配置几个注解即可实现自动翻译
- 支持翻译的数据格式丰富（Entity，Map，List）
- 极强的扩展性，特殊场景方便的自定义翻译实现
## TO-DO
- 对于AOP功能，能识别的方法返回类型只有Map、List等通用类型，如果是自定义的类（如Page、Response等），无法兼容，计划设计一个数据探测器类，用@Bean进行注入
- 加入多数据源支持

如果你有兴趣，欢迎PR! :)

## How to use
* 添加 Translator 依赖

    * Maven:
    ```xml
    <dependency>
        <groupId>com.github.Robot-L</groupId>
        <artifactId>translator</artifactId>
        <version>1.3</version>
    </dependency>
    
    ```
    ```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ```
    * Gradle
    ```groovy
    dependencies {
        implementation 'com.github.Robot-L:translator:1.3'
    }
    ```
    ```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
    ```
    
  > 需求场景：
  > 学生表：student(id, name, class_id)，班级表：class(id, name, teacherId)
  > 学生表中有一个`班级id`（class_id）字段，需要根据班级表的对应记录翻译成`班级名称`
  
* 在班级类上添加`@Dictionary`注解标识自己是个字典，指定代表`字典编码`的列`id`和代表`字典文本`的列`name`，表示凡是通过班级字典翻译后，班级id都会被翻译成班级名称
  ```java
  /** 班级信息 */
  @Data
  @Dictionary(codeColumn = "id", textColumn = "name")
  public class Class {
      /**
       * 主键ID
       */
      private Long id;
      /**
       * 班级名称
       */
      private String name;
      /**
       * 班主任
       */
      private Long teacherId;
  }
  ```
  
* 在需要翻译的`Student`类的字段`classId`上添加`@Translate`注解，指定作为字典的班级类`Class`，同时添加一个字段`className`用于接收班级名称的值
    ```java
    /** 学生信息类 */
    @Data
    public class Student {
        /**
         * 主键ID
         */
        private Long id;
        /**
         * 姓名
         */
        private String name;
        /**
         * 班级id
         */
        @Translate(Class.class)
        private Long classId;
        
        // 增加className用于接收翻译后的值，类型必须为String
        private String className;
    }
    ```
  
* 在查询接口的方法上添加`@Translator`注解，大功告成
  ```java
  /** 学生服务 */
  @Service
  public class StudentService {
      @Resource
      StudentDao dao;
  
      /**
       * 查询所有学生信息，加上注解后自动判断方法返回值，并对内容进行翻译
       */
      @Translator
      public List<Student> queryAllStudents(){
          return dao.queryAll();
      } 
     
  }
  ```
  
* 测试
  ```java
  List<Student> students = studentService.queryAllStudents();
  students.forEach(System.out::println);
  ```
  控制台输出：
  
  ```sh
  Student(id=1, name=张三, classId=1, className=三年一班)
  Student(id=2, name=李四, classId=3, className=三年三班)
  Student(id=3, name=周杰伦, classId=2, className=三年二班)
  ```
  >这里仅展示一小部分功能，详细说明请参阅 [WIKI](https://github.com/Robot-L/translator/wiki)
  
## License
Translator is under the MIT License.
