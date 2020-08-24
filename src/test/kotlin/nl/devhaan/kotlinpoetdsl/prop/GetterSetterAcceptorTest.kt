package nl.devhaan.kotlinpoetdsl.prop

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import nl.devhaan.kotlinpoetdsl.getters.createGetter
import nl.devhaan.kotlinpoetdsl.getters.getter
import nl.devhaan.kotlinpoetdsl.inline
import nl.devhaan.kotlinpoetdsl.properties.createProp
import nl.devhaan.kotlinpoetdsl.properties.prop
import nl.devhaan.kotlinpoetdsl.setters.createSetter
import nl.devhaan.kotlinpoetdsl.setters.setter
import nl.devhaan.kotlinpoetdsl.varOf

class GetterSetterAcceptorTest : StringSpec({
    val setterWithoutModifier = FunSpec.setterBuilder()
            .addStatement("println(1)")
            .addParameter("value", Any::class)
            .build()
    val getterWithoutModifier = FunSpec.getterBuilder()
            .addStatement("return 1")
            .build()
    val setterWithModifier = FunSpec.setterBuilder()
            .addStatement("println(1)")
            .addParameter("value", Any::class)
            .addModifiers(KModifier.INLINE)
            .build()

    val getterWithModifier = FunSpec.getterBuilder()
            .addStatement("return 1")
            .addModifiers(KModifier.INLINE)
            .build()

    "setterBuilder with modifier"{
        createSetter {
            inline.setter {
                statement("println(1)")
            }
        } shouldBe setterWithModifier
    }
    "getterBuilder with modifier"{
        createGetter {
            inline.getter {
                statement("return 1")
            }
        } shouldBe getterWithModifier
    }
    "setterBuilder without modifier"{
        createSetter {
            setter {
                statement("println(1)")
            }
        } shouldBe setterWithoutModifier
    }
    "getterBuilder without modifier"{
        createGetter {
            getter {
                statement("return 1")
            }
        } shouldBe getterWithoutModifier
    }


    "prop with modifier"{
        createProp {
            inline.prop("prop" varOf Int::class) {
                inline.setter {
                    statement("println(1)")
                }
                inline.getter {
                    statement("return 1")
                }
            }
        } shouldBe PropertySpec.builder("prop", Int::class)
                .mutable(true)
                .getter(getterWithModifier)
                .setter(setterWithModifier)
                .build()
    }

    "prop without modifier"{
        createProp {
            prop("prop" varOf Int::class) {
                getter {
                    statement("return 1")
                }
                setter {
                    statement("println(1)")
                }
            }
        } shouldBe PropertySpec.builder("prop", Int::class)
                .mutable(true)
                .getter(getterWithoutModifier)
                .setter(setterWithoutModifier)
                .build()
    }
})
