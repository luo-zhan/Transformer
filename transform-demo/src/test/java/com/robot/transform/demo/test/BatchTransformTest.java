package com.robot.transform.demo.test;

import com.robot.transform.demo.controller.transform.GradeTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 批量转换功能测试
 *
 * @author R
 */
@SpringBootTest
@AutoConfigureMockMvc
class BatchTransformTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GradeTransformer gradeTransformer;

    @BeforeEach
    void setUp() {
        gradeTransformer.resetBatchCallCount();
    }

    /**
     * 验证批量转换路径：List转换时 batchTransform 仅被调用一次
     */
    @Test
    void batchTransformPathForList() throws Exception {
        mvc.perform(get("/student/batch-list"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                // 验证第一个元素的gradeName已正确转换
                .andExpect(jsonPath("$[0].grade").value(1))
                .andExpect(jsonPath("$[0].gradeName").value(" grade 1"))
                // 验证第二个元素的gradeName已正确转换
                .andExpect(jsonPath("$[1].grade").value(2))
                .andExpect(jsonPath("$[1].gradeName").value(" grade 2"))
                // 验证第三个元素的gradeName已正确转换
                .andExpect(jsonPath("$[2].grade").value(3))
                .andExpect(jsonPath("$[2].gradeName").value(" grade 3"))
                // 验证第四个元素（grade=1，与第一个重复）
                .andExpect(jsonPath("$[3].grade").value(1))
                .andExpect(jsonPath("$[3].gradeName").value(" grade 1"))
                // 验证第五个元素（grade=2，与第二个重复）
                .andExpect(jsonPath("$[4].grade").value(2))
                .andExpect(jsonPath("$[4].gradeName").value(" grade 2"));

        // 核心断言：5个元素、3个不同grade值，batchTransform 只应被调用1次
        assertEquals(1, gradeTransformer.getBatchCallCount(),
                "List转换时 batchTransform 应仅被调用一次，证明批量路径生效");
    }

    /**
     * 验证批量转换不影响单对象转换
     */
    @Test
    void singleObjectTransformStillWorks() throws Exception {
        mvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("学生1"))
                .andExpect(jsonPath("$.sexName").isString())
                .andExpect(jsonPath("$.hobbyName").isString())
                .andExpect(jsonPath("$.className").isString());
    }
}
