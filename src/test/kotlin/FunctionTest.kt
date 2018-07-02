import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.buildClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.buildFun
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.open
import nl.devhaan.kotlinpoetdsl.protected

class FunctionAcceptorTest : StringSpec({

    val zeroFun = FunSpec.builder("func")
            .addStatement("println(%S)", "hi")
            .build()

    val oneFun = FunSpec.builder("func")
            .addStatement("println(%S)", "hi")
            .addModifiers(KModifier.OPEN)
            .build()

    val twoFun = FunSpec.builder("func")
            .addStatement("println(%S)", "hi")
            .addModifiers(KModifier.PROTECTED, KModifier.OPEN)
            .build()

    "builder without modifier"{
        buildFun {
            func("func") {
                statement("println(%S)", "hi")
            }
        } shouldBe zeroFun
    }

    "builder with modifier"{
        buildFun(KModifier.OPEN) {
            func("func") {
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

    "file with initialized Modifier"{
        file("", "HelloWorld") {
            func(oneFun)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addFunction(oneFun)
                .build()
    }

    "file add modifier"{
        file("", "HelloWorld") {
            open.func(zeroFun)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addFunction(oneFun)
                .build()
    }

    "file merge modifier"{
        file("", "HelloWorld") {
            protected.func(oneFun)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addFunction(twoFun)
                .build()
    }


    "class without modifier"{
        buildClass {
            clazz("HelloWorld") {
                func("func") {
                    statement("println(%S)", "hi")
                }
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addFunction(zeroFun)
                .build()
    }

    "class with initialized Modifier"{
        buildClass {
            clazz("HelloWorld") {
                func(oneFun)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addFunction(oneFun)
                .build()
    }

    "class add modifier"{
        buildClass {
            clazz("HelloWorld") {
                open.func(zeroFun)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addFunction(oneFun)
                .build()
    }

    "class merge modifier"{
        buildClass {
            clazz("HelloWorld") {
                protected.func(oneFun)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addFunction(twoFun)
                .build()
    }
})