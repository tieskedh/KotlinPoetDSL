package nl.devhaan.kotlinpoetdsl.interf

import com.squareup.kotlinpoet.*
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import nl.devhaan.kotlinpoetdsl.`interface`.implements
import nl.devhaan.kotlinpoetdsl.`interface`.interf
import nl.devhaan.kotlinpoetdsl.classes.buildUpon
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.open

class InterfaceInvocationTest : StringSpec({
    "plain interface"{
        file("", "TestFile"){
            interf("Interf"){}
        } shouldBe FileSpec.builder("", "TestFile")
                .addType(TypeSpec.interfaceBuilder("Interf").build())
                .build()
    }
    "interface with content"{
        file("", "TestFile"){
            interf("Interf"){
                func("func")
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Interf").addFunction(
                        FunSpec.builder("func").build()
                ).build()
        ).build()
    }

    "interface-implementation without body"{
        file("", "TestFile"){
            interf("Inter") implements String::class
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Inter")
                        .addSuperinterface(String::class)
                        .build()
        ).build()
    }

    "Interface-implementation with body"{
        file("", "TestFile"){
            interf("Interf") implements String::class{
                func("func")
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Interf").addFunction(
                        FunSpec.builder("func").build()
                ).addSuperinterface(String::class.asTypeName()).build()
        ).build()
    }

    "interface-implementation with class-vararg without Body"{
        file("", "TestFile"){
            interf("Interf").implements(String::class, Int::class)
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Interf").addSuperinterfaces(listOf(
                        String::class.asTypeName(), Int::class.asTypeName()
                )).build()
        ).build()
    }
    "interface-implementation with class-vararg with Body"{
        file("", "TestFile"){
            interf("Interf").implements(String::class, Int::class){
                func("func")
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Interf").addSuperinterfaces(listOf(
                        String::class.asTypeName(), Int::class.asTypeName()
                )).addFunction(
                        FunSpec.builder("func").build()
                ).build()
        ).build()
    }

    "interface-implementation with typeName-vararg without Body"{
        file("", "TestFile"){
            interf("Interf").implements(String::class.asTypeName(), Int::class.asTypeName())
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Interf").addSuperinterfaces(listOf(
                        String::class.asTypeName(), Int::class.asTypeName()
                )).build()
        ).build()
    }
    "interface-implementation with typeName-vararg with Body"{
        file("", "TestFile"){
            interf("Interf").implements(String::class.asTypeName(), Int::class.asTypeName()){
                func("func")
            }
        } shouldBe FileSpec.builder("", "TestFile").addType(
                TypeSpec.interfaceBuilder("Interf").addSuperinterfaces(listOf(
                        String::class.asTypeName(), Int::class.asTypeName()
                )).addFunction(
                        FunSpec.builder("func").build()
                ).build()
        ).build()
    }

    "interface attachFunc direct"{
        val interf  = TypeSpec.interfaceBuilder("Interf")
                .addModifiers(KModifier.PUBLIC)
                .build()

        file("", "TestFile"){
            interf.attachClazz()
        } shouldBe FileSpec.builder("", "TestFile")
                .addType(interf)
                .build()
    }

    "class attachFunc DSL"{
        val interf  = TypeSpec.interfaceBuilder("Interf")
                .addModifiers(KModifier.PUBLIC)
                .build()

        file("", "TestFile"){
            interf.attachClazz{ existing + open }
        } shouldBe FileSpec.builder("", "TestFile")
                .addType(interf.buildUpon { addModifiers(KModifier.OPEN) })
                .build()
    }
})