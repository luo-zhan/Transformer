# Transformer 2.0

[![GitHub](https://img.shields.io/badge/license-MIT-green.svg)](http://opensource.org/licenses/MIT)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/Robot-L/translator)]()
[![GitHub last commit](https://img.shields.io/github/last-commit/Robot-L/translator?label=Last%20commit)]()

🎉🎉🎉

2.0来了，由Translator更名为Transformer，代码全部重构，拥抱spring体系，功能更强大更灵活。

## What is Transformer

Transformer是一款功能全面的数据转换工具，只需要几个简单的注解配置，即可实现各种姿势的数据转换，抛弃连表查询和累赘的转换逻辑，让开发更简单。

## Features

- 多种类型的转换（业务表转换、数据字典表转换、枚举转换、自定义转换）
- 开箱即用，简单配置几个注解即可实现自动转换
- 兼容所有数据类型（Entity，Collection，Page，ResultWrapper或其他自定义类型）
- 极强的扩展性，特殊场景方便的自定义转换实现，且支持自定义转换注解

如果你有兴趣，欢迎PR! :)

## How to use

* 添加 Transformer 依赖

> 需求场景：
> 学生表：student(id, name, class_id)，班级表：class(id, name, teacherId)
> 学生表中有一个`班级id`（class_id）字段，需要根据班级表的对应记录转换成`班级名称`

* 在班级类上添加`@Dictionary`注解标识自己是个字典，指定代表`字典编码`的列`id`和代表`字典文本`的列`name`，表示凡是通过班级字典转换后，班级id都会被转换成班级名称
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

* 在需要转换的`Student`类的字段`classId`上添加`@Translate`注解，指定作为字典的班级类`Class`，同时添加一个字段`className`用于接收班级名称的值
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
        
        // 增加className用于接收转换后的值，类型必须为String
        private String className;
    }
    ```

* 在查询接口的方法上添加`@Transformer`注解，大功告成
  ```java
  /** 学生服务 */
  @Service
  public class StudentService {
      @Resource
      StudentDao dao;
  
      /**
       * 查询所有学生信息，加上注解后自动判断方法返回值，并对内容进行转换
       */
      @Transformer
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
  > 这里仅展示一小部分功能，详细说明请参阅 [WIKI](https://github.com/Robot-L/Transformer/wiki)
  
## License

Transformer is under the MIT License.
