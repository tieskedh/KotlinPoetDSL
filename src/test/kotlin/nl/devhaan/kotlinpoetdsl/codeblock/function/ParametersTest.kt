package nl.devhaan.kotlinpoetdsl.codeblock.function

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import nl.devhaan.kotlinpoetdsl.functions.createFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.of
import nl.devhaan.kotlinpoetdsl.vararg

class ParametersTest : StringSpec({
    "use existing"{
        createFun {
            func("func", "a" of String::class) {
                val a by params.existing
                returns(a)
            }
        } shouldBe FunSpec.builder("func")
                .addParameter("a", String::class)
                .returns(String::class)
                .addStatement("return a")
                .build()
    }
    "use nonExisting"{
        shouldThrow<NoSuchElementException> {
            createFun {
                func("func") {
                    val a by params.existing
                    returns(a)
                }
            }
        }.message shouldBe "At this moment, the function has no parameter named a"
    }
    "create nonExisting"{
        createFun {
            func("func") {
                val a by params.creating<String>()
                returns(a)
            }
        } shouldBe FunSpec.builder("func")
                .addParameter("a", String::class)
                .returns(String::class)
                .addStatement("return a")
                .build()
    }

    "create existing"{
        shouldThrow<ParameterAlreadyExistsException> {
            createFun {
                func("func", "a" of String::class) {
                    val a by params.creating<String>()
                    returns(a)
                }
            }
        }.parameterName  shouldBe "a"
    }
    "create and change"{
        createFun {
            func("func") {
                val a by params.creating<String>{
                    addModifiers(KModifier.VARARG)
                }
                returns(a)
            }
        } shouldBe FunSpec.builder("func")
                .addParameter("a", String::class, KModifier.VARARG)
                .returns(String::class)
                .addStatement("return a")
                .build()
    }
    "create nonExisting with variable"{
        createFun {
            func("func") {
                val a = params.create("a" vararg String::class)
                returns(a)
            }
        } shouldBe FunSpec.builder("func")
                .addParameter("a", String::class, KModifier.VARARG)
                .returns(String::class)
                .addStatement("return a")
                .build()
    }
    "redeclare existing with variable"{
        shouldThrow<ParameterAlreadyExistsException> {
            createFun {
                func("func", "a" of Int::class){
                    val a = params.create("a" vararg String::class)
                    returns(a)
                }
            }
        }.parameterName shouldBe "a"
    }
})