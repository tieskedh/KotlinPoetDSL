package nl.devhaan.kotlinpoetdsl.prop

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.getters.getter
import nl.devhaan.kotlinpoetdsl.inline
import nl.devhaan.kotlinpoetdsl.private

import nl.devhaan.kotlinpoetdsl.properties.prop
import nl.devhaan.kotlinpoetdsl.setters.setter
import nl.devhaan.kotlinpoetdsl.valOf
import nl.devhaan.kotlinpoetdsl.varOf

class PropInvocationtest : StringSpec({
    "var" {
        file("", "HelloWorld") {
            prop("prop" varOf String::class)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", String::class).mutable().build()
                ).build()
    }

    "val"{
        file("", "HelloWorld") {
            prop("prop" valOf String::class)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", String::class).build()
                ).build()
    }

    "initialized var"{
        file("", "HelloWorld") {
            prop("prop".varOf<Int>("1"))
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", Int::class)
                                .mutable()
                                .initializer("1")
                                .build()
                ).build()
    }

    "initialized val"{
        file("", "HelloWorld") {
            prop("prop".valOf<Int>("1"))
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", Int::class)
                                .initializer("1")
                                .build()
                ).build()
    }

    "initialize prop"{
        file("", "HelloWorld") {
            prop("prop" valOf Int::class) {
                init("1")
            }
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", Int::class)
                                .initializer("1")
                                .build()
                ).build()
    }

    "private prop"{
        file("", "HelloWorld") {
            private.prop("prop".valOf<Int>("1"))
        } shouldBe FileSpec.builder("", "HelloWorld").addProperty(
                PropertySpec.builder("prop", Int::class).initializer("1").addModifiers(KModifier.PRIVATE).build()
        ).build()
    }


    "inlined val-prop with initializer"{
        file("", "HelloWorld") {
            inline.prop("prop".valOf<Int>("1"))
        } shouldBe FileSpec.builder("", "HelloWorld").addProperty(
                PropertySpec.builder("prop", Int::class).getter(
                        FunSpec.getterBuilder()
                                .addStatement("return 1")
                                .addModifiers(KModifier.INLINE)
                                .build()
                ).build()
        ).build()
    }

    "inlined val-prop with inlined getter"{
        file("", "HelloWorld") {
            inline.prop("prop" valOf Int::class) {
                inline.getter {
                    statement("return 1")
                }
            }
        } shouldBe FileSpec.builder("", "HelloWorld").addProperty(
                PropertySpec.builder("prop", Int::class).getter(
                        FunSpec.getterBuilder()
                                .addStatement("return 1")
                                .addModifiers(KModifier.INLINE)
                                .build()
                ).build()
        ).build()
    }

    "inlined var-prop with inlined getter and inlined setter"{
        file("", "HelloWorld") {
            inline.prop("prop" varOf Int::class) {
                inline.setter { }
                inline.getter("return 1")
            }
        } shouldBe FileSpec.builder("", "HelloWorld").addProperty(
                PropertySpec.builder("prop", Int::class).getter(
                        FunSpec.getterBuilder()
                                .addStatement("return 1")
                                .addModifiers(KModifier.INLINE)
                                .build()
                ).setter(
                        FunSpec.setterBuilder()
                                .addParameter("value", String::class)
                                .addModifiers(KModifier.INLINE)
                                .build()
                ).mutable().build()
        ).build()
    }
})