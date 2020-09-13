package utils;

import java.util.function.Consumer;

// Magic ✨
// See: https://dzone.com/articles/how-to-handle-checked-exception-in-lambda-expressi
public interface ThrowingConsumer<T, R, E extends Throwable> {
    void accept(T t, R r) throws E;
}
