package nl.devhaan.kotlinpoetdsl.variable

import com.squareup.kotlinpoet.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.vararg
import nl.devhaan.kotlinpoetdsl.varargVal
import nl.devhaan.kotlinpoetdsl.varargVar

class VariableUsageTest: StringSpec({
    "vararg clazz"{
        createClass {
            clazz("Clazz", "a".vararg<String>("arrayOf(\"hi\")")) {}
        } shouldBe TypeSpec.classBuilder("Clazz")
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(ParameterSpec.builder("a", String::class, KModifier.VARARG)
                                .defaultValue("arrayOf(\"hi\")")
                                .build())
                        .build())
                .build()
    }

    "varargVal clazz"{
        createClass {
            clazz("Clazz", "a".varargVal<String>("arrayOf(\"hi\")")) {}
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder().addParameter(
                        ParameterSpec.builder("a", String::class, KModifier.VARARG)
                                .defaultValue("arrayOf(\"hi\")")
                                .build()
                ).build()
        ).addProperty(
                PropertySpec.builder("a", String::class)
                        .initializer("a")
                        .mutable(false)
                        .build()
        ).build()
    }

    "varargVar clazz"{
        createClass {
            clazz("Clazz", "a".varargVar<String>("arrayOf(\"hi\")")) {}
        } shouldBe TypeSpec.classBuilder("Clazz").primaryConstructor(
                FunSpec.constructorBuilder().addParameter(
                        ParameterSpec.builder("a", String::class, KModifier.VARARG)
                                .defaultValue("arrayOf(\"hi\")")
                                .build()
                ).build()
        ).addProperty(
                PropertySpec.builder("a", String::class)
                        .initializer("a")
                        .mutable(true)
                        .build()
        ).build()
    }


})