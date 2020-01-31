package repro

import repro.api.Action
import repro.dsl.*
import repro.api.Project
import repro.api.Provider
import repro.api.ProviderFactory
import repro.api.TaskContainer
import java.lang.reflect.ParameterizedType
import java.util.concurrent.Callable

fun main() {
    printInferredProviderType()
}

fun printInferredProviderType() {
    DummyProject().script()
}

fun Project.script() {
    tasks {
        register<Broken>("broken") {
            providers.provider { "abc" }
        }
    }
}

class Broken : TaskContainer.Task

class DummyProject : Project, TaskContainer, ProviderFactory {

    override fun getTasks(): TaskContainer = this

    override fun getProviders(): ProviderFactory = this

    override fun <T : TaskContainer.Task> register(name: String, taskType: Class<T>, action: Action<in T>): Provider<T> {
        action.invoke(taskType.getConstructor().newInstance())
        return DefaultProvider()
    }

    override fun <T : Any> provider(value: Callable<out T>): Provider<T> {
        val inferredType = value.javaClass
            .genericInterfaces
            .filterIsInstance<ParameterizedType>()
            .firstOrNull { it.rawType == Callable::class.java }
            ?.let { it.actualTypeArguments[0] }
        println(inferredType)
        return DefaultProvider()
    }

    class DefaultProvider<T> : Provider<T>
}
