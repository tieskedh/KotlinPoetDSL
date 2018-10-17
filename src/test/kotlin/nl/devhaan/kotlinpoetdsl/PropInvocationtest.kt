package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.files.file

import nl.devhaan.kotlinpoetdsl.properties.prop

class PropInvocationtest : StringSpec({
//    fun PropAcceptor.prop(variable: Variable, buildScript: PropBuilder.()->Unit={})
    "var" {
        file("", "HelloWorld"){
            prop("prop" varOf String::class)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.varBuilder("prop", String::class).build()
                ).build()
    }

    "val"{
        file("", "HelloWorld"){
            prop("prop" valOf String::class)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", String::class).build()
                ).build()
    }

    "initialized var"{
        file("", "HelloWorld"){
            prop("prop".varOf<Int>("1"))
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.varBuilder("prop", Int::class)
                                .initializer("1")
                                .build()
                ).build()
    }

    "initialized val"{
        file("", "HelloWorld"){
            prop("prop".valOf<Int>("1"))
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", Int::class)
                                .initializer("1")
                                .build()
                ).build()
    }

    "initialize prop"{
        file("", "HelloWorld"){
            prop("prop" valOf Int::class){
                init("1")
            }
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(
                        PropertySpec.builder("prop", Int::class)
                                .initializer("1")
                                .build()
                ).build()
    }
})