package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock

class RepeatTest : StringSpec({
    "foreach"{
        createCodeBlock {
            statement("var total = 0")
            forEach("i in 0 until 10") {
                statement("total += i")
            }
        } shouldBe CodeBlock.builder()
                .addStatement("var total = 0")
                .beginControlFlow("for (i in 0 until 10)")
                .addStatement("total += i")
                .endControlFlow()
                .build()
    }


    "doRepeat asLongAs"{
        createCodeBlock {
            statement("var total = 0")
            statement("var i = 0")
            doRepeat {
                statement("total += i")
                statement("i++")
            } asLongAs "i < 10"
        } shouldBe CodeBlock.builder()
                .addStatement("var total = 0")
                .addStatement("var i = 0")
                .beginControlFlow("do")
                .addStatement("total += i")
                .addStatement("i++")
                .endControlFlow("while (i < 10)")
                .build()
    }

    "doRepeat forever"{
        createCodeBlock {
            statement("var total = 0")
            statement("var i = 0")
            doRepeat {
                statement("total += i")
                statement("i++")
            }.forEver()
        } shouldBe CodeBlock.builder()
                .addStatement("var total = 0")
                .addStatement("var i = 0")
                .beginControlFlow("do")
                .addStatement("total += i")
                .addStatement("i++")
                .endControlFlow("while (true)")
                .build()
    }

    "repeatWhile"{
        createCodeBlock {
            statement("var total = 0")
            statement("var i = 0")
            repeatWhile("i < 10") {
                statement("total += i")
                statement("i++")
            }
        } shouldBe buildCodeBlock {
            addStatement("var total = 0")
            addStatement("var i = 0")
            beginControlFlow("while (i < 10)")
            addStatement("total += i")
            addStatement("i++")
            endControlFlow()
        }
    }

    "repeat with it"{
        createCodeBlock {
            repeat(5) {
                statement("println(it)")
            }
        } shouldBe buildCodeBlock {
            beginControlFlow("repeat(5)")
            addStatement("println(it)")
            endControlFlow()
        }
    }

    "repeat with argname"{
        createCodeBlock {
            repeat(5, "_") {
                statement("println(it)")
            }
        } shouldBe buildCodeBlock {
            beginControlFlow("repeat(5) { _ ->")
            addStatement("println(it)")
            endControlFlow()
        }
    }
})