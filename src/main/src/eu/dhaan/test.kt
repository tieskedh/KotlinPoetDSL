import com.squareup.kotlinpoet.ClassName
import eu.dhaan.constructs.Accessor.Companion.internal
import eu.dhaan.constructs.Accessor.Companion.open
import eu.dhaan.constructs.Accessor.Companion.public
import eu.dhaan.contexts.file

fun main(args: Array<String>) {
    file("", "HelloWorld"){
        internal.clazz("Greeter"){
            prop("name" vararg String::class){
                init("name")
            }
            open.func("greet"){
                statement("println(%S)", "Hello, \$name")
            }
        }

        public.func("main", "args" of Array<String>::class) {
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