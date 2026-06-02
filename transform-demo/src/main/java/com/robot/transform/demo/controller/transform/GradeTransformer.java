package com.robot.transform.demo.controller.transform;

import com.robot.transform.transformer.BatchTransformer;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量转换测试转换器
 * 将年级code批量转换为年级名称，并记录批量调用次数以验证批量路径生效
 *
 * @author R
 */
@Component
public class GradeTransformer implements BatchTransformer<Integer, TransformGrade> {

    private final AtomicInteger batchCallCount = new AtomicInteger(0);

    @NotNull
    @Override
    public Map<Integer, String> batchTransform(@NonNull Set<Integer> originalValues, TransformGrade annotation) {
        batchCallCount.incrementAndGet();
        Map<Integer, String> result = new HashMap<>();
        for (Integer value : originalValues) {
            if (value != null) {
                result.put(value, " grade " + value);
            }
        }
        return result;
    }

    public int getBatchCallCount() {
        return batchCallCount.get();
    }

    public void resetBatchCallCount() {
        batchCallCount.set(0);
    }
}
