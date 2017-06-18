# KotlinPoetDSL
KotlinPoetDSL provides a dsl for KotlinPoet.
On this moment, it's still in development, so is most of the code not evaluated jet.


## Quickstart
Example
```kotlin
fun main(args: Array<String>) {
    file("", "HelloWorld"){
        internal.clazz("Greeter", "name" of String::class){
            open.func("greet"){
                statement("println(%S)", "Hello, \$name")
            }
        }

        public.func("main", "args" vararg String::class) {
            If("args.size>0") {
                statement("%T(args[0]).greet()", ClassName("", "Greeter"))
            } orElse {
                repeat(3){
                    statement("println(%S)", "DONT FORGET TO PASS AN ARGUMENT!!!")
                }
            }
        }
    }.writeTo(System.out)
}
```

maps to

```kotlin
import kotlin.String

internal class Greeter(name: String) {
  open fun greet() {
    println("Hello, $name")
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
