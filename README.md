# Translator
[![GitHub](https://img.shields.io/badge/license-MIT-green.svg)](http://opensource.org/licenses/MIT)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Robot-L/translator)]()
[![GitHub last commit](https://img.shields.io/github/last-commit/Robot-L/translator?label=Last%20commit)]()


## What is Translator
Translator是一款功能全面的数据翻译工具，只需要几个简单的注解配置，就能实现强大的翻译，抛弃连表查询和累赘的翻译逻辑，让开发更简单。

## Features
- 多种类型的翻译（业务表翻译、数据字典表翻译、枚举翻译）
- 开箱即用，简单配置几个注解即可实现自动翻译
- 支持翻译的数据格式丰富（Entity，Map，List）
- 极强的扩展性，特殊场景方便的自定义翻译实现

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
  > 以学生和班级为例，学生信息中有一个`班级id`（classId）字段，需要根据班级信息翻译成`班级名称`，在这里班级信息就充当了一个字典。
* 在字典类上添加`@Dictionary`注解，指定字典表中代表code的列和代表text的列
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
  
* 在需要翻译的实体类的字段上添加`@Translate`注解，指向字典类`Class`，同时添加一个字段`className`用于接收翻译值
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
        private String className;
    }
    ```
  
* 在查询接口方法上添加`@Translator`注解，指定方法返回值对应翻译配置类`Student`
  ```java
  /** 学生服务 */
  @Service
  public class StudentService {
      @Resource
      StudentDao dao;
  
      /**
       * 查询所有学生信息
       */
      @Translator(Student.class)
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