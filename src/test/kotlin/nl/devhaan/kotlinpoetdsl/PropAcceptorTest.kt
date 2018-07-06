package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.buildClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.properties.buildProp
import nl.devhaan.kotlinpoetdsl.properties.prop

class PropAcceptorTest : StringSpec({
    val zeroProp = PropertySpec.builder("prop", String::class).build()
    val oneProp = PropertySpec.builder("prop", String::class, KModifier.FINAL).build()
    val twoProp = PropertySpec.builder("prop", String::class, KModifier.PUBLIC, KModifier.FINAL).build()

    "builder without modifier"{
        buildProp {
            prop("prop" valOf String::class)
        } shouldBe zeroProp
    }

    "builder with modifier"{
        buildProp(KModifier.FINAL) {
            prop("prop" valOf String::class)
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
        buildClass {
            clazz("HelloWorld") {
                prop("prop" valOf String::class)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(zeroProp)
                .build()
    }

    "class with initialized modifier"{
        buildClass {
            clazz("HelloWorld") {
                prop(oneProp)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "class add modifier"{
        buildClass {
            clazz("HelloWorld") {
                final.prop(zeroProp)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(oneProp)
                .build()
    }

    "class merge modifier"{
        buildClass {
            clazz("HelloWorld") {
                public.prop(oneProp)
            }
        } shouldBe TypeSpec.classBuilder("HelloWorld")
                .addProperty(twoProp)
                .build()
    }

})
