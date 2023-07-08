package com.robot.framework.demo.test;

import com.robot.framework.demo.TransformDemoApplication;
import com.robot.framework.demo.bean.StudentVO;
import com.robot.framework.demo.enums.Sex;
import com.robot.transform.util.TransformUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

//    测试报告如下：
//    CPU：Intel(R) Core(TM) i7-10700 CPU @ 2.90GHz
//
//    Benchmark                         (number)  Mode  Cnt    Score    Error   Units
//    TransformBenchmark.transform         10000  avgt         1.130            ms/op
//    TransformBenchmark.transform        100000  avgt        16.337            ms/op
//    TransformBenchmark.transform       1000000  avgt       159.852            ms/op
//    TransformBenchmark.transformNest     10000  avgt         4.019            ms/op
//    TransformBenchmark.transformNest    100000  avgt        48.355            ms/op
//    TransformBenchmark.transformNest   1000000  avgt       469.017            ms/op
@Slf4j
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime) // 测试方法平均执行时间
@OutputTimeUnit(TimeUnit.MILLISECONDS)// 输出结果的时间单位
@Measurement(iterations = 1, time = 5) // 测试次数和时间
@Warmup(iterations = 1, time = 5) // 预热次数和时间
@Fork(1) // 1个线程
public class TransformBenchmark {
    private final List<StudentVO> nomalList = new ArrayList<>();
    private final List<StudentVO> nestTransformList = new ArrayList<>();
    private ConfigurableApplicationContext context;
    /**
     * 测试数据量
     */
    @Param({"10000", "100000", "1000000"})
    private int number;

    private static StudentVO newStudentVO(int i) {
        StudentVO student = new StudentVO();
        student.setId((long) i);
        student.setName("学生" + i);
        // 100个班级，随机分配
        student.setClassId(RandomUtils.nextLong(1, 100));
        // 男女随机
        student.setSex(RandomUtils.nextBoolean() ? Sex.MALE.getCode() : Sex.FEMALE.getCode());
        // 爱好随机
        student.setHobby(RandomUtils.nextInt(0, 4));
        return student;
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(TransformBenchmark.class.getSimpleName())

                .build();

        new Runner(options).run();
    }

    /**
     * 普通转换
     */
    @Benchmark
    public void transform() throws Exception {
        TransformUtil.transform(nomalList);
    }

    /**
     * 嵌套转换
     */
    @Benchmark
    public void transformNest() throws Exception {
        TransformUtil.transform(nestTransformList);
    }

    @Setup
    public void setUp() {
        // 启动容器
        context = SpringApplication.run(TransformDemoApplication.class);
        // 模拟从db中查询出来的数据
        for (int i = 0; i < number; i++) {
            StudentVO student = newStudentVO(i);
            nomalList.add(student);

        }
        for (int i = 0; i < number; i++) {
            StudentVO student = newStudentVO(i);
            nestTransformList.add(student);
        }
        for (int i = 1; i < nestTransformList.size(); i++) {
            StudentVO studentVO = nestTransformList.get(i);
            studentVO.setTeam(Collections.singletonList(nestTransformList.get(0)));
        }
        log.info("Spring容器启动成功，模拟测试数据{}条", number);
    }

    @TearDown
    public void tearDown() {
        context.close();
    }


}