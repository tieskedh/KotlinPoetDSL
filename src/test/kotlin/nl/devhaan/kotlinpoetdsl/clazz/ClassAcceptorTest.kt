package nl.devhaan.kotlinpoetdsl.clazz

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.`interface`.createInterface
import nl.devhaan.kotlinpoetdsl.`interface`.interf
import nl.devhaan.kotlinpoetdsl.abstract
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.func
import nl.devhaan.kotlinpoetdsl.internal
import nl.devhaan.kotlinpoetdsl.open


/**
 * This class tests if the ClassAcceptor is implemented correctly.
 * This is done by checking if:
 *  - the builders itself implements the interface correctly,
 *  - the accessors implements the interface correctly,
 *  - The delegation works correct.
 */
class ClassAcceptorTest : StringSpec({
    val zeroClass = TypeSpec.classBuilder("Clazz").build()
    val oneClass = TypeSpec.classBuilder("Clazz").addModifiers(KModifier.OPEN).build()
    val twoClass = TypeSpec.classBuilder("Clazz").addModifiers(KModifier.INTERNAL, KModifier.OPEN).build()


    "abstract builder with abstract method"{
        createClass{
            abstract.clazz("Clazz"){
                abstract.func("hi")
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addModifiers(KModifier.ABSTRACT)
                .addFunction(
                        FunSpec.builder("hi").addModifiers(KModifier.ABSTRACT).build()
                ).build()
    }

    "builder without modifier"{
        createClass {
            clazz("Clazz"){}
        } shouldBe zeroClass
    }

    "builder with modifier"{
        createClass {
            open.clazz("Clazz"){}
        } shouldBe oneClass
    }

    "file without modifier"{
        file("", "HelloWorld") {
            clazz("Clazz"){}
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(zeroClass)
                .build()
    }

    "file with initialized Modifier"{
        file("", "HelloWorld") {
            clazz(oneClass)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(oneClass)
                .build()
    }

    "file add modifier"{
        file("", "HelloWorld") {
            open.clazz(zeroClass)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(oneClass)
                .build()
    }

    "file merge modifier"{
        file("", "HelloWorld") {
            internal.clazz(oneClass)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addType(twoClass)
                .build()
    }

    "interface without modifier"{
        createInterface {
            interf("HelloWorld"){
                clazz("Clazz"){}
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(zeroClass)
                .build()
    }

    "interface with initialized modifier"{
        createInterface {
            interf("HelloWorld"){
                clazz(oneClass)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "interface add modifier"{
        createInterface {
            interf("HelloWorld"){
                open.clazz(zeroClass)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "interface merge modifier"{
        createInterface {
            interf("HelloWorld"){
                internal.clazz(oneClass)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(twoClass)
                .build()
    }
    "class without modifier"{
        createClass{
            clazz("HelloWorld") {
                clazz("Clazz"){}
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(zeroClass)
                .build()
    }

    "class with initialized Modifier"{
        createClass {
            clazz("HelloWorld") {
                clazz(oneClass)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "class add modifier"{
        createClass {
            clazz("HelloWorld") {
                open.clazz(zeroClass)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "class merge modifier"{
        createClass {
            clazz("HelloWorld") {
                internal.clazz(oneClass)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(twoClass)
                .build()
    }
})