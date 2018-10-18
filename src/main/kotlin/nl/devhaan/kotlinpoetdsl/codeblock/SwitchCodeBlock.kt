package nl.devhaan.kotlinpoetdsl.codeblock

import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper

private fun _switch(format: String, vararg parts: Any, switchBuilder: SwitchDSL.()->Unit) = buildLazyComponent{ builder->
    if (format == ""){
        builder.beginControlFlow("when")
    } else {
        builder.beginControlFlow("when($format)", parts)
    }
    SwitchDSL(builder).switchBuilder()
    builder.endControlFlow()
}



interface ISwitch {
    fun switch(format: String = "", vararg parts: Any, switchBuilder: SwitchDSL.()->Unit)
}

class SwitchStart(private val builder: BlockWrapper<*, *>) : ISwitch{
    override fun switch(format: String, vararg parts: Any, switchBuilder: SwitchDSL.() -> Unit) {
        _switch(format, parts, switchBuilder = switchBuilder).wrapper(builder)
    }
}


@Suppress("unused")
fun LazyComponentAcceptor.switch(format: String = "", vararg parts: Any, switchBuilder: SwitchDSL.() -> Unit) = _switch(format, parts, switchBuilder = switchBuilder)




@CodeBlockLevel
class SwitchDSL(private val builder: BlockWrapper<*, *>) : LazyComponentAcceptor{
    private val cbb = CodeBlockBuilder(builder)

    @JvmName("thenComponent")
    infix fun String.then(lazyComponent: LazyComponent) {
        builder.beginControlFlow("$this ->")
        lazyComponent.wrapper(builder)
        builder.endControlFlow()
    }

    infix fun String.then(statements: CodeBlockBuilder.() -> Unit){
        builder.beginControlFlow("$this ->")
        cbb.statements()
        builder.endControlFlow()
    }
    fun String.then(format: String, vararg parts : Any) = then{ statement(format, *parts) }
    infix fun String.then(statement: String) = then{statement.statement()}

    fun case(format: String, vararg parts: Any, statements: CodeBlockBuilder.()->Unit){
        builder.beginControlFlow("$format ->", parts)
        cbb.statements()
        builder.endControlFlow()
    }
    fun Else(statements: CodeBlockBuilder.() -> Unit) = case("else", statements = statements)
    fun Else(string: String) = Else{string.statement()}
}