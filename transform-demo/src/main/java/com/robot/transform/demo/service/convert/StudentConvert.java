package com.robot.transform.demo.service.convert;


import com.robot.transform.demo.bean.Student;
import com.robot.transform.demo.bean.StudentVO;
import org.mapstruct.Mapper;

/**
 * 对象转换器
 *
 * @author R
 */
@Mapper(componentModel = "spring")
public interface StudentConvert {

    /**
     * entity转换成vo
     *
     * @param student 学生实体
     * @return StudentVO
     */
    StudentVO toVo(Student student);


}
