package nl.devhaan.kotlinpoetdsl.parameters

import com.squareup.kotlinpoet.ParameterSpec

fun ParameterSpec.buildUpon(
    buildScript : ParameterSpec.Builder.()->Unit
)= toBuilder().also(buildScript).build()