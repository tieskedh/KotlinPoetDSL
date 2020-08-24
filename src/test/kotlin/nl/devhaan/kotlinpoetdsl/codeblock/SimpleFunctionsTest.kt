package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.buildCodeBlock
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock


/**
 * Some functions are added inside CodeBlockBuilder themselves, as they are to easy.
 * Here we test them.
 */
class SimpleFunctionsTest : StringSpec({
    "statements"{
        createCodeBlock {
            statement("println(0)")
            "println(1)".statement()
            "println(%S)".statement("Hi")
        } shouldBe buildCodeBlock {
            addStatement("println(0)")
            addStatement("println(1)")
            addStatement("println(%S)", "Hi")
        }
    }

    "trimmedCode"{
        createCodeBlock {
            """
                println(0)
                println(1)
                println(%S)
            """.addTrimmedCode("Hi")
        } shouldBe buildCodeBlock {
            add("""
                println(0)
                println(1)
                println(%S)
            """.trimIndent(), "Hi")
        }
    }

    "margined code"{
        createCodeBlock {
            """
            |println(0)
            |println(1)
            |println(%S)
            |""".addMarginedCode("Hi")
        } shouldBe buildCodeBlock {
            add("""
                |println(0)
                |println(1)
                |println(%S)
                |""".trimMargin(), "Hi")
        }
    }

    "normal code"{
        createCodeBlock {
            addCode("println(0)")
            "println(%S)".addCode("hi")
        } shouldBe buildCodeBlock {
            add("println(0)")
            add("println(%S)", "hi")
        }
    }

})