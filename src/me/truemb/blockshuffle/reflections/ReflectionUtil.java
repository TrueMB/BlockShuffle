package me.truemb.blockshuffle.reflections;

public class ReflectionUtil {

    private static final ReflectionUtil REFLECTION_UTIL = new ReflectionUtil();

    private ReflectionUtil() {
    }

    public static ReflectionUtil newCall() {
        return REFLECTION_UTIL;
    }

    public ReflectionResponse<ReflectionField> getField(Class<?> holder, String fieldName) {
        try {
            return new ReflectionResponse<>(new ReflectionField(holder.getDeclaredField(fieldName)));
        } catch (NoSuchFieldException ignored) {
        }
        return new ReflectionResponse<>(null);
    }


    public ReflectionResponse<ReflectionMethod> getMethod(Class<?> holder, String method, Class<?>... parameters) {
        try {
            return new ReflectionResponse<>(new ReflectionMethod(holder.getDeclaredMethod(method, parameters)));
        } catch (NoSuchMethodException ignored) {
        }
        return new ReflectionResponse<>(null);
    }


    public ReflectionResponse<ReflectionConstructor> getConstructor(Class<?> holder, Class<?>... parameters) {
        try {
            return new ReflectionResponse<>(new ReflectionConstructor(holder.getDeclaredConstructor(parameters)));
        } catch (NoSuchMethodException ignored) {
        }
        return new ReflectionResponse<>(null);
    }
}
