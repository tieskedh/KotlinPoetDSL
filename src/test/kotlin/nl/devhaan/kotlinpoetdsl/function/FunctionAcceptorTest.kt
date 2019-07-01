package nl.devhaan.kotlinpoetdsl.function

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.createFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.open
import nl.devhaan.kotlinpoetdsl.private
import nl.devhaan.kotlinpoetdsl.protected

/**
 * This class tests if the FunctionAcceptor is implemented correctly.
 * This is done by checking if:
 *  - the builders itself implements the interface correctly,
 *  - the accessors implements the interface correctly,
 *  - The delegation works correct.
 */
class FunctionAcceptorTest : StringSpec({

    val zeroFun = FunSpec.builder("func")
            .addStatement("println(%S)", "hi")
            .build()

    val oneFun = FunSpec.builder("func")
            .addStatement("println(%S)", "hi")
            .addModifiers(KModifier.PRIVATE)
            .build()

    "builder without modifier"{
        createFun {
            func("func") {
                statement("println(%S)", "hi")
            }
        } shouldBe zeroFun
    }

    "builder with modifier"{
        createFun {
            private.func("func") {
                statement("println(%S)", "hi")
            }
        } shouldBe oneFun
    }


    "file without modifier"{
        file("", "HelloWorld") {
            func("func") {
                statement("println(%S)", "hi")
            }
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addFunction(zeroFun)
                .build()
    }

    "file with Modifier"{
        file("", "HelloWorld") {
            private.func("func") {
                statement("println(%S)", "hi")
            }
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addFunction(oneFun)
                .build()
    }

    "class without modifier"{
        createClass {
            clazz("HelloWorld") {
                func("func") {
                    statement("println(%S)", "hi")
                }
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addFunction(zeroFun)
                .build()
    }

    "class with Modifier"{
        createClass {
            clazz("HelloWorld") {
                private.func("func") {
                    statement("println(%S)", "hi")
                }
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addFunction(oneFun)
                .build()
    }
})