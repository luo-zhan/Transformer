package com.robot.transform.demo.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {
    @Autowired
    private MockMvc mvc;

    /**
     * 原始代码（手写转换逻辑）：验证手动转换后字段正确填充
     */
    @Test
    void getStudentOld() throws Exception {
        mvc.perform(get("/student/old/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("学生1"))
                .andExpect(jsonPath("$.sex").isNumber())
                .andExpect(jsonPath("$.sexName").isString())
                .andExpect(jsonPath("$.hobby").isNumber())
                .andExpect(jsonPath("$.hobbyName").isString())
                .andExpect(jsonPath("$.classId").isNumber())
                .andExpect(jsonPath("$.className").isString());
    }

    /**
     * 使用转换插件，转换普通VO：验证自动转换后字段正确填充
     */
    @Test
    void getStudent() throws Exception {
        mvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("学生1"))
                .andExpect(jsonPath("$.sex").isNumber())
                .andExpect(jsonPath("$.sexName").isString())
                .andExpect(jsonPath("$.hobby").isNumber())
                .andExpect(jsonPath("$.hobbyName").isString())
                .andExpect(jsonPath("$.classId").isNumber())
                .andExpect(jsonPath("$.className").isString());
    }

    /**
     * 使用转换插件，转换List：验证列表大小及每个元素的转换字段
     */
    @Test
    void getStudentForList() throws Exception {
        mvc.perform(get("/student/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5))
                // 验证第一个元素的转换字段
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].name").value("学生0"))
                .andExpect(jsonPath("$[0].sexName").isString())
                .andExpect(jsonPath("$[0].hobbyName").isString())
                .andExpect(jsonPath("$[0].className").isString())
                // 验证嵌套转换：第一个元素的同桌也完成了转换
                .andExpect(jsonPath("$[0].deskmate").isMap())
                .andExpect(jsonPath("$[0].deskmate.sexName").isString())
                .andExpect(jsonPath("$[0].deskmate.hobbyName").isString())
                .andExpect(jsonPath("$[0].deskmate.className").isString())
                // 验证最后一个元素
                .andExpect(jsonPath("$[4].id").value(4))
                .andExpect(jsonPath("$[4].name").value("学生4"))
                .andExpect(jsonPath("$[4].sexName").isString())
                .andExpect(jsonPath("$[4].hobbyName").isString())
                .andExpect(jsonPath("$[4].className").isString());
    }

    /**
     * 使用转换插件，转换包装类Page：验证Page结构及内部记录的转换字段
     */
    @Test
    void getStudentPage() throws Exception {
        mvc.perform(get("/student/page"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records.length()").value(5))
                .andExpect(jsonPath("$.records[0].sexName").isString())
                .andExpect(jsonPath("$.records[0].hobbyName").isString())
                .andExpect(jsonPath("$.records[0].className").isString());
    }

    /**
     * 使用转换插件，转换包装类ResultWrapper：验证Wrapper结构及内部数据的转换字段
     */
    @Test
    void getStudentWrapper() throws Exception {
        mvc.perform(get("/student/wrapper"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("学生1"))
                .andExpect(jsonPath("$.data.sexName").isString())
                .andExpect(jsonPath("$.data.hobbyName").isString())
                .andExpect(jsonPath("$.data.className").isString());
    }
}