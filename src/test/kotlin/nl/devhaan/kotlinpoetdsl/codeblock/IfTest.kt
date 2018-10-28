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
import nl.devhaan.kotlinpoetdsl.of

class IfTest : StringSpec({
    val nullableBool = ParameterSpec.builder("bool", String::class.asTypeName().asNullable()).build()

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
})