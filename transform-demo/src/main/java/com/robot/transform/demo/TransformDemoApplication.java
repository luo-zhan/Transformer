package com.robot.transform.demo;

import com.robot.transform.annotation.EnableTransform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 转换Demo
 *
 * @author R
 */
@EnableTransform
@SpringBootApplication
public class TransformDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransformDemoApplication.class, args);
    }
}