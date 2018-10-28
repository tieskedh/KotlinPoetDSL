package nl.devhaan.kotlinpoetdsl.interf

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.`interface`.createInterface
import nl.devhaan.kotlinpoetdsl.`interface`.interf
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.open
import nl.devhaan.kotlinpoetdsl.private

class InterfaceAcceptorTest : StringSpec({
    val zeroInterface = TypeSpec.interfaceBuilder("Interf").build()
    val oneInterface = TypeSpec.interfaceBuilder("Interf").addModifiers(KModifier.PRIVATE).build()
    val twoInterface = TypeSpec.interfaceBuilder("Interf").addModifiers(KModifier.OPEN, KModifier.PRIVATE).build()
    "builder without modifier"{
        createInterface{
            interf("Interf"){}
        } shouldBe zeroInterface
    }

    "builder with modifier"{
        createInterface {
            private.interf("Interf"){}
        } shouldBe oneInterface
    }

    "file without modifier"{
        file("", "HelloWorld"){
            interf("Interf"){}
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(zeroInterface)
                .build()
    }

    "file with initialized Modifier"{
        file("", "HelloWorld"){
            interf(oneInterface)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(oneInterface)
                .build()
    }

    "file add modifier" {
        file("", "HelloWorld") {
            private.interf(zeroInterface)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(oneInterface)
                .build()
    }

    "file merge modifier" {
        file("", "HelloWorld") {
            open.interf(oneInterface)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(twoInterface)
                .build()
    }

    "clazz without modifier"{
        createClass{
            clazz("HelloWorld"){
                interf("Interf"){}
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(zeroInterface)
                .build()
    }
    "clazz with initialized modifier"{
        createClass{
            clazz("HelloWorld"){
                interf(oneInterface)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneInterface)
                .build()
    }

    "class add modifier"{
        createClass{
            clazz("HelloWorld"){
                private.interf(zeroInterface)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneInterface)
                .build()
    }

    "class merge modifier"{
        createClass{
            clazz("HelloWorld"){
                open.interf(oneInterface)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(twoInterface)
                .build()
    }

    "interface without modifier"{
        createInterface {
            interf("HelloWorld"){
                interf("Interf"){}
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(zeroInterface)
                .build()
    }

    "interface with initialized modifier"{
        createInterface {
            interf("HelloWorld"){
                interf(oneInterface)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneInterface)
                .build()
    }

    "interface add modifier"{
        createInterface {
            interf("HelloWorld"){
                private.interf(zeroInterface)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneInterface)
                .build()
    }

    "interface merge modifier"{
        createInterface {
            interf("HelloWorld"){
                open.interf(oneInterface)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(twoInterface)
                .build()
    }
})