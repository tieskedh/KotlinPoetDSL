package nl.devhaan.kotlinpoetdsl.interf

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
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

    "file with Modifier"{
        file("", "HelloWorld"){
            private.interf("Interf")
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(oneInterface)
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
    "clazz with modifier"{
        createClass{
            clazz("HelloWorld"){
                private.interf("Interf"){}
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneInterface)
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

    "interface with modifier"{
        createInterface {
            interf("HelloWorld"){
                private.interf("Interf")
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneInterface)
                .build()
    }
})