package nl.devhaan.kotlinpoetdsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import nl.devhaan.kotlinpoetdsl.helpers.ParameterData

/**
 * Created by thijs on 16-6-2017.
 */
typealias Parameter = Pair<String, ParameterData>
typealias ContextTypeSpec = Pair<ClassName, TypeSpec>