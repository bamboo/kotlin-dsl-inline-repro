package repro.api;

public interface Project {

    TaskContainer getTasks();

    ProviderFactory getProviders();
}
