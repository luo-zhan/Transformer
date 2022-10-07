# Transformer 2.0

[![GitHub](https://img.shields.io/github/license/luo-zhan/Transformer)](http://opensource.org/licenses/apache2.0)
[![GitHub code size](https://img.shields.io/github/languages/code-size/Robot-L/translator)]()
[![GitHub last commit](https://img.shields.io/github/last-commit/Robot-L/translator?label=Last%20commit)]()

🎉🎉🎉

全新的2.0来了，由Translator更名为Transformer，代码全部重构，拥抱spring体系，功能更强大更灵活。

## What is Transformer

Transformer是一款功能全面的数据转换工具，只需要几个简单的注解配置，即可实现各种姿势的字段转换，抛弃连表查询和累赘的转换逻辑，让开发更简单。

## Situation

 你在**查询数据对象返回给前端**时是否也有以下场景：
 1. 枚举值编码转换成文本（如性别Sex，“1”要转换成“男”）需要手动转换
 2. 数据字典值转换成文本（如订单状态order_status，“1”要转换成“已下单”）需要手动转换
 3. 数据对象中的外键id要转换成name，因使用连表查询从而不得已放弃了MybatisPlus的单表增强查询功能
 4. 自定义字段转换场景（如年龄介于10-17为少年，18-45为青年...），但代码缺少可复用性，想复用的时候不顺畅      

以上转换场景你会发现都是固定的逻辑，却要在各个不同的需求中重复编写，影响业务开发的效率，Transformer正是用来解决这些问题的。

## Features

- [x] 多种类型的转换（数据字典表转换、枚举转换、表外键转换、自定义转换）
- [x] 开箱即用，极简的API设计
- [x] 智能识别返回值类型（如返回值Page、ResultWrapper这种包装类拆包后才是真正的数据）
- [x] 极强的扩展性，特殊场景如跨服务接口转换，也能方便的自定义实现（支持使用自定义注解） 
- [ ] 转换结果加入缓存，在集合转换时可以节省资源
- [ ] 多线程转换，提高转换速度

如果你有好的想法或建议，欢迎提issues或PR :)

## How to use

1. 添加依赖

  * Maven
     ```xml
     <dependency>
         <groupId>io.github.luo-zhan</groupId>
         <artifactId>transform-spring-boot-starter</artifactId>
         <version>2.0.0-RELEASE</version>
     </dependency>
    <!-- MybatisPlus扩展，增加外键id转换和Page类解包功能，非必须 -->
     <dependency>
         <groupId>io.github.luo-zhan</groupId>
         <artifactId>transform-extension-for-mybatis-plus</artifactId>
         <version>2.0.0-RELEASE</version>
     </dependency>
   
     ```
  * Gradle
    ```groovy
    dependencies {
        implementation 'io.github.luo-zhan:transform-spring-boot-starter:2.0.0-RELEASE'
        // MybatisPlus扩展，增加外键id转换和Page类解包功能，非必须
        implementation 'io.github.luo-zhan:transform-extension-for-mybatis-plus:2.0.0-RELEASE'
    }
    ```
2. 在VO类的属性上使用`@Transform`注解
    > 例如现在有这样一个需求场景：   
      学生信息包括姓名(name)、性别(sex)、班级(class_id)、班干部(class_leader)
    > ```js
    > {
    >   "id": 1, 
    >   "name": "周杰伦", 
    >   "sex": 1,          // 性别，1-男，2-女，存储在枚举Sex中
    >   "classId": 32,     // 班级id
    >   "classLeader": 2   // 班干部，0-普通成员,1-班长,2-音乐委员,3-学习委员，存储在数据字典表中，group为"classLeader"
    > }
    > ```
    > 从数据库中查询出如上数据后，需要将其中的数字值**转换**成文本再传递给前端展示
    
    VO定义如下，在转换属性上标注`@TransformX`注解：

    ```java
    /** 学生信息VO */
    @Data
    public class StudentVO {
        /**
         * 主键ID
         */
        private Long id;
        /**
         * 姓名
         */
        private String name;
        /**
         * 性别值
         */
        private Integer sex;
        /**
         * 性别（通过枚举转换，Sex是性别枚举类）
         */
        @TransformEnum(Sex.class)
        private String sexName;
        /**
         * 班干部值
         */
        private Integer classLeader;
        /**
         * 班干部（通过数据字典转换，字典的组为"classLeader"）
         */
        @TransformDict(group = "classLeader")
        private String classLeaderName;
        /**
         * 班级id
         */
        private Long classId;
         /**
         * 班级名称（此处是演示的自定义转换注解，通过班级表的id转换成班级名称，自定义转换注解使用方式见wiki）
         */
        @TransformClass
        private String className;
    }
    ```


3. 在查询接口的方法上添加`@Transform`注解，大功告成
   ```java
   /** 学生接口 */
   @RestController
   @RequestMapping("/student")
   public class StudentController {
   
       /**
        * 查询学生信息
        * 加上@Transform注解后利用AOP自动对返回数据中的字段进行转换
        */
       @GetMapping("/{id}")
       @Transform
       public StudentVO getStudent(@PathVariable Long id) {
         StudentVO student = ...
         // 这里假设从数据库查询出来的数据如下：
         // {
         //   "id": 1, 
         //   "name": "周杰伦", 
         //   "sex": 1,          // 性别，1-男，2-女
         //   "classId": 32,     // 班级id
         //   "classLeader": 2   // 班干部，0-普通成员,1-班长,2-音乐委员,3-学习委员
         // }
         return student;
       } 
      
   }
   ```
  
4. 测试一下，前端访问`http://localhost:8080/student/1`
  响应结果：
  
   ```json
   {
      "id": 1, 
      "name": "周杰伦", 
      "sex": 1,      
      "sexName": "男",  
      "classId": 32,   
      "className": "三年二班", 
      "classLeader": 2 
      "classLeaderName": "音乐委员" 
   }
   ```
   完整示例代码见项目中transform-demo模块的StudentController类

  
## License

Transformer is under the Apache-2.0 License.

## WIKI
这里仅简单介绍效果，更多功能的详细说明请参阅 [指南](https://github.com/luo-zhan/Transformer/wiki)
