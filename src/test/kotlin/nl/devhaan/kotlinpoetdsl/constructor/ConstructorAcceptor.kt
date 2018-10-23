package nl.devhaan.kotlinpoetdsl.constructor

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.classes.buildClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.constructorBuilder.ConstructorSpec
import nl.devhaan.kotlinpoetdsl.constructorBuilder.addConstructor
import nl.devhaan.kotlinpoetdsl.constructorBuilder.buildConstructor
import nl.devhaan.kotlinpoetdsl.constructorBuilder.constructor
import nl.devhaan.kotlinpoetdsl.final
import nl.devhaan.kotlinpoetdsl.private

class ConstructorAcceptor : StringSpec({
    val zeroConstr = ConstructorSpec.constructorBuilder(false).build()
    val oneConstr = ConstructorSpec.constructorBuilder(false).addModifiers(KModifier.PRIVATE).build()
    val twoConstr = ConstructorSpec.constructorBuilder(false).addModifiers(KModifier.FINAL, KModifier.PRIVATE).build()
    "builder without modifier"{
        buildConstructor {
            constructor{}
        } shouldBe zeroConstr
    }

    "builder with modifier"{
        buildConstructor {
            private.constructor{}
        } shouldBe oneConstr
    }

    "class without modifier"{
        buildClass {
            clazz("Clazz"){
                constructor()
            }
        } shouldBe TypeSpec.classBuilder("Clazz")
                .addConstructor(zeroConstr)
                .build()
    }

    "class with initialized modifier"{
        buildClass {
            clazz("Clazz"){
                constructor(oneConstr)
            }
        } shouldBe TypeSpec.classBuilder("Clazz")
                .addConstructor(oneConstr)
                .build()
    }

    "class add modifier"{
        buildClass {
            clazz("Clazz"){
                private.constructor(zeroConstr)
            }
        } shouldBe TypeSpec.classBuilder("Clazz")
                .addConstructor(oneConstr)
                .build()
    }

    "class merge modifier"{
        buildClass {
            clazz("Clazz"){
                final.constructor(oneConstr)
            }
        } shouldBe TypeSpec.classBuilder("Clazz")
                .addConstructor(twoConstr)
                .build()
    }
})