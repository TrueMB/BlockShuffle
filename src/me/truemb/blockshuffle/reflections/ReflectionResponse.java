package me.truemb.blockshuffle.reflections;

public class ReflectionResponse<T> {

    private T object;

    public ReflectionResponse(T object) {
        this.object = object;
    }


    public boolean isValid() {
        return object != null;
    }


    public T get() {
        return object;
    }
}
