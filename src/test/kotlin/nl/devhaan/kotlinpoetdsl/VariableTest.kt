package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.ClassName
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
                "a".of<String>()
        ) allShouldHave stringValue("a: kotlin.String")
    }

    "initialized without propData"{
        listOf(
                "a".of<String>("%S", "Hi"),
                "a".of(clazz, "%S", "Hi")
        ) allShouldHave stringValue("a: kotlin.String = \"Hi\"")
    }

    "uninitialized val"{
        listOf(
                "a" valOf String::class,
                "a" valOf clazz,
                "a".valOf<String>()
        ) allShouldHave stringValue("val a: kotlin.String")
    }

    "initialized val"{
        listOf(
                "a".valOf<String>("%S", "Hi"),
                "a".valOf(clazz, "%S", "Hi")
        ) allShouldHave stringValue("val a: kotlin.String = \"Hi\"")
    }

    "uninitialized var"{
        listOf(
                "a" varOf String::class,
                "a" varOf clazz,
                "a".varOf<String>()
        ) allShouldHave stringValue("var a: kotlin.String")
    }

    "initialized var"{
        listOf(
                "a".varOf<String>("%S", "Hi"),
                "a".varOf(clazz, "%S", "Hi")
        ) allShouldHave stringValue("var a: kotlin.String = \"Hi\"")
    }
})


infix fun <T> List<T>.allShouldHave(matcher: Matcher<T>) = forAll { it shouldHave matcher }
fun stringValue(stringValue: String) = object : Matcher<Any> {
    override fun test(value: Any) = Result(value.toString() == stringValue,
            """String "$value" should be "$stringValue".""",
            "String $value should not be $stringValue")
}