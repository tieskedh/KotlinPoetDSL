package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldHave
import io.kotlintest.specs.StringSpec

class VariableTest : StringSpec({
    val clazz = ClassName("kotlin", "String")
    "unititialized without propData"{
        listOf(
                "a" of String::class,
                "a" of clazz,
                "a".of<String>() // only supported because of params
        ) allShouldHave stringValue("a: kotlin.String")
    }

    "initialized without propData"{
        listOf(
                "a".of<String>(CodeBlock.of("%S", "Hi")),
                "a".of<String>("%S", "Hi"),


                "a".of(clazz, CodeBlock.of("%S", "Hi")),
                "a".of(clazz, "%S", "Hi")
        ) allShouldHave stringValue("a: kotlin.String = \"Hi\"")
    }

    "uninitialized val"{
        listOf(
                "a" valOf String::class,
                "a" valOf clazz,
                "a".valOf<String>() // only supported because of params
        ) allShouldHave stringValue("val a: kotlin.String")
    }

    "initialized val"{
        listOf(
                "a".valOf<String>("%S", "Hi"),
                "a".valOf<String>(CodeBlock.of("%S", "Hi")),

                "a".valOf(clazz, "%S", "Hi"),
                "a".valOf(clazz, CodeBlock.of("%S", "Hi"))
        ) allShouldHave stringValue("val a: kotlin.String = \"Hi\"")
    }

    "uninitialized var"{
        listOf(
                "a" varOf String::class,
                "a" varOf clazz,
                "a".varOf<String>() // only suported because of params
        ) allShouldHave stringValue("var a: kotlin.String")
    }

    "initialized var"{
        listOf(
                "a".varOf<String>("%S", "Hi"),
                "a".varOf<String>(CodeBlock.of("%S", "Hi")),

                "a".varOf(clazz, "%S", "Hi"),
                "a".varOf(clazz, CodeBlock.of("%S", "Hi"))
        ) allShouldHave stringValue("var a: kotlin.String = \"Hi\"")
    }

    "unititialized vararg"{
        listOf(
                "a" vararg String::class,
                "a" vararg clazz,
                "a".vararg<String>() // only supported because of params
        ) allShouldHave stringValue("vararg a: kotlin.String")
    }

    "initialized vararg"{
        listOf(
                "a".vararg<String>(CodeBlock.of("arrayOf(%S)", "Hi")),
                "a".vararg<String>("arrayOf(%S)", "Hi"),

                "a".vararg(clazz, CodeBlock.of("arrayOf(%S)", "Hi")),
                "a".vararg(clazz, "arrayOf(%S)", "Hi")
        ) allShouldHave stringValue("vararg a: kotlin.String = arrayOf(\"Hi\")")
    }

    "uninitialized varargVal"{
        listOf(
                "a" varargVal String::class,
                "a" varargVal clazz,
                "a".varargVal<String>() // only supported because of params
        ) allShouldHave stringValue("vararg val a: kotlin.String")
    }

    "initialized varargVal"{
        listOf(
                "a".varargVal<String>("arrayOf(%S)", "Hi"),
                "a".varargVal<String>(CodeBlock.of("arrayOf(%S)", "Hi")),

                "a".varargVal(clazz, "arrayOf(%S)", "Hi"),
                "a".varargVal(clazz, CodeBlock.of("arrayOf(%S)", "Hi"))
        ) allShouldHave stringValue("vararg val a: kotlin.String = arrayOf(\"Hi\")")
    }
    "uninitialized varargVar"{
        listOf(
                "a" varargVar String::class,
                "a" varargVar clazz,
                "a".varargVar<String>() // only supported because of params
        ) allShouldHave stringValue("vararg var a: kotlin.String")
    }

    "initialized varargVar"{
        listOf(
                "a".varargVar<String>("arrayOf(%S)", "Hi"),
                "a".varargVar<String>(CodeBlock.of("arrayOf(%S)", "Hi")),

                "a".varargVar(clazz, "arrayOf(%S)", "Hi"),
                "a".varargVar(clazz, CodeBlock.of("arrayOf(%S)", "Hi"))
        ) allShouldHave stringValue("vararg var a: kotlin.String = arrayOf(\"Hi\")")
    }
})


infix fun <T> List<T>.allShouldHave(matcher: Matcher<T>) = forAll { it shouldHave matcher }
fun stringValue(stringValue: String) = object : Matcher<Any> {
    override fun test(value: Any) = Result(value.toString() == stringValue,
            """String "$value" should be "$stringValue".""",
            "String $value should not be $stringValue")
}