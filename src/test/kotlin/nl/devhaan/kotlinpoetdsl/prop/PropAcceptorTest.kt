package nl.devhaan.kotlinpoetdsl.prop

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.final
import nl.devhaan.kotlinpoetdsl.properties.createProp
import nl.devhaan.kotlinpoetdsl.properties.prop
import nl.devhaan.kotlinpoetdsl.public
import nl.devhaan.kotlinpoetdsl.valOf

class PropAcceptorTest : StringSpec({
    val zeroProp = PropertySpec.builder("prop", String::class).build()
    val oneProp = PropertySpec.builder("prop", String::class, KModifier.FINAL).build()

    "builder without modifier"{
        createProp {
            prop("prop" valOf String::class)
        } shouldBe zeroProp
    }

    "builder with modifier"{
        createProp {
            final.prop("prop" valOf String::class)
        } shouldBe oneProp
    }

    "file without modifier"{
        file("", "HelloWorld") {
            prop("prop" valOf String::class)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(zeroProp)
                .build()
    }

    "file with modifier"{
        file("", "HelloWorld") {
            final.prop("prop" valOf String::class)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "class without modifier"{
        createClass {
            clazz("HelloWorld") {
                prop("prop" valOf String::class)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(zeroProp)
                .build()
    }

    "class with modifier"{
        createClass {
            clazz("HelloWorld") {
                final.prop("prop" valOf String::class)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(oneProp)
                .build()
    }
})
