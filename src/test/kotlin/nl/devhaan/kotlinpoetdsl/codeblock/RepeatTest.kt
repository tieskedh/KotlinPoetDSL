package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.endControlFlow
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock
import nl.devhaan.kotlinpoetdsl.println

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
                .endControlFlow(" while (i < 10)")
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
                .endControlFlow(" while (true)")
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

    "lazy repeat"{
        createCodeBlock {
            statement("var i = 1")
            switch("i") {
                "0" then forEach("i in 0 until 10"){
                    statement("println(i)")
                }
                "1" then repeatWhile("i < 20"){
                    statement("println(i)")
                    statement("i++")
                }
                "2" then doRepeat {
                    statement("println(2)")
                }.forEver()
                Else(repeat(3){
                    statement("println(3)")
                })
            }
        }.toString() shouldBe
                """|var i = 1
                   |when(i) {
                   |    0 -> for (i in 0 until 10) {
                   |        println(i)
                   |    }
                   |    1 -> while (i < 20) {
                   |        println(i)
                   |        i++
                   |    }
                   |    2 -> do {
                   |        println(2)
                   |    } while (true)
                   |    else -> repeat(3) {
                   |        println(3)
                   |    }
                   |}
                   |""".trimMargin()
    }
})