# Repro project for Kotlin compiler bug [KT-36297](https://youtrack.jetbrains.com/issue/KT-36297)

When `-XXLanguage:+NewInference` is set, the Kotlin compiler emits a reference to a
nonexisting class for code with nested inline lambdas.

This prevents the type of the generated `Callable` to be inspected via reflection at runtime.

To reproduce the bug simply run the provided app (or the tests):
```
$ ./kotlin-dsl-inline-repro » ./gradlew :app:run -q                                                               1 ↵
Exception in thread "main" java.lang.NoClassDefFoundError: repro/AppKt$script$1$1
        at java.base/java.lang.Class.getEnclosingMethod0(Native Method)
        at java.base/java.lang.Class.getEnclosingMethodInfo(Class.java:1303)
        at java.base/java.lang.Class.getEnclosingMethod(Class.java:1249)
        at java.base/sun.reflect.generics.scope.ClassScope.computeEnclosingScope(ClassScope.java:50)
        at java.base/sun.reflect.generics.scope.AbstractScope.getEnclosingScope(AbstractScope.java:77)
        at java.base/sun.reflect.generics.scope.AbstractScope.lookup(AbstractScope.java:95)
        at java.base/sun.reflect.generics.factory.CoreReflectionFactory.findTypeVariable(CoreReflectionFactory.java:110)
        at java.base/sun.reflect.generics.visitor.Reifier.visitTypeVariableSignature(Reifier.java:165)
        at java.base/sun.reflect.generics.tree.TypeVariableSignature.accept(TypeVariableSignature.java:43)
        at java.base/sun.reflect.generics.visitor.Reifier.reifyTypeArguments(Reifier.java:68)
        at java.base/sun.reflect.generics.visitor.Reifier.visitClassTypeSignature(Reifier.java:138)
        at java.base/sun.reflect.generics.tree.ClassTypeSignature.accept(ClassTypeSignature.java:49)
        at java.base/sun.reflect.generics.repository.ClassRepository.computeSuperInterfaces(ClassRepository.java:117)
        at java.base/sun.reflect.generics.repository.ClassRepository.getSuperInterfaces(ClassRepository.java:95)
        at java.base/java.lang.Class.getGenericInterfaces(Class.java:1137)
        at repro.DummyProject.provider(App.kt:42)
        at repro.AppKt$script$$inlined$invoke$lambda$1.invoke(App.kt:23)
        at repro.AppKt$script$$inlined$invoke$lambda$1.invoke(App.kt)
        at repro.AppKt$inlined$sam$i$repro_api_Action$0.invoke(TaskContainerExtensions.kt)
        at repro.DummyProject.register(App.kt:37)
        at repro.AppKt.script(App.kt:60)
        at repro.AppKt.printInferredProviderType(App.kt:17)
        at repro.AppKt.main(App.kt:13)
        at repro.AppKt.main(App.kt)
Caused by: java.lang.ClassNotFoundException: repro.AppKt$script$1$1
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:581)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
        ... 24 more

FAILURE: Build failed with an exception.
```

Or run the tests:
```
$ ./kotlin-dsl-inline-repro » ./gradlew check 
    
> Task :app:test FAILED

repro.ReproTest > provider type is inferred from Callable generic interface argument FAILED
    java.lang.NoClassDefFoundError at ReproTest.kt:28
        Caused by: java.lang.ClassNotFoundException at ReproTest.kt:28

1 test completed, 1 failed

FAILURE: Build failed with an exception.
```

The failure stacktrace should be basically the same as before:
```
java.lang.NoClassDefFoundError: repro/AppKt$script$1$1
	at java.base/java.lang.Class.getEnclosingMethod0(Native Method)
	at java.base/java.lang.Class.getEnclosingMethodInfo(Class.java:1303)
	at java.base/java.lang.Class.getEnclosingMethod(Class.java:1249)
	at java.base/sun.reflect.generics.scope.ClassScope.computeEnclosingScope(ClassScope.java:50)
	at java.base/sun.reflect.generics.scope.AbstractScope.getEnclosingScope(AbstractScope.java:77)
	at java.base/sun.reflect.generics.scope.AbstractScope.lookup(AbstractScope.java:95)
	at java.base/sun.reflect.generics.factory.CoreReflectionFactory.findTypeVariable(CoreReflectionFactory.java:110)
	at java.base/sun.reflect.generics.visitor.Reifier.visitTypeVariableSignature(Reifier.java:165)
	at java.base/sun.reflect.generics.tree.TypeVariableSignature.accept(TypeVariableSignature.java:43)
	at java.base/sun.reflect.generics.visitor.Reifier.reifyTypeArguments(Reifier.java:68)
	at java.base/sun.reflect.generics.visitor.Reifier.visitClassTypeSignature(Reifier.java:138)
	at java.base/sun.reflect.generics.tree.ClassTypeSignature.accept(ClassTypeSignature.java:49)
	at java.base/sun.reflect.generics.repository.ClassRepository.computeSuperInterfaces(ClassRepository.java:117)
	at java.base/sun.reflect.generics.repository.ClassRepository.getSuperInterfaces(ClassRepository.java:95)
	at java.base/java.lang.Class.getGenericInterfaces(Class.java:1137)
	at repro.DummyProject.provider(App.kt:42)
```

## Structure

To better match the issue affecting Gradle users, this project replicates the involved Gradle types and DSL structure.

* `api` contains a simplified version of the Java Gradle API required to reproduce the bug
* `dsl` contains the two inline Kotlin extensions required to reproduce the bug
* `app` puts together the dsl and api to reproduce the bug