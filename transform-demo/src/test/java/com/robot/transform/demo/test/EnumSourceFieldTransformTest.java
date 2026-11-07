package com.robot.transform.demo.test;

import com.robot.transform.annotation.TransformEnum;
import com.robot.transform.demo.bean.StudentVO;
import com.robot.transform.demo.enums.Sex;
import com.robot.transform.util.TransformUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 验证@TransformEnum的源字段本身是枚举类型时的转换
 * <p>
 * 注意：与其它测试类统一加 @AutoConfigureMockMvc 共享同一个Spring上下文，
 * 避免多上下文各自持有单例 Transformer 导致跨上下文 bean 错位
 */
@SpringBootTest
@AutoConfigureMockMvc
class EnumSourceFieldTransformTest {

    /**
     * 源字段直接使用枚举类型的VO（区别于StudentVO中Integer类型的sex）
     */
    @Data
    static class StudentWithEnumSexVO {

        // 性别（源字段直接是枚举实例）
        private Sex sex;

        // 性别名称（枚举转换）
        @TransformEnum(Sex.class)
        private String sexName;
    }

    /**
     * 主测试：源字段是枚举实例，直接取枚举的text
     */
    @Test
    void transformWhenSourceFieldIsEnum() throws IllegalAccessException {
        StudentWithEnumSexVO male = new StudentWithEnumSexVO();
        male.setSex(Sex.MALE);
        TransformUtil.transform(male);
        assertEquals("男", male.getSexName());

        StudentWithEnumSexVO female = new StudentWithEnumSexVO();
        female.setSex(Sex.FEMALE);
        TransformUtil.transform(female);
        assertEquals("女", female.getSexName());
    }

    /**
     * 源字段是枚举实例时，List集合转换同样生效
     */
    @Test
    void transformEnumSourceFieldInList() throws IllegalAccessException {
        List<StudentWithEnumSexVO> list = new ArrayList<>();
        for (Sex sex : Sex.values()) {
            StudentWithEnumSexVO vo = new StudentWithEnumSexVO();
            vo.setSex(sex);
            list.add(vo);
        }
        TransformUtil.transform(list);
        for (StudentWithEnumSexVO vo : list) {
            assertEquals(vo.getSex().getText(), vo.getSexName());
        }
    }

    /**
     * 对照组：源字段是Integer类型的code，原有转换逻辑不受影响
     */
    @Test
    void transformWhenSourceFieldIsCode() throws IllegalAccessException {
        StudentVO vo = new StudentVO();
        vo.setSex(Sex.MALE.getCode());
        TransformUtil.transform(vo);
        assertEquals("男", vo.getSexName());
    }

    /**
     * 源字段为null时不转换，目标字段保持null
     */
    @Test
    void transformWhenSourceFieldIsNull() throws IllegalAccessException {
        StudentWithEnumSexVO vo = new StudentWithEnumSexVO();
        TransformUtil.transform(vo);
        assertNull(vo.getSexName());
    }
}
