---
description: Welcome to the KotlinPoetDSL-guide.
---

# Welcome

## The goal of KotlinPoetDSL

The main goal of KotlinPoetDSL is to generate code, with code like the generated code.

The main way to do this is by providing dumb wrappers around KotlinPoet.   
There are however some smart features that KotlinPoet doesn't have \(yet\), like constructorSpec, which adds val- or var-propperties straight away.

The best way to demonstrate this is using an example.

### Code to generate.

Let's say we want to generate the follolwing code:

```kotlin
import kotlin.String

class Greeter(val name: String = "You") {
    inline fun greet() {
        println("Hello, $name")
    }
}

fun main(vararg args: String) {
    Greeter(args[0]).greet()
}
```

### Using KotlinPoet

In KotlinPoet we use the following code:

```kotlin
val greeterClass = ClassName("", "Greeter")
    FileSpec.builder("", "HelloWorld")
        .addType(TypeSpec.classBuilder("Greeter")
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder("name", String::class)
                    .defaultValue("\"you\"")
                    .build())
                .build())
            .addProperty(PropertySpec.builder("name", String::class)
                .initializer("name")
                .build())
            .addFunction(FunSpec.builder("greet")
                .addModifiers(KModifier.INLINE)
                .addStatement("println(%S)", "Hello, \$name")
                .build())
            .build())
        .addFunction(FunSpec.builder("main")
            .addParameter("args", String::class, KModifier.VARARG)
            .addStatement("%T(args[0]).greet()", greeterClass)
            .build())
        .build().writeTo(System.out)
```

### Using KotlinPoetDSL

The same code:

```kotlin
import kotlin.String

class Greeter(val name: String = "You") {
    inline fun greet() {
        println("Hello, $name")
    }
}

fun main(vararg args: String) {
    Greeter(args[0]).greet()
}
```

Generated using KotlinPoetDSL will look like:

```kotlin
file("", "HelloWorld") {
    val greeter = clazz("Greeter", "name".valOf<String>("You".S())) {
        inline.func("greet") {
            statement("println(%S)", "Hello, \$name")
        }
    }.packaged("")

    func("main", "args" vararg String::class) {
        statement("%T(args[0]).greet()", greeter)
    }
}.writeTo(System.out)
```

### Evaluation

As you can see, KotlinPoet is very composable and is ideal for slowly building up everything.  
When you write everything at once, it isn't very readible.

On the other hand, KotlinPoetDSL is very readible when you write everything at once.  
With DSL's it's on the other hand, not that friendly to compose together.

This means that KotlinPoetDSL does not replace KotlinPoet, but makes it stronger on a different point.  
Because KotlinPoetDSL makes use of KotlinPoet, and uses KotlinPoets type throughout the wrapper, you can easily use them together.

