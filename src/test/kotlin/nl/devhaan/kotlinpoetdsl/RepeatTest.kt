package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.helpers.FuncBlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.buildCodeBlock

class RepeatTest : StringSpec({
    "foreach"{
        buildCodeBlock {
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
        buildCodeBlock {
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
        buildCodeBlock {
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
        buildCodeBlock {
            statement("var total = 0")
            statement("var i = 0")
            repeatWhile("i < 10"){
                statement("total += i")
                statement("i++")
            }
        } shouldBe CodeBlock.Builder()
                .addStatement("var total = 0")
                .addStatement("var i = 0")
                .beginControlFlow("while (i < 10)")
                .addStatement("total += i")
                .addStatement("i++")
                .endControlFlow()
                .build()
    }
})