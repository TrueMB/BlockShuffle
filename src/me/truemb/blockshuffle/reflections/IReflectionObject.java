package me.truemb.blockshuffle.reflections;

public interface IReflectionObject {
    <E extends IReflectionObject> E setAccessible(boolean value);

    ReflectionUtil newCall();
}
