package com.msb.framework.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.msb.framework.demo.entity.StudentVO;
import com.msb.framework.demo.service.ClassService;
import com.msb.framework.demo.service.DictionaryService;
import com.msb.framework.demo.service.StudentService;
import com.msb.framework.demo.service.convert.StudentConvert;
import com.robot.transform.annotation.Transform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * 学生示例接口
 *
 * @author R
 */
@RestController
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

    @GetMapping("list/{id}")
    @Transform
    public List<StudentVO> getStudentList(@PathVariable Long id) {
        StudentVO currentStudent = studentConvert.toVo(studentService.getById(id));
        StudentVO student2 = studentConvert.toVo(studentService.getById(2L));
        StudentVO student3 = studentConvert.toVo(studentService.getById(3L));
        List<StudentVO> list = Arrays.asList(student2, student3);
        currentStudent.setPartner(student2);
        currentStudent.setTeam(list);
//        // 转换逻辑
//        // 1、翻译班级名
//        String className = classService.getName(currentStudent.getClassId());
//        currentStudent.setClassName(className);
//        // 2、翻译性别
//        Integer sex = currentStudent.getSex();
//        String sexText = dictionaryService.getText("sex", sex);
//        currentStudent.setSexName(sexText);
//        ///3、翻译班干部职位
//        Integer classLeaderCode = currentStudent.getClassLeader();
//        String classLeader = IDict.getTextByCode(StudentVO.ClassLeaderEnum.class, classLeaderCode);
//        currentStudent.setClassLeaderName(classLeader);
        return Collections.singletonList(currentStudent);
    }

    @GetMapping("page/{id}")
    @Transform
    public IPage<StudentVO> getStudentPage(@PathVariable Long id) {
        return new Page<StudentVO>().setRecords(getStudentList(id));
    }

    @GetMapping("wrapper/{id}")
    @Transform
    public ResultWrapper<IPage<StudentVO>> getStudentWrapper(@PathVariable Long id) {
        return ResultWrapper.success(getStudentPage(id));
    }

}
