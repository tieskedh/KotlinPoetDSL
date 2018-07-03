package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.buildClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file


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


    "builder without modifier"{
        buildClass{
            clazz("Clazz")
        } shouldBe zeroClass
    }

    "builder with modifier"{
        buildClass(KModifier.OPEN) {
            clazz("Clazz")
        } shouldBe oneClass
    }

    "file without modifier"{
        file("", "HelloWorld") {
            clazz("Clazz")
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


    "class without modifier"{
        buildClass {
            clazz("HelloWorld") {
                clazz("Clazz")
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