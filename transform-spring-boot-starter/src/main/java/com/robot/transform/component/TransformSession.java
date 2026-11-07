package com.robot.transform.component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 转换会话（线程级别）
 * <p>
 * 适用于大数据量分页导出等需要多次调用 TransformUtil#transform 的场景：
 * 会话期间转换结果缓存不会在每次转换结束后被清空，多次转换之间共享缓存，
 * 相同的原始值（如字典code、枚举值）只触发一次实际转换（如一次字典查询、一次远程调用），
 * 会话关闭时统一清空缓存。
 * <p>
 * 推荐配合try-with-resources使用，避免忘记关闭导致缓存泄漏到线程池中的后续任务：
 * <pre>{@code
 * try (TransformSession session = TransformUtil.openSession()) {
 *     while (!(page = studentService.page(pageNum++, 1000)).isEmpty()) {
 *         TransformUtil.transform(page);
 *         excelWriter.write(page);
 *     }
 * }
 * }</pre>
 * <p>
 * 注意：会话基于ThreadLocal，仅当前线程内生效，并行分页导出时每个工作线程应各自开启会话；
 * 支持嵌套开启，最外层会话关闭时才真正清空缓存
 *
 * @author R
 * @since 2026-11-6
 */
public class TransformSession implements AutoCloseable {

    private static final ThreadLocal<TransformSession> HOLDER = new ThreadLocal<>();

    /**
     * 嵌套会话计数：最外层会话创建时为1，每嵌套开启一次+1，每关闭一次-1，归零（最外层关闭）时真正清空缓存
     */
    private final AtomicInteger depth = new AtomicInteger(1);

    /**
     * 会话期间执行过转换的转换类，close时统一清空其结果缓存
     */
    private final Set<TransformClass> transformClasses = ConcurrentHashMap.newKeySet();

    private TransformSession() {
    }

    /**
     * 开启会话，若当前线程已存在会话则复用该会话（支持嵌套）
     *
     * @return 会话
     */
    public static TransformSession open() {
        TransformSession current = HOLDER.get();
        if (current != null) {
            current.depth.incrementAndGet();
            return current;
        }
        current = new TransformSession();
        HOLDER.set(current);
        return current;
    }

    /**
     * 当前线程处于会话中时，登记本次转换涉及的转换类（延迟到会话关闭时统一清缓存），并返回true
     *
     * @param transformClasses 本次转换涉及的转换类
     * @return 是否处于会话中
     */
    public static boolean recordIfActive(Collection<TransformClass> transformClasses) {
        TransformSession current = HOLDER.get();
        if (current == null) {
            return false;
        }
        current.transformClasses.addAll(transformClasses);
        return true;
    }

    /**
     * 关闭会话，最外层会话关闭时统一清空本次会话累计的转换结果缓存
     */
    @Override
    public void close() {
        if (depth.decrementAndGet() == 0) {
            try {
                transformClasses.forEach(TransformClass::clearCache);
            } finally {
                transformClasses.clear();
                HOLDER.remove();
            }
        }
    }
}
