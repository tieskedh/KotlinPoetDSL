package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.buildCodeBlock
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import nl.devhaan.kotlinpoetdsl.functions.createFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.functions.returns
import nl.devhaan.kotlinpoetdsl.functions.withBody
import nl.devhaan.kotlinpoetdsl.helpers.wrapper

class LazyComponentTest : StringSpec({
    val lazy = createLazyComponent(true) {
        it.addCode {
            repeat(5) {
                statement("println(it)")
            }
        }
    }

    "add lazy-component to functionBlock inside function"{
        createFun {
            func("hi") {
                statement("println(\"I'm going to count\")")
                lazy.attach()
                statement("println(\"done\")")
            }
        } shouldBe FunSpec.builder("hi")
                .addCode(buildCodeBlock {
                    addStatement("println(\"I'm going to count\")")
                    beginControlFlow("repeat(5)")
                    addStatement("println(it)")
                    endControlFlow()
                    addStatement("println(\"done\")")
                }).build()
    }

    "add lazy-component as function-body"{
        createFun {
            func("hi") withBody lazy
        } shouldBe FunSpec.builder("hi")
                .addCode(lazy.toCodeBlock())
                .build()
    }
})