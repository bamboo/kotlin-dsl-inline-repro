package repro.api;

public interface Action<T> {
    void invoke(T subject);
}
