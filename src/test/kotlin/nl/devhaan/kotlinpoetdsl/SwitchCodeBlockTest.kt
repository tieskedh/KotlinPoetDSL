package nl.devhaan.kotlinpoetdsl

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.codeblock.switch
import nl.devhaan.kotlinpoetdsl.functions.createFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.functions.returns

/**
 * @Todo Switch is now "manually" implemented, update when KotlinPoet provide better support
 */
class SwitchCodeBlockTest : StringSpec({
    "switch with s_case"{
        createFun {
            func("switch") {
                switch("val a = Random.nextInt(10)") {
                    "a < 5" then "println(\"little\")"
                    "a == 5" then {
                        "println(%S)".argStatement("center")
                    }
                    "a > 5".then("println(%S)", "big")
                }
            }
        }.toString() shouldBe
                """|fun switch() {
                   |    when(val a = Random.nextInt(10)) {
                   |        a < 5 -> {
                   |            println("little")
                   |        }
                   |        a == 5 -> {
                   |            println("center")
                   |        }
                   |        a > 5 -> {
                   |            println("big")
                   |        }
                   |    }
                   |}
                   |""".trimMargin()
    }

    "nested switch"{

        createFun {
            Int::class.func("switch", "b" of Int::class, "shouldBe" of String::class) returns Boolean::class{
                switch("shouldBe") {
                    "bigger".S() then switch{
                        "this <= b" then "false"
                        Else("true")
                    }

                    "smaller".S() then switch {
                        "this >= b" then "false"
                        Else { statement("true") }
                    }

                    Else("false")
                }
            }
        }.toString() shouldBe
                """|fun kotlin.Int.switch(b: kotlin.Int, shouldBe: kotlin.String): kotlin.Boolean {
                   |    when(shouldBe) {
                   |        "bigger" -> {
                   |            when {
                   |                this <= b -> {
                   |                    false
                   |                }
                   |                else -> {
                   |                    true
                   |                }
                   |            }
                   |        }
                   |        "smaller" -> {
                   |            when {
                   |                this >= b -> {
                   |                    false
                   |                }
                   |                else -> {
                   |                    true
                   |                }
                   |            }
                   |        }
                   |        else -> {
                   |            false
                   |        }
                   |    }
                   |}
                   |""".trimMargin()
    }
})
