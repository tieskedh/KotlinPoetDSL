package nl.devhaan.kotlinpoetdsl.clazz

import com.squareup.kotlinpoet.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.classes.*
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.func
import java.lang.IllegalArgumentException


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

    "class with paramSpec"{
        val paramSpec = ParameterSpec
                .builder("param", String::class)
                .defaultValue("\"hi\"")
                .build()

        file("", "TestFile") {
            clazz("Clazz", paramSpec)
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").primaryConstructor(
                        FunSpec.constructorBuilder().addParameter(paramSpec).build()
                ).build()
        ).build()
    }

    "class with initialized propSpec"{
        val propSpec = PropertySpec.builder("arg", String::class).initializer("\"hi\"").mutable(true).build()

        file("", "TestFile") {
            clazz("Clazz", propSpec)
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

    "class-extension without params and without body"{
        file("", "TestFile") {
            clazz("Clazz") extends String::class
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").superclass(String::class).build()
        ).build()
    }

    "class-extension without params with body"{
        file("", "TestFile") {
            clazz("Clazz") extends String::class{
                func("test")
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .superclass(String::class)
                        .addFunction(FunSpec.builder("test").build())
                        .build()
        ).build()
    }


    "class-extension with param without body"{
        file("", "TestFile") {
            clazz("Clazz") extends String::class("1")
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").superclass(String::class).addSuperclassConstructorParameter("1").build()
        ).build()
    }

    "class-extension with param with body"{
        file("", "TestFile") {
            clazz("Clazz") extends String::class("1"){
                func("test") {}
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .superclass(String::class)
                        .addSuperclassConstructorParameter("1")
                        .addFunction(FunSpec.builder("test").build())
                        .build()
        ).build()
    }

    "class-implementation without delegation and without body"{
        file("", "TestFile") {
            clazz("Clazz") implements String::class
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").addSuperinterface(String::class).build()
        ).build()
    }

    "class-implementation without delegation with body"{
        file("", "TestFile") {
            clazz("Clazz") implements String::class{
                func("test") {}
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .addSuperinterface(String::class)
                        .addFunction(FunSpec.builder("test").build())
                        .build()
        ).build()
    }

    "class-implementation with method-delegation without body"{
        file("", "TestFile") {
            clazz("Clazz") implements String::class("createDel()")
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz").addSuperinterface(String::class, CodeBlock.of("createDel()")).build()
        ).build()
    }

    "class-implementation with method-delegation with body"{
        file("", "TestFile") {
            clazz("Clazz") implements String::class("createDel()"){
                func("test") {}
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .addSuperinterface(String::class, CodeBlock.of("createDel()"))
                        .addFunction(FunSpec.builder("test").build())
                        .build()
        ).build()
    }

    "class-implementation with property-delegation"{
        file("", "TestFile") {
            clazz("Clazz", "prop" of String::class) implements String::class(delVar("prop"))
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .primaryConstructor(
                                FunSpec.constructorBuilder().addParameter("prop", String::class).build()
                        ).addSuperinterface(String::class, "prop").build()
        ).build()
    }

    "class-implementation with dsl"{
        file("", "TestFile") {
            clazz("Clazz", "prop" of String::class).implements ({
                String::class(delVar("prop"))
                Int::class("4")
            }){}
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .primaryConstructor(
                                FunSpec.constructorBuilder().addParameter("prop", String::class).build()
                        ).addSuperinterface(String::class, "prop")
                        .addSuperinterface(Int::class, CodeBlock.of("4"))
                        .build()
        ).build()
    }

    "class-implementation and extension with DSL"{
        file("", "TestFile"){
            clazz("Clazz", "prop" of String::class) extends StringBuffer::class("4") implements {
                String::class(delVar("prop"))
            } withBody {
                func("test")
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.classBuilder("Clazz")
                        .primaryConstructor(
                                FunSpec.constructorBuilder().addParameter("prop", String::class).build()
                        ).addSuperinterface(String::class, CodeBlock.of("prop"))
                        .superclass(StringBuffer::class)
                        .addSuperclassConstructorParameter("4")
                        .addFunction(FunSpec.builder("test").build())
                        .build()
        ).build()
    }

    "class called primaryConstructor twice"{
        shouldThrow<IllegalArgumentException> {
            file("", "TestFile"){
                clazz("Clazz") extends String::class{
                    primaryConstructor("prop" of String::class){
                        statement("println(%S)", "hi".S())
                    }
                    primaryConstructor("prop" of Int::class){
                        statement("println(%S)", "hoi".S())
                    }
                }
            }
        }.message shouldBe "primary constructor is already set"
    }

    "class attachFunc direct"{
        val clazz  = TypeSpec.classBuilder("Clazz")
                .addModifiers(KModifier.PUBLIC)
                .build()

        file("", "TestFile"){
            clazz.attachClazz()
        } shouldBe FileSpec.builder("", "TestFile")
                .addType(clazz)
                .build()
    }

    "class attachFunc DSL"{
        val clazz  = TypeSpec.classBuilder("Clazz")
                .addModifiers(KModifier.PUBLIC)
                .build()

        file("", "TestFile"){
            clazz.attachClazz{ existing + open }
        } shouldBe FileSpec.builder("", "TestFile")
                .addType(clazz.buildUpon { addModifiers(KModifier.OPEN) })
                .build()
    }
})