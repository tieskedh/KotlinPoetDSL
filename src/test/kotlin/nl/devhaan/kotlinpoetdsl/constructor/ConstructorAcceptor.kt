package nl.devhaan.kotlinpoetdsl.constructor

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import nl.devhaan.kotlinpoetdsl.addConstructor
import nl.devhaan.kotlinpoetdsl.classes.createClass
import nl.devhaan.kotlinpoetdsl.classes.clazz
import nl.devhaan.kotlinpoetdsl.constructorBuilder.ConstructorSpec
import nl.devhaan.kotlinpoetdsl.constructorBuilder.createConstructor
import nl.devhaan.kotlinpoetdsl.constructorBuilder.constructor
import nl.devhaan.kotlinpoetdsl.final
import nl.devhaan.kotlinpoetdsl.private
import nl.devhaan.kotlinpoetdsl.public

class ConstructorAcceptor : StringSpec({
    val zeroConstr = ConstructorSpec.constructorBuilder(false).build()
    val oneConstr = ConstructorSpec.constructorBuilder(false).addModifiers(KModifier.PRIVATE).build()
    "builder without modifier"{
        createConstructor {
            constructor{}
        } shouldBe zeroConstr
    }

    "builder with modifier"{
        createConstructor {
            private.constructor{}
        } shouldBe oneConstr
    }

    "class without modifier"{
        createClass {
            clazz("Clazz"){
                constructor()
            }
        } shouldBe TypeSpec.classBuilder("Clazz")
                .addConstructor(zeroConstr)
                .build()
    }

    "class with modifier"{
        createClass {
            clazz("Clazz"){
                private.constructor()
            }
        } shouldBe TypeSpec.classBuilder("Clazz")
                .addConstructor(oneConstr)
                .build()
    }
})