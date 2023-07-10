package com.robot.transform.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.robot.dict.Dict;
import com.robot.transform.annotation.Transform;
import com.robot.transform.demo.bean.ResultWrapper;
import com.robot.transform.demo.bean.StudentVO;
import com.robot.transform.demo.config.TransformConfig;
import com.robot.transform.demo.enums.Sex;
import com.robot.transform.demo.service.ClassService;
import com.robot.transform.demo.service.DictionaryService;
import com.robot.transform.demo.service.StudentService;
import com.robot.transform.demo.service.convert.StudentConvert;
import com.robot.transform.extend.TransformExtendForMyBatisPlusAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
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
     * 原始代码，手写转换逻辑，作为对比
     *
     * @link 启动项目后点击链接 <a href="http://localhost:8080/student/old/1">查看效果</a>
     */
    @GetMapping("/old/{id}")
    public StudentVO getStudentOld(@PathVariable Long id) {
        StudentVO studentVO = studentConvert.toVo(studentService.getById(id));
        // 原始的转换逻辑，比较繁琐
        // 1、班级名
        String className = classService.getName(studentVO.getClassId());
        studentVO.setClassName(className);
        // 2、性别
        Integer sex = studentVO.getSex();
        String sexName = Dict.getTextByCode(Sex.class, sex);
        studentVO.setSexName(sexName);
        // 3、爱好
        Integer hobbyCode = studentVO.getHobby();
        String hobby = dictionaryService.getText("hobby", hobbyCode);
        studentVO.setHobbyName(hobby);
        // 4、小组成员和同桌，属于嵌套转换，这里比较麻烦就不写了..

        return studentVO;
    }

    /**
     * 1、使用转换插件，转换普通对象
     * （转换配置在StudentVO中，可参考StudentVO的注释）
     *
     * @link 启动项目后点击链接 <a href="http://localhost:8080/student/1">查看效果</a>
     */
    @GetMapping("/{id}")
    @Transform
    public StudentVO getStudent(@PathVariable Long id) {
        return studentConvert.toVo(studentService.getById(id));
    }

    /**
     * 2、使用转换插件，转换List
     *
     * @link 启动项目后点击链接 <a href="http://localhost:8080/student/list/100">查看效果</a>
     */
    @GetMapping("/list/{number}")
    @Transform
    public List<StudentVO> getStudentForList(@PathVariable Integer number) {
        // 示例代码，实际情况下应从db获取
        List<StudentVO> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            list.add(studentConvert.toVo(studentService.getById((long) i)));
        }
        // 同桌和小组成员，用来测试嵌套转换功能
        list.get(0).setDeskmate(list.get(1));

        return list;
    }

    /**
     * 3、转换插件支持Page包装类
     * Page解包器由插件扩展包-mybatisPlus扩展包提供，也可以自定义
     *
     * @link 启动项目后点击链接 <a href="http://localhost:8080/student/page">查看效果</a>
     * @see TransformExtendForMyBatisPlusAutoConfiguration Page解包器示例
     */
    @GetMapping("/page")
    @Transform
    public IPage<StudentVO> getStudentPage() {
        return new Page<StudentVO>().setRecords(getStudentForList(100));
    }

    /**
     * 4、转换插件支持自定义包装类
     * 解包器在项目实际使用时需自己实现
     *
     * @link 启动项目后点击链接 <a href="http://localhost:8080/student/wrapper">查看效果</a> 
     * @see TransformConfig.ResultUnWrapper 自定义解包器示例
     */
    @GetMapping("/wrapper")
    @Transform
    public ResultWrapper<StudentVO> getStudentWrapper() {
        return ResultWrapper.success(getStudent(1L));
    }

}
