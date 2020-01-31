package repro.api;

import java.util.concurrent.Callable;

public interface ProviderFactory {

    <T> Provider<T> provider(Callable<? extends T> value);
}
