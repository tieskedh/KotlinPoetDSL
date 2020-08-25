package nl.devhaan.kotlinpoetdsl.prop

import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import nl.devhaan.kotlinpoetdsl.files.file
import nl.devhaan.kotlinpoetdsl.getters.getter
import nl.devhaan.kotlinpoetdsl.inline
import nl.devhaan.kotlinpoetdsl.of
import nl.devhaan.kotlinpoetdsl.properties.createProp
import nl.devhaan.kotlinpoetdsl.properties.prop
import nl.devhaan.kotlinpoetdsl.setters.setter
import nl.devhaan.kotlinpoetdsl.valOf
import nl.devhaan.kotlinpoetdsl.varOf

/**
 * KotlinPoetDsl treats inline properties a bit different than KotlinPoet.
 *
 * KotlinPoetDsl allows to add inline to the property itself, with strict limitations:
 * - When the property is mutable, it will require: an inline setter, an inline getter and no initialization
 * - When the property is not mutable, it offers two options:
 *     1. The property needs to be initialized (the initialization will be placed in the getter)
 *     2. The property needs to have an inlined getter
 *
 * Because of this, KotlinPoetDsl needs to throw some errors himself.
 */
class PropErrorTest : StringSpec({
    "inlined var-prop without getter and setter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop".varOf<String>("\"hello\""))
            }
        }.message shouldBe "No inline getter and setter provided for inline var-property prop."
    }
    "inlined var-prop without getter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop" varOf String::class){
                    inline.setter{
                        statement("println(1)")
                    }
                }
            }
        }.message shouldBe "No inline getter provided for inline var-property prop."
    }
    "inlined var-prop without setter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop" varOf String::class){
                    inline.getter{
                        statement("println(1)")
                    }
                }
            }
        }.message shouldBe "No inline setter provided for inline var-property prop."
    }


    /* This is only needed due to the implementation-flow of properties */
    "val-prop with setter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop" valOf Int::class) {
                    setter { statement("return 1") }
                }
            }
        }.message shouldBe "Val prop cannot have setter."
    }

    "inlined initialized prop with inlined getter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop".valOf<Int>("1")) {
                    inline.getter {
                        statement("return 1")
                    }
                }
            }
        }.message shouldBe "Inlined property prop cannot have initializer and getter."
    }

    "inlined initialized prop with inlined setter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop".valOf<Int>("1")) {
                    inline.setter {
                        statement("return 1")
                    }
                }
            }
        }.message shouldBe "Inlined property prop cannot have initializer and setter."
    }

    "inline uninitialized prop without getters and setters"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop" of Int::class)
            }
        }.message shouldBe "Inlined property must have getters or setters or be initialized."
    }

    "setter not inlined"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop" valOf Int::class){
                    getter {
                        statement("return 1")
                    }
                }
            }
        }.message shouldBe "Getter for inline property 'prop' must be inlined."
    }
    "getter not-inlined getter"{
        shouldThrow<IllegalArgumentException> {
            createProp {
                inline.prop("prop" valOf Int::class) {
                    getter { statement("return 1") }
                }
            }
        }.message shouldBe "Getter for inline property 'prop' must be inlined."
    }
})