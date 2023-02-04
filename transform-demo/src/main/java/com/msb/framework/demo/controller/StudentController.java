package com.msb.framework.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.msb.framework.demo.bean.ResultWrapper;
import com.msb.framework.demo.bean.StudentVO;
import com.msb.framework.demo.config.TransformConfig;
import com.msb.framework.demo.enums.Sex;
import com.msb.framework.demo.service.ClassService;
import com.msb.framework.demo.service.DictionaryService;
import com.msb.framework.demo.service.StudentService;
import com.msb.framework.demo.service.convert.StudentConvert;
import com.robot.transform.annotation.Transform;
import com.robot.transform.component.Dict;
import com.robot.transform.component.UnWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 学生示例接口
 *
 * @author R
 * @date 2022-10-5
 */
@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {
    @Resource
    private StudentConvert studentConvert;
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private ClassService classService;
    @Resource
    private StudentService studentService;

    /**
     * 原始代码（手写转换逻辑，作为对比）
     */
    @GetMapping("/old/{id}")
    public StudentVO getStudentOld(@PathVariable Long id) {
        StudentVO studentVO = studentConvert.toVo(studentService.getById(id));
        // 原始的转换逻辑：
        // 1、翻译班级名
        String className = classService.getName(studentVO.getClassId());
        studentVO.setClassName(className);
        // 2、翻译性别
        Integer sex = studentVO.getSex();
        String sexName = Dict.getTextByCode(Sex.class, sex);
        studentVO.setSexName(sexName);
        // 3、翻译班干部职位
        Integer classLeaderCode = studentVO.getClassLeader();
        String classLeader = dictionaryService.getText("classLeader", classLeaderCode);
        studentVO.setClassLeaderName(classLeader);
        return studentVO;
    }

    /**
     * 1、使用转换插件，转换普通对象
     * 转换配置在StudentVO中，下同
     */
    @GetMapping("/{id}")
    @Transform
    public StudentVO getStudent(@PathVariable Long id) {
        return studentConvert.toVo(studentService.getById(id));
    }

    /**
     * 2、使用转换插件，转换List
     */
    @GetMapping("/list")
    @Transform
    public List<StudentVO> getStudentForList() {
        // 示例代码，实际情况下应从db获取
        StudentVO student1 = studentConvert.toVo(studentService.getById(1L));
        StudentVO student2 = studentConvert.toVo(studentService.getById(2L));
        return Arrays.asList(student1, student2);
    }

    /**
     * 3、使用转换插件，转换包装类Page
     * Page解包器由插件扩展包-mybatisPlus扩展包提供
     *
     * @see UnWrapper
     */
    @GetMapping("/page")
    @Transform
    public IPage<StudentVO> getStudentPage() {
        return new Page<StudentVO>().setRecords(getStudentForList());
    }

    /**
     * 4、使用转换插件，转换包装类ResultWrapper
     * ResultWrapper解包器在项目实际使用时需自己实现
     *
     * @see TransformConfig.ResultUnWrapper
     */
    @GetMapping("/wrapper")
    @Transform
    public ResultWrapper<StudentVO> getStudentWrapper() {
        return ResultWrapper.success(getStudent(1L));
    }

}
