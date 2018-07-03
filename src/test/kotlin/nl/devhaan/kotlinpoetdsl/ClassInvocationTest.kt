package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.func


/**
 * This class checks if all the function-invocations work correctly.
 * This is done against one ClassAcceptor (as the implementation is already checked)
 */
class ClassInvocationTest : StringSpec({
    "plain class"{
        file("", "TestFile") {
            clazz("Clazz")
        } shouldBe FileSpec.builder("", "TestFile")
                .addType(TypeSpec.classBuilder("Clazz").build())
                .build()
    }

    "class with parameter"{
        file("", "TestFile") {
            clazz("Clazz", "arg" of String::class)
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").primaryConstructor(
                        FunSpec.constructorBuilder().addParameter("arg", String::class).build()
                ).build()
        ).build()
    }

    "class with var-parameter"{
        file("", "TestFile") {
            clazz("Clazz", "arg" varOf String::class)
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").primaryConstructor(
                        FunSpec.constructorBuilder().addParameter("arg", String::class).build()
                ).addProperty(
                        PropertySpec.builder("arg", String::class).initializer("arg").mutable(true).build()
                ).build()
        ).build()
    }

    "class with val-parameter"{
        file("", "TestFile") {
            clazz("Clazz", "arg" valOf String::class)
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").primaryConstructor(
                        FunSpec.constructorBuilder().addParameter("arg", String::class).build()
                ).addProperty(
                        PropertySpec.builder("arg", String::class).initializer("arg").build()
                ).build()
        ).build()
    }

    "class with initialized var-parameter"{
        file("", "TestFile") {
            clazz("Clazz", "arg".varOf<String>("\"hi\""))
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").primaryConstructor(
                        FunSpec.constructorBuilder().addParameter(
                                ParameterSpec.builder("arg", String::class).defaultValue("\"hi\"").build()
                        ).build()
                ).addProperty(
                        PropertySpec.builder("arg", String::class).initializer("arg").mutable(true).build()
                ).build()
        ).build()
    }

    "class with content"{
        file("", "TestFile") {
            clazz("Clazz") {
                func("func") {
                    statement("println(\"hi\"")
                }
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").addFunction(
                        FunSpec.builder("func").addStatement("println(\"hi\"").build()
                ).build()
        ).build()
    }
})