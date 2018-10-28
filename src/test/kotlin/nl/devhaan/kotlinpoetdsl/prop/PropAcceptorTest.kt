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
    val twoProp = PropertySpec.builder("prop", String::class, KModifier.PUBLIC, KModifier.FINAL).build()

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

    "file with initialized modifier"{
        file("", "HelloWorld") {
            prop(oneProp)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "file add modifier"{
        file("", "HelloWorld") {
            final.prop(zeroProp)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "file merge modifier"{
        file("", "HelloWorld") {
            public.prop(oneProp)
        } shouldBe FileSpec.builder("", "HelloWorld")
                .addProperty(twoProp)
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

    "class with initialized modifier"{
        createClass {
            clazz("HelloWorld") {
                prop(oneProp)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "class add modifier"{
        createClass {
            clazz("HelloWorld") {
                final.prop(zeroProp)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "class merge modifier"{
        createClass {
            clazz("HelloWorld") {
                public.prop(oneProp)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(twoProp)
                .build()
    }

})
