package com.robot.transform.demo.test;

import com.robot.transform.component.TransformSession;
import com.robot.transform.demo.bean.StudentVO;
import com.robot.transform.demo.controller.transform.GradeTransformer;
import com.robot.transform.util.TransformUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 会话级缓存测试
 * 模拟大数据量分页导出场景：会话期间多次转换共享缓存，相同的原始值只触发一次实际转换
 * <p>
 * 注意：与其它测试类统一加 @AutoConfigureMockMvc 共享同一个Spring上下文，
 * 避免多上下文各自持有 GradeTransformer 单例导致计数器断言错位
 *
 * @author R
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransformSessionTest {

    @Autowired
    private GradeTransformer gradeTransformer;

    /**
     * 构造一页导出数据（模拟分页查询结果）
     */
    private List<StudentVO> newPage(int... grades) {
        List<StudentVO> page = new ArrayList<>();
        for (int grade : grades) {
            StudentVO vo = new StudentVO();
            vo.setGrade(grade);
            page.add(vo);
        }
        return page;
    }

    @BeforeEach
    void setUp() {
        gradeTransformer.resetBatchCallCount();
    }

    /**
     * 对照组：非会话模式下，分页多次转换，每页都要重新批量转换
     */
    @Test
    void transformWithoutSessionQueriesEveryPage() throws IllegalAccessException {
        for (int i = 0; i < 3; i++) {
            TransformUtil.transform(newPage(1, 2, 3, 1, 2));
        }
        assertEquals(3, gradeTransformer.getBatchCallCount(),
                "非会话模式下每次转换后缓存被清空，3页数据应触发3次批量转换");
    }

    /**
     * 会话模式：分页多次转换共享缓存，相同的字典值只查询一次
     */
    @Test
    void transformWithinSessionQueriesOnlyOnce() throws IllegalAccessException {
        try (TransformSession ignored = TransformUtil.openSession()) {
            for (int i = 0; i < 3; i++) {
                List<StudentVO> page = newPage(1, 2, 3, 1, 2);
                TransformUtil.transform(page);
                // 转换结果仍然正确
                for (StudentVO vo : page) {
                    assertEquals(" grade " + vo.getGrade(), vo.getGradeName());
                }
            }
        }
        assertEquals(1, gradeTransformer.getBatchCallCount(),
                "会话期间缓存跨页共享，3页相同字典值应只触发1次批量转换");
    }

    /**
     * 单对象转换路径（非批量集合入口）在会话内同样跨调用去重
     */
    @Test
    void singleBeanTransformWithinSessionQueriesOnlyOnce() throws IllegalAccessException {
        try (TransformSession ignored = TransformUtil.openSession()) {
            for (int i = 0; i < 3; i++) {
                StudentVO vo = new StudentVO();
                vo.setGrade(2);
                TransformUtil.transform(vo);
                assertEquals(" grade 2", vo.getGradeName());
            }
        }
        assertEquals(1, gradeTransformer.getBatchCallCount(),
                "会话期间单值路径缓存同样跨调用共享，应只触发1次转换");
    }

    /**
     * 会话关闭后缓存清空，新会话重新查询（字典变更可在新会话生效）
     */
    @Test
    void cacheClearedAfterSessionClosed() throws IllegalAccessException {
        try (TransformSession ignored = TransformUtil.openSession()) {
            TransformUtil.transform(newPage(1));
        }
        assertEquals(1, gradeTransformer.getBatchCallCount());

        // 新会话，缓存已清空，相同数据重新查询
        try (TransformSession ignored = TransformUtil.openSession()) {
            TransformUtil.transform(newPage(1));
        }
        assertEquals(2, gradeTransformer.getBatchCallCount(),
                "会话关闭时缓存被清空，新会话应重新查询");
    }

    /**
     * 嵌套会话：内层关闭不清缓存，最外层关闭才清
     */
    @Test
    void nestedSessionClearedOnlyByOutermost() throws IllegalAccessException {
        try (TransformSession outer = TransformUtil.openSession()) {
            TransformUtil.transform(newPage(1));
            assertEquals(1, gradeTransformer.getBatchCallCount());

            try (TransformSession inner = TransformUtil.openSession()) {
                TransformUtil.transform(newPage(1));
            }
            // 内层会话关闭后缓存仍在，转换直接命中
            assertEquals(1, gradeTransformer.getBatchCallCount());

            TransformUtil.transform(newPage(1));
            assertEquals(1, gradeTransformer.getBatchCallCount());
        }
        // 最外层会话已关闭，恢复非会话模式逐次查询
        TransformUtil.transform(newPage(1));
        assertEquals(2, gradeTransformer.getBatchCallCount(),
                "最外层会话关闭时才清空缓存，之后恢复逐次查询");
    }

    /**
     * 会话内新出现的字典值只增量查询：缓存命中与未命中合并成一次批量转换
     */
    @Test
    void sessionMergesCachedAndNewValues() throws IllegalAccessException {
        try (TransformSession ignored = TransformUtil.openSession()) {
            TransformUtil.transform(newPage(1, 2));
            assertEquals(1, gradeTransformer.getBatchCallCount());

            // 第二页：2已缓存，3为新值，只需为未命中的3查询一次
            List<StudentVO> page = newPage(2, 3);
            TransformUtil.transform(page);
            assertEquals(2, gradeTransformer.getBatchCallCount(),
                    "已缓存值直接复用，只有新值触发一次增量批量转换");
            for (StudentVO vo : page) {
                assertEquals(" grade " + vo.getGrade(), vo.getGradeName());
            }
        }
    }
}
