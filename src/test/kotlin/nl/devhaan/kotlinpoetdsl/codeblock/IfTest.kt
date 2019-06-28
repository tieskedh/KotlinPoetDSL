package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asTypeName
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.functions.createFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.helpers.UnFinishException
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock
import nl.devhaan.kotlinpoetdsl.of
import nl.devhaan.kotlinpoetdsl.println

class IfTest : StringSpec({
    val nullableBool = ParameterSpec.builder("bool", String::class.asTypeName().copy(nullable = false)).build()

    "if end"{
        createFun{
            func("func", "bool" of Boolean::class) {
                If("bool") {
                    statement("println(true)")
                }.end()
            }
        } shouldBe FunSpec.builder("func")
                .addParameter("bool".of(Boolean::class).toParamSpec())
                .beginControlFlow("if (bool)")
                .addStatement("println(true)")
                .endControlFlow()
                .build()
    }
    "ifp end"{
        createFun {
            func("func", nullableBool) {
                ifp("bool") {
                    statement("println(true)")
                }.end()
            }
        } shouldBe FunSpec.builder("func")
                .addParameter(nullableBool)
                .beginControlFlow("if ((bool) == true)")
                .addStatement("println(true)")
                .endControlFlow()
                .build()
    }
    "ifn end"{
        createFun {
            func("func", nullableBool) {
                ifn("bool") {
                    statement("println(false)")
                }.end()
            }
        } shouldBe FunSpec.builder("func")
                .addParameter(nullableBool)
                .beginControlFlow("if ((bool) == false)")
                .addStatement("println(false)")
                .endControlFlow()
                .build()
    }

    "orElseIfp"{
        createFun {
            func("func", nullableBool) {
                ifn("bool") {
                    statement("println(false)")
                }.orElseIfp("bool") {
                    statement("println(true)")
                }.end()
            }
        } shouldBe FunSpec.builder("func")
                .addParameter(nullableBool)
                .beginControlFlow("if ((bool) == false)")
                .addStatement("println(false)")
                .nextControlFlow("else if ((bool) == true)")
                .addStatement("println(true)")
                .endControlFlow()
                .build()
    }

    "orElseIfn"{
        createFun {
            func("func", nullableBool) {
                ifp("bool") {
                    statement("println(true)")
                }.orElseIfn("bool"){
                    statement("println(false)")
                }.end()
            }
        } shouldBe FunSpec.builder("func")
                .addParameter(nullableBool)
                .beginControlFlow("if ((bool) == true)")
                .addStatement("println(true)")
                .nextControlFlow("else if ((bool) == false)")
                .addStatement("println(false)")
                .endControlFlow()
                .build()
    }

    "orElseIf orElse"{
        val int = ("int" of Int::class).toParamSpec()

        createFun {
            func("func", int) {
                If("int == 1"){
                    statement("println(\"first\")")
                }.orElseIf("int == 2"){
                    statement("println(\"second\")")
                } orElse {
                    statement("println(\"last\")")
                }
            }
        } shouldBe FunSpec.builder("func")
                .addParameter(int)
                .beginControlFlow("if (int == 1)")
                .addStatement("println(\"first\")")
                .nextControlFlow("else if (int == 2)")
                .addStatement("println(\"second\")")
                .nextControlFlow("else")
                .addStatement("println(\"last\")")
                .endControlFlow()
                .build()
    }

    "unfinished in IfStart"{
        shouldThrow<UnFinishException> {
            createFun {
                func("func", nullableBool) {
                    ifp("bool") {}
                }
            }
        }
    }

    "unfinished in IfEnd"{
        shouldThrow<UnFinishException> {
            createFun {
                func("func", nullableBool) {
                    ifp("bool") {}.orElseIfn("bool"){}
                }
            }
        }
    }


    "lazy if"{
        createCodeBlock {
            switch("1"){
                "1" then If("1==1"){
                    statement("println(1)")
                }.end()

                "2" then ifp("null") {
                    statement("""println("won't print, it's not positive")""")
                }.orElseIfn("null"){
                    statement("""println("won't print, it's not negative")""")
                }.orElseIfp("null") {
                    statement("""println("well this is stupid code")""")
                }.orElse {
                    statement("""println("finally, smth to print")""")
                }

                "3" then ifn("null"){
                    statement("""println("won't print, it's not positive")""")
                }.orElseIf("true"){
                    statement("""println(2)""")
                }.end()
            }
        }.toString() shouldBe
                """|when(1) {
                   |    1 -> if (1==1) {
                   |        println(1)
                   |    }
                   |    2 -> if ((null) == true) {
                   |        println("won't print, it's not positive")
                   |    } else if ((null) == false) {
                   |        println("won't print, it's not negative")
                   |    } else if ((null) == true) {
                   |        println("well this is stupid code")
                   |    } else {
                   |        println("finally, smth to print")
                   |    }
                   |    3 -> if ((null) == false) {
                   |        println("won't print, it's not positive")
                   |    } else if (true) {
                   |        println(2)
                   |    }
                   |}
                   |""".trimMargin()
    }
})