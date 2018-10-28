package nl.devhaan.kotlinpoetdsl.function

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.abstract
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.functions.extension
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.functions.returns
import nl.devhaan.kotlinpoetdsl.of
import nl.devhaan.kotlinpoetdsl.private

/**
 * This class checks if all the function-invocations work correctly.
 * This is done against one FuncAcceptor (as the implementation is already checked)
 */
class FunctionInvocationTest : StringSpec({

    "plain function"{
        createClass {
            clazz("TestClass") {
                func("func") {
                    statement("println(\"works\")")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .addStatement("println(\"works\")")
                                .build()
                ).build()
    }

    "function with parameter"{
        createClass {
            clazz("TestClass") {
                func("func", "toPrint" of String::class) {
                    statement("println(toPrint)")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .addParameter("toPrint", String::class)
                                .addStatement("println(toPrint)")
                                .build()
                ).build()
    }

    "function with class-return"{
        createClass {
            clazz("TestClass") {
                func("func") returns String::class{
                    statement("return %S", "ret")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .returns(String::class)
                                .addStatement("return %S", "ret")
                                .build()
                ).build()
    }

    "function with typeName-return"{
        val string = String::class.asTypeName()
        createClass {
            clazz("TestClass") {
                func("func") returns string {
                    statement("return %S", "ret")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .returns(String::class)
                                .addStatement("return %S", "ret")
                                .build()
                ).build()
    }

    "function with parameter and return"{
        createClass {
            clazz("TestClass") {
                func("func", "retVal" of String::class) returns String::class{
                    statement("return retVal")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .addParameter("retVal", String::class)
                                .returns(String::class)
                                .addStatement("return retVal")
                                .build()
                ).build()
    }

    "private function with parameter and return"{
        createClass {
            clazz("TestClass") {
                private.func("func", "retVal" of String::class) returns String::class{
                    statement("return retVal")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .addParameter("retVal", String::class)
                                .addModifiers(KModifier.PRIVATE)
                                .returns(String::class)
                                .addStatement("return retVal")
                                .build()
                ).build()
    }

    "private function with parameter"{
        createClass {
            clazz("TestClass") {
                private.func("func", "toPrint" of String::class) {
                    statement("println(toPrint)")
                }
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addFunction(
                        FunSpec.builder("func")
                                .addParameter("toPrint", String::class)
                                .addModifiers(KModifier.PRIVATE)
                                .addStatement("println(toPrint)")
                                .build()
                ).build()
    }

    "abstract function no return"{
        createClass {
            abstract.clazz("TestClass") {
                abstract.func("func", "toPrint" of String::class) {}
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addModifiers(KModifier.ABSTRACT)
                .addFunction(
                        FunSpec.builder("func")
                                .addModifiers(KModifier.ABSTRACT)
                                .addParameter("toPrint", String::class)
                                .build()
                ).build()
    }
    "abstract function with return"{
        createClass {
            abstract.clazz("TestClass") {
                abstract.func("func", "toPrint" of String::class) returns String::class
            }
        } shouldBe TypeSpec.classBuilder("TestClass")
                .addModifiers(KModifier.ABSTRACT)
                .addFunction(
                        FunSpec.builder("func")
                                .addModifiers(KModifier.ABSTRACT)
                                .addParameter("toPrint", String::class)
                                .returns(String::class)
                                .build()
                ).build()
    }

    "receiver-function no return no modifier"{
        createClass {
            clazz("TestClazz") {
                Int::class.func("func")
            }
        } shouldBe TypeSpec.classBuilder("TestClazz").addFunction(
                FunSpec.builder("func").receiver(Int::class).build()
        ).build()
    }
    "receiver-function no return with modifier"{
        createClass {
            clazz("TestClazz") {
                private.extension(Int::class.asTypeName()).func("func")
            }
        } shouldBe TypeSpec.classBuilder("TestClazz").addFunction(
                FunSpec.builder("func").receiver(Int::class).addModifiers(KModifier.PRIVATE).build()
        ).build()
    }

    "receiver-function with return no modifier"{
        createClass {
            clazz("TestClazz"){
                Int::class.func("func") returns Int::class
            }
        } shouldBe TypeSpec.classBuilder("TestClazz").addFunction(
                FunSpec.builder("func").receiver(Int::class).returns(Int::class).build()
        ).build()
    }

    "receiver-function with return with modifier"{
        createClass {
            clazz("TestClazz"){
                private.extension(Int::class).func("func") returns Int::class
            }
        } shouldBe TypeSpec.classBuilder("TestClazz").addFunction(
                FunSpec.builder("func")
                        .receiver(Int::class)
                        .returns(Int::class)
                        .addModifiers(KModifier.PRIVATE)
                        .build()
        ).build()
    }
})