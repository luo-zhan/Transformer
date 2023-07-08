package com.robot.framework.demo.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class StudentControllerTest {
    @Autowired
    private MockMvc mvc;

    /**
     * 原始代码（手写转换逻辑）
     */
    @Test
    void getStudentOld() throws Exception {
        request("/student/old/1");
    }

    /**
     * 使用转换插件，转换普通VO
     */
    @Test
    void getStudent() throws Exception {
        request("/student/1");
    }

    /**
     * 使用转换插件，转换List
     */
    @Test
    void getStudentForList() throws Exception {
        request("/student/list/10");
    }

    /**
     * 使用转换插件，转换包装类Page
     */
    @Test
    void getStudentPage() throws Exception {
        request("/student/page");
    }

    /**
     * 使用转换插件，转换包装类ResultWrapper
     */
    @Test
    void getStudentWrapper() throws Exception {
        request("/student/wrapper");
    }

    private void request(String url) throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        response.setCharacterEncoding("utf-8");
        String result = response.getContentAsString();
        // 格式化输出
        Object jsonObject = JSON.isValidArray(result) ? JSON.parseArray(result) : JSON.parseObject(result);
        log.info(JSON.toJSONString(jsonObject, JSONWriter.Feature.PrettyFormat));
    }


}