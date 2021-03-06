package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.buildCodeBlock
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import nl.devhaan.kotlinpoetdsl.S
import nl.devhaan.kotlinpoetdsl.endControlFlow
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.createFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.functions.returns
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock
import nl.devhaan.kotlinpoetdsl.of

/**
 * @Todo Switch is now "manually" implemented, update when KotlinPoet provide better support
 */
class SwitchTest : StringSpec({
    "switch with s_case"{
        createFun {
            func("switch") {
                switch("val a = Random.nextInt(10)") {
                    "a < 5" then "println(\"little\")"
                    "a == 5" then {
                        "println(%S)".statement("center")
                    }
                    "a > 5".then("println(%S)", "big")
                }
            }
        }.toString() shouldBe
                """|fun switch() {
                   |  when(val a = Random.nextInt(10)) {
                   |    a < 5 -> println("little")
                   |    a == 5 -> println("center")
                   |    a > 5 -> println("big")
                   |  }
                   |}
                   |""".trimMargin()
    }

    "switch with singleline multistatement case"{
        createCodeBlock {
            switch {
                "3" then "println(1); println(1)"
            }
        } shouldBe buildCodeBlock {
            beginControlFlow("when")
            beginControlFlow("3 ->")
            addStatement("println(1); println(1)")
            endControlFlow()
            endControlFlow()
        }
    }

    "switch with multiline case"{
        createFun {
            func("switch") {
                switch("val a = Random.nextInt(10)") {
                    "a < 5" then "println(\"little\")"
                    "a == 5" then {
                        "println(%S)".statement("center")
                        "println(%S)".statement("center")
                    }
                    "a > 5".then("println(%S)", "big")
                }
            }
        }.toString() shouldBe
                """|fun switch() {
                   |  when(val a = Random.nextInt(10)) {
                   |    a < 5 -> println("little")
                   |    a == 5 -> {
                   |      println("center")
                   |      println("center")
                   |    }
                   |    a > 5 -> println("big")
                   |  }
                   |}
                   |""".trimMargin()
    }


    "importTest"{
        file("", "HelloWorld") {
            func("a") {
                switch {
                    "bigger" then {
                        statement("%T()", String::class)
                    }
                    Else {
                        statement("%T()", String::class)
                    }
                }
            }
        } shouldBe FileSpec.builder("", "HelloWorld").addFunction(
                FunSpec.builder("a")
                        .beginControlFlow("when")
                        .addStatement("bigger -> %T()", String::class)
                        .addStatement("else -> %T()", String::class)
                        .endControlFlow()
                        .build()
        ).build()
    }

    "pre- and post switch"{
        createCodeBlock {
            switch("1", prefix = "println(", postFix = ")") {
                "0" then "1"
                "1" then "0"
            }
        } shouldBe buildCodeBlock {
            beginControlFlow("println(when(1) {")
            addStatement("0 -> 1")
            addStatement("1 -> 0")
            endControlFlow(")")
        }
    }


    "nested switch"{

        createFun {
            Int::class.func("switch", "b" of Int::class, "shouldBe" of String::class) returns Boolean::class{
                switch("shouldBe") {
                    "bigger".S() then switch {
                        "this <= b" then "false"
                        Else("true")
                    }

                    "smaller".S() then switch {
                        "this >= b" then "false"
                        Else { statement("true") }
                    }

                    this Else switch {
                        "this >= b" then "false"
                        Else { statement("true") }
                    }
                }
            }
        }.toString() shouldBe
                """|fun kotlin.Int.switch(b: kotlin.Int, shouldBe: kotlin.String): kotlin.Boolean {
                   |  when(shouldBe) {
                   |    "bigger" -> when {
                   |      this <= b -> false
                   |      else -> true
                   |    }
                   |    "smaller" -> when {
                   |      this >= b -> false
                   |      else -> true
                   |    }
                   |    else -> when {
                   |      this >= b -> false
                   |      else -> true
                   |    }
                   |  }
                   |}
                   |""".trimMargin()
    }
})
