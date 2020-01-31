package repro.api;

public interface TaskContainer {

    interface Task {
    }

    <T extends Task> Provider<T> register(
            String name,
            Class<T> taskType,
            Action<? super T> action
    );
}
