package repro.dsl

import repro.api.Provider
import repro.api.TaskContainer


inline operator fun TaskContainer.invoke(
    configuration: TaskContainer.() -> Unit
): TaskContainer = apply(configuration)


inline fun <reified T : TaskContainer.Task> TaskContainer.register(
    name: String,
    noinline configuration: T.() -> Unit
): Provider<T> = register(name, T::class.java, configuration)
