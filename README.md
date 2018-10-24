[![](https://jitpack.io/v/nl.devhaan/KotlinPoetDSL.svg)](https://jitpack.io/#nl.devhaan/KotlinPoetDSL)


# KotlinPoetDSL
KotlinPoetDSL provides a dsl for KotlinPoet.

At this moment, it's still a playground!!!

So, unless you're really adventurous, don't use in production! 

## Quickstart

To get a Git project into your build:

1. Add the JitPack repository to your build file

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. Add the dependency
```
dependencies {
        implementation 'nl.devhaan:KotlinPoetDSL:0.1.0'
}
```

## Example
```kotlin
file("", "HelloWorld") {

    internal.clazz("Greeter", "name".valOf<String>("\"UNKNOWN\"")) {

        open.func("greet") returns Boolean::class{
            statement("println(%S)", "Hello, \$name")
            statement("return %L", true)
        }

    }

    public.func("main", "args" vararg String::class) {
        If("args.size>0") {
            statement("%T(args[0]).greet()", ClassName("", "Greeter"))
        } orElse {
            repeat(3) {
                statement("println(%S)", "DONT FORGET TO PASS AN ARGUMENT!!!")
            }
        }
    }

}.writeTo(System.out)
```

maps to

```kotlin
import kotlin.Boolean
import kotlin.String

internal class Greeter(val name: String = "UNKOWN") {
  open fun greet(): Boolean {
    println("Hello, $name")
    return true
  }
}

fun main(vararg args: String) {
  if(args.size>0) {
    Greeter(args[0]).greet()
  } else {
    repeat(3) {
      println("DONT FORGET TO PASS AN ARGUMENT!!!")
    }
  }
}
```
