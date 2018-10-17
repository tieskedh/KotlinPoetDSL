package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.`interface`.buildInterface
import nl.devhaan.kotlinpoetdsl.`interface`.interf
import nl.devhaan.kotlinpoetdsl.classes.buildClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.functions.func


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
        buildClass{
            abstract.clazz("Clazz"){
                abstract.func("hi"){}
            }
        } shouldBe TypeSpec.classBuilder("Clazz").addModifiers(KModifier.ABSTRACT)
                .addFunction(
                        FunSpec.builder("hi").addModifiers(KModifier.ABSTRACT).build()
                ).build()
    }

    "builder without modifier"{
        buildClass {
            clazz("Clazz"){}
        } shouldBe zeroClass
    }

    "builder with modifier"{
        buildClass {
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
        buildInterface {
            interf("HelloWorld"){
                clazz("Clazz"){}
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(zeroClass)
                .build()
    }

    "interface with initialized modifier"{
        buildInterface {
            interf("HelloWorld"){
                clazz(oneClass)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "interface add modifier"{
        buildInterface {
            interf("HelloWorld"){
                open.clazz(zeroClass)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "interface merge modifier"{
        buildInterface {
            interf("HelloWorld"){
                internal.clazz(oneClass)
            }
        } shouldBe TypeSpec.interfaceBuilder("HelloWorld")
                .addType(twoClass)
                .build()
    }
    "class without modifier"{
        buildClass{
            clazz("HelloWorld") {
                clazz("Clazz"){}
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(zeroClass)
                .build()
    }

    "class with initialized Modifier"{
        buildClass {
            clazz("HelloWorld") {
                clazz(oneClass)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "class add modifier"{
        buildClass {
            clazz("HelloWorld") {
                open.clazz(zeroClass)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(oneClass)
                .build()
    }

    "class merge modifier"{
        buildClass {
            clazz("HelloWorld") {
                internal.clazz(oneClass)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addType(twoClass)
                .build()
    }
})