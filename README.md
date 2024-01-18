# Transformer

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=luo-zhan_Transformer&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=luo-zhan_Transformer)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=luo-zhan_Transformer&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=luo-zhan_Transformer)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=luo-zhan_Transformer&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=luo-zhan_Transformer)<br>
[![GitHub](https://img.shields.io/github/license/luo-zhan/Transformer)](http://opensource.org/licenses/apache-2-0)
[![GitHub code size](https://img.shields.io/github/languages/code-size/Robot-L/transformer)]()
[![GitHub last commit](https://img.shields.io/github/last-commit/Robot-L/translator?label=last%20commit)]()

🎉🎉🎉

全新的2.X版本来了，代码全部重构，拥抱spring体系，功能更全面，性能更强劲。

## 简介 / Transformer

Transformer是一款功能全面的字段转换工具，只需要几个简单的注解，即可实现各种姿势的字段转换，抛弃连表查询和累赘的转换逻辑，让开发更简单。

> 2.X 版已在生产环境稳定运行一年，功能更强大，性能更优越，JMH测试百万数据转换仅需0.15秒
>
![image](https://msb-edu-dev.oss-cn-beijing.aliyuncs.com/test/Transform.png)

## 场景 / Situation

你在**查询数据对象返回给前端**时是否也有以下场景：

1. 枚举值编码转换成文本（如性别Sex，“1”要转换成“男”）需要手动转换
2. 数据字典值转换成文本（如订单状态order_status，“1”要转换成“已下单”）需要手动转换
3. 数据对象中的外键id要转换成name，因使用连表查询从而不得已放弃了MybatisPlus的单表增强查询功能
4. 自定义字段转换场景（如年龄介于10-17为少年，18-45为青年...），但代码缺少可复用性，想复用的时候不顺畅

以上转换场景你会发现都是固定的逻辑，却要在各个不同的需求中重复编写，影响业务开发的效率，Transformer正是用来解决这些问题的。

## 功能 / Features

- [x] 多种类型的转换（数据字典转换、枚举转换、表外键转换、跨服务转换等其他自定义转换）
- [x] 开箱即用，极简的API设计，对业务代码无侵入
- [x] 支持处理包装类型（如返回值Page、ResultWrapper这种包装类拆包后才是真正的数据）
- [x] 支持自定义转换注解，增强扩展性（支持redis、本地缓存）
- [x] 支持嵌套字段的转换 【2.0.0已支持】
- [x] 转换配置缓存，转换配置首次处理后不再重复处理，节省反射开销 【2.1.0已支持】
- [x] 转换结果缓存，同一线程转换结果放入ThreadLocal，提高批量转换的性能 【2.1.0已支持】
- [ ] 多线程转换，进一步提高批量转换的性能

如果你有好的想法或建议，欢迎提issues或PR :)

## 快速上手 / Quick Start

### 1. 定义VO对象，增加转换注解

例如学生信息如下所示，返回给前端前须将其中的数值**转换**成可读文本

```js
{
  "id": 1, 
  "name": "周杰伦", 
  "sex": 1,        // 性别，1-男，2-女，存储在枚举类Sex.class中
  "classId": 32,   // 班级id
  "hobby": 2       // 爱好，0-无爱好,1-学习,2-音乐,3-运动...（存储在数据字典表中，分组名为"hobby"）
}
```

StudentVO.java定义，增加相应的几个文本字段，并加上转换注解:

```java
/** 学生信息VO */
@Data
public class StudentVO {
    private Long id;
    // 姓名
    private String name;

    // 性别值
    private Integer sex;

    // 性别（枚举转换，Sex是性别枚举类）
    @TransformEnum(Sex.class)
    private String sexName;

    // 爱好code
    private Integer hobby;

    // 爱好名称（数据字典转换，字典的组为"hobby"）
    @TransformDict(group = "hobby")
    private String hobbyName;

    // 班级id
    private Long classId;

     //班级名称（自定义转换——通过班级表的id转换成班级名称）
    @TransformClass
    private String className;
}
```
  在文本字段上使用转换注解，其中`@TransformEnum`、`@TransformDict`为内置注解，`@TransformClass`为自定义注解，组件支持自定义注解来提高扩展性，自定义注解的使用说明见下文wiki

### 2. 在查询接口的方法上添加`@Transform`注解，大功告成！
   ```java
   /** 学生接口 */
   @RestController
   @RequestMapping("/student")
   public class StudentController {

    /**
     * 查询学生信息
     * 加上@Transform注解开启字段转换
     */
    @Transform
    @GetMapping("/{id}")
    public StudentVO getStudent(@PathVariable Long id) {
        StudentVO student = ...
        // 这里假设从数据库查询出来的数据如下：
        // {
        //   "id": 1, 
        //   "name": "周杰伦", 
        //   "sex": 1,         // 性别，1-男，2-女
        //   "hobby": 2,       // 爱好，0-无爱好,1-学习,2-音乐,3-运动,...
        //   "classId": 32     // 班级id
        
        // }
        return student;
    }

}
   ```

### 3. 测试

前端访问`http://localhost:8080/student/1`，响应结果如下：

   ```json
   {
  "id": 1,
  "name": "Jay",
  "sex": 1,
  "sexName": "男",
  "hobby": 2,
  "hobbyName": "音乐"
  "classId": 32,
  "className": "三年二班"
}
   ```

完整示例代码见项目中`transform-demo`模块的`StudentController`类

## 依赖 / Dependency
   ```xml
   <dependency>
       <groupId>io.github.luo-zhan</groupId>
       <artifactId>transform-spring-boot-starter</artifactId>
       <version>2.1.1-RELEASE</version>
   </dependency>
   
  <!-- MybatisPlus扩展，增加外键id转换和Page类解包功能，非必须 -->
   <dependency>
       <groupId>io.github.luo-zhan</groupId>
       <artifactId>transform-extension-for-mybatis-plus</artifactId>
       <version>2.1.1-RELEASE</version>
   </dependency>

    <!-- MybatisFlex扩展，增加外键id转换和Page类解包功能，非必须 -->
    <dependency>
        <groupId>io.github.luo-zhan</groupId>
        <artifactId>transform-extension-for-mybatis-flex</artifactId>
        <version>2.1.1-RELEASE</version>
    </dependency>
 
   ```

## 性能 / JMH

使用JMH基准测试工具，分别测试了普通转换（transform），嵌套转换（transformNest）对一万、十万、百万数据的平均处理时间。

```agsl
JMH测试报告如下：
CPU：Intel(R) Core(TM) i7-10700 CPU @ 2.90GHz
Benchmark                         (number)   Mode     Score   Error   Units
TransformBenchmark.transform         10000   avgt     1.130           ms/op
TransformBenchmark.transform        100000   avgt    16.337           ms/op
TransformBenchmark.transform       1000000   avgt   159.852           ms/op
TransformBenchmark.transformNest     10000   avgt     4.019           ms/op
TransformBenchmark.transformNest    100000   avgt    48.355           ms/op
TransformBenchmark.transformNest   1000000   avgt   469.017           ms/op
```

> 注意：以上测试未包含IO访问，如果涉及IO访问，由于组件设计了转换结果缓存，所以转换性能将与IO耗时以及请求数据重复率有很大关系，所以具体性能还需根据实际情况而定。

测试代码详见`transform-demo`模块的`TransformBenchmark`类。

## 开源协议 / License

Transformer is under the Apache-2.0 License.

## 使用文档 / WIKI

这里仅简单介绍效果，更多功能的详细说明请参阅 [WIKI](https://github.com/luo-zhan/Transformer/wiki)

## 讨论 / Discussions

有任何问题或想说的，欢迎提issues或者来讨论组内畅所欲言

[💬进入讨论组](https://github.com/luo-zhan/Transformer/discussions)
