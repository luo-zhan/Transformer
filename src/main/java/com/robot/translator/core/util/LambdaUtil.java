package com.robot.translator.core.util;

import java.util.function.*;

/**
 * Lambda异常处理工具类
 *
 * @author luozhan
 * @date 2019-05
 * @link <a>https://github.com/Robot-L/LambdaExceptionUtil</a>觉得不错的话请给个Star，thx! :D
 */
public final class LambdaUtil {

    /**
     * 包装普通函数（Function）
     * <p>
     * 注：方法签名中的" throws E "编译器会提示多余，但其实是为了将具体的异常向外传递，如果不抛出的话：
     * 1.外层代码中编译器将无法提示有异常需要处理
     * 2.也无法主动在外层捕获具体的异常，如果尝试try一个具体的异常，编译器将提示：在try语句体中永远不会抛出相应异常（Exception 'XXX' is never thrown in the corresponding try block）
     */
    public static <T, R, E extends Exception> Function<T, R> wrapFunction(FunctionWithExceptions<T, R, E> function) throws E {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * 包装双入参普通函数（BiFunction）
     */
    public static <T, U, R, E extends Exception> BiFunction<T, U, R> wrapBiFunction(BiFunctionWithExceptions<T, U, R, E> biFunction) throws E {
        return (t, u) -> {
            try {
                return biFunction.apply(t, u);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * 包装消费函数（Consumer）
     */
    public static <T, E extends Exception> Consumer<T> wrapConsumer(ConsumerWithExceptions<T, E> consumer) throws E {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * 包装双重消费函数（BiConsumer）
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> wrapBiConsumer(BiConsumerWithExceptions<T, U, E> biConsumer) throws E {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
        };
    }

    /**
     * 包装生产函数（Supplier）
     */
    public static <T, E extends Exception> Supplier<T> wrapSupplier(SupplierWithExceptions<T, E> function) throws E {
        return () -> {
            try {
                return function.get();
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return null;
            }
        };
    }

    /**
     * 包装条件函数（Predicate）
     */
    public static <T, E extends Exception> Predicate<T> wrapPredicate(PredicateWithExceptions<T, E> predicate) throws E {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return false;
            }
        };
    }

    /**
     * 包装双入参条件函数（BiPredicate）
     */
    public static <T, U, E extends Exception> BiPredicate<T, U> wrapBiPredicate(BiPredicateWithExceptions<T, U, E> predicate) throws E {
        return (t, u) -> {
            try {
                return predicate.test(t, u);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
                return false;
            }
        };
    }

    /**
     * 包装纯执行函数（Runnable）
     */
    public static <E extends Exception> void wrapRunnable(RunnableWithExceptions<E> runnable) throws E {
        try {
            runnable.run();
        } catch (Exception exception) {
            throwAsUnchecked(exception);
        }
    }

    /**
     * 如果一个方法绝对不会抛出所申明的异常，可以使用该方法进行包装
     * 如：new String(byteArr, "UTF-8")申明了UnsupportedEncodingException，
     * 但编码"UTF-8"是必定不会抛异常的，所以可以使用uncheck()进行包装
     * String text = uncheck(() -> new String(byteArr, "UTF-8"))
     */
    public static <R, E extends Exception> R uncheck(SupplierWithExceptions<R, E> supplier) {
        try {
            return supplier.get();
        } catch (Exception exception) {
            throwAsUnchecked(exception);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

    @FunctionalInterface
    public interface ConsumerWithExceptions<T, E extends Exception> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface BiConsumerWithExceptions<T, U, E extends Exception> {
        void accept(T t, U u) throws E;
    }


    @FunctionalInterface
    public interface FunctionWithExceptions<T, R, E extends Exception> {
        static <T> FunctionWithExceptions<T, T, Exception> identity() {
            return t -> t;
        }

        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface BiFunctionWithExceptions<T, U, R, E extends Exception> {
        R apply(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface SupplierWithExceptions<T, E extends Exception> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface RunnableWithExceptions<E extends Exception> {
        void run() throws E;
    }

    @FunctionalInterface
    public interface PredicateWithExceptions<T, E extends Exception> {
        boolean test(T t) throws E;
    }

    @FunctionalInterface
    public interface BiPredicateWithExceptions<T, U, E extends Exception> {
        boolean test(T t, U u) throws E;
    }

}