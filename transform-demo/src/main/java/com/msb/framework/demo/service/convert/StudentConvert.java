package com.msb.framework.demo.service.convert;


import com.msb.framework.demo.entity.Student;
import com.msb.framework.demo.entity.StudentVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentConvert {

    StudentVO toVo(Student student);


}
