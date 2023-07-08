package com.robot.framework.demo.service.convert;


import com.robot.framework.demo.bean.Student;
import com.robot.framework.demo.bean.StudentVO;
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
