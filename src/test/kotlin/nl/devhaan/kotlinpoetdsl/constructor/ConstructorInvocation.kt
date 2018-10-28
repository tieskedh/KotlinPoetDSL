package nl.devhaan.kotlinpoetdsl.constructor

import com.squareup.kotlinpoet.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.*
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.classes.extends
import nl.devhaan.kotlinpoetdsl.constructorBuilder.constructor
import nl.devhaan.kotlinpoetdsl.constructorBuilder.primary
import nl.devhaan.kotlinpoetdsl.constructorBuilder.thiz
import nl.devhaan.kotlinpoetdsl.constructorBuilder.zuper
import nl.devhaan.kotlinpoetdsl.files.file

class ConstructorInvocation : StringSpec({
    "2nd constructor without body with parameter"{
        createClass {
            clazz("Clazz") {
                constructor("a".of<Int>("4"))
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addFunction(
                FunSpec.constructorBuilder().addParameter(
                        ParameterSpec.builder("a", Int::class).defaultValue("4").build()
                ).build()
        ).build()
    }
    "2nd constructor with body and parameter"{
        createClass {
            clazz("Clazz") {
                constructor("a".of<Int>("4")) {}
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addFunction(
                FunSpec.constructorBuilder().addParameter(
                        ParameterSpec.builder("a", Int::class).defaultValue("4").build()
                ).build()
        ).build()
    }

    "private 2nd constructor without body and parameters"{
        createClass {
            clazz("Clazz") {
                private.constructor()
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addFunction(
                FunSpec.constructorBuilder()
                        .addModifiers(KModifier.PRIVATE)
                        .build()
        ).build()
    }

    "2nd constructor with thizz without body"{
        createClass {
            clazz("Clazz", "a" valOf String::class) {
                constructor("a" of Int::class).thiz("a.toString()")
            }
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder().addParameter("a", String::class).build()
        ).addProperty(
                PropertySpec.builder("a", String::class).initializer("a").build()
        ).addFunction(
                FunSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder("a", Int::class).build())
                        .callThisConstructor("a.toString()")
                        .build()
        ).build()
    }

    "2nd constructor with thizz with body"{
        createClass {
            clazz("Clazz", "a" valOf String::class) {
                constructor("a" of Int::class).thiz("a.toString()") {
                    statement("println(1)")
                }
            }
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder().addParameter("a", String::class).build()
        ).addProperty(
                PropertySpec.builder("a", String::class).initializer("a").build()
        ).addFunction(
                FunSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder("a", Int::class).build())
                        .callThisConstructor("a.toString()")
                        .addStatement("println(1)")
                        .build()
        ).build()
    }

    "2nd constructor with zuper without body"{
        createClass {
            clazz("Clazz") extends String::class{
                constructor("arg" of String::class).zuper("arg")
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addFunction(
                FunSpec.constructorBuilder()
                        .addParameter("arg", String::class)
                        .callSuperConstructor("arg")
                        .build()
        ).superclass(String::class).build()
    }

    "2nd constructor with zuper with body"{
        createClass {
            clazz("Clazz") extends String::class{
                constructor("arg" valOf String::class).zuper("arg") {
                    "println(1)".statement()
                }
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addFunction(
                FunSpec.constructorBuilder()
                        .addParameter("arg", String::class)
                        .callSuperConstructor("arg")
                        .addStatement("println(1)")
                        .build()
        ).superclass(String::class).build()
    }



    "1st constructor without body"{
        createClass {
            clazz("Clazz") {
                primary.constructor("a".of<Int>("4"))
            }
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder().addParameter(
                        ParameterSpec.builder("a", Int::class).defaultValue("4").build()
                ).build()
        ).build()
    }
    "1st constructor with body"{
        createClass {
            clazz("Clazz") {
                primary.constructor("a".of<Int>("4")) {}
            }
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder().addParameter(
                        ParameterSpec.builder("a", Int::class).defaultValue("4").build()
                ).build()
        ).build()
    }

    "private 1st constructor with accessor"{
        createClass {
            clazz("Clazz") {
                private.primary.constructor()
            }
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder()
                        .addModifiers(KModifier.PRIVATE)
                        .build()
        ).build()
    }

    "1st constructor with property"{
        createClass {
            clazz("Clazz") {
                primary.constructor("a" valOf Int::class)
            }
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder()
                        .addParameter("a", Int::class)
                        .callSuperConstructor("a.ToString()")
                        .build()
        ).addProperty(
                PropertySpec.builder("a", Int::class).initializer("a").build()
        ).build()
    }

    "proposal-issue"{
        file("", "TestFile") {
            open.clazz("clazz1") {
                private.primary.constructor("test".valOf<String>("def".S())) {
                    statement("println(1)")
                }
                protected.constructor("test".valOf<Int>()).thiz("test.toString()") {
                    //valOf shouldn't be used, but should be let's test if it is correctly ignored anyway...
                    statement("println(2)")
                }
            }
            clazz("Clazz") extends String::class{
                protected.constructor("test" of Int::class).zuper("a".S()) {
                    statement("println(0)")
                }
            }
        } shouldBe run{
            val clz1prim = FunSpec.constructorBuilder().addParameter(
                    ParameterSpec.builder("test", String::class).defaultValue("\"def\"").build()
            ).addModifiers(KModifier.PRIVATE).addStatement("println(1)").build()

            val clz1sec = FunSpec.constructorBuilder()
                    .addParameter("test", Int::class)
                    .callThisConstructor("test.toString()")
                    .addStatement("println(2)")
                    .addModifiers(KModifier.PROTECTED)
                    .build()
            val clz1prop = PropertySpec.builder("test", String::class).initializer("test").build()
            val clz1 = TypeSpec.classBuilder("clazz1")
                    .addModifiers(KModifier.OPEN)
                    .primaryConstructor(clz1prim)
                    .addProperty(clz1prop)
                    .addFunction(clz1sec)
                    .build()

            val clz2sec = FunSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder("test", Int::class).build())
                    .callSuperConstructor("\"a\"")
                    .addModifiers(KModifier.PROTECTED)
                    .addStatement("println(0)")
                    .build()
            val clz2 = TypeSpec.classBuilder("Clazz")
                    .addFunction(clz2sec)
                    .superclass(String::class)
                    .build()

            FileSpec.builder("", "TestFile")
                    .addType(clz1)
                    .addType(clz2)
                    .build()
        }
    }
})

