# KotlinPoetDSL
KotlinPoetDSL provides a dsl for KotlinPoet.
On this moment, it's still in development, so is most of the code not evaluated jet.


## Quickstart
Example
```kotlin
file("", "HelloWorld"){
  internal.clazz("Greeter", "name" valOf  String::class){
    open.func("greet") returns (Boolean::class){
      statement("println(%S)", "Hello, \$name")
      statement("returns %L", true)
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
```

maps to

```kotlin
import kotlin.Boolean
import kotlin.String

internal class Greeter(val name: String) {
  open fun greet(): Boolean {
    println("Hello, $name")
    returns true
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
