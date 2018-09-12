package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.buildClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.functions.func

/**
 * This class checks if all the function-invocations work correctly.
 * This is done against one FuncAcceptor (as the implementation is already checked)
 */
class FunctionInvocationTest : StringSpec({

    "plain function"{
        buildClass{
            clazz("TestClass"){
                func("func"){
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
        buildClass{
            clazz("TestClass"){
                func("func", "toPrint" of String::class){
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
        buildClass {
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
        buildClass {
            clazz("TestClass") {
                func("func") returns string{
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
        buildClass {
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
        buildClass {
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
        buildClass{
            clazz("TestClass"){
                private.func("func", "toPrint" of String::class){
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
        buildClass{
            clazz("TestClass"){
                addModifiers(KModifier.ABSTRACT)
                abstract.func("func", "toPrint" of String::class){}
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
        buildClass{
            clazz("TestClass"){
                addModifiers(KModifier.ABSTRACT)
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

})