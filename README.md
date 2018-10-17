# KotlinPoetDSL
KotlinPoetDSL provides a dsl for KotlinPoet.
AT THIS MOMENT, IT'S STILL A PLAYGROUND!!!
SO DON'T USE IN PRODUCTION.

## Quickstart
Example
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
