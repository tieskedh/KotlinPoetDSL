package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.CodeBlock
import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock

private fun _switch(format: String, vararg parts: Any, switchBuilder: SwitchDSL.()->Unit) = createLazyComponent(true){ builder->
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

class SwitchStart(private val builder: BlockWrapper<*, *, *>) : ISwitch{
    override fun switch(format: String, vararg parts: Any, switchBuilder: SwitchDSL.() -> Unit) {
        _switch(format, parts, switchBuilder = switchBuilder).wrapper(builder)
    }
}


@Suppress("unused")
fun LazyComponentAcceptor.switch(format: String = "", vararg parts: Any, switchBuilder: SwitchDSL.() -> Unit) = _switch(format, parts, switchBuilder = switchBuilder)

@CodeBlockLevel
class SwitchDSL(private val builder: BlockWrapper<*, *, *>) : LazyComponentAcceptor{
    @JvmName("thenComponent")
    infix fun String.then(lazyComponent: LazyComponent) =
        case(this, body = lazyComponent.toCodeBlock(), needBrackets = lazyComponent.singleStatement)


    private fun CodeBlock.singleLined() = !toString().dropLast(1).contains('\n')
    infix fun String.then(statements: CodeBlockBuilder.() -> Unit) =
            case(this, body= createCodeBlock(statements))

    fun String.then(format: String, vararg parts : Any) = then{ statement(format, *parts) }
    infix fun String.then(statement: String) = then{statement.statement()}

    fun case(
            format: String,
            vararg parts: Any,
            body: CodeBlock,
            needBrackets : Boolean= body.singleLined()
    ){
        if (needBrackets){
            if (parts.isEmpty()) builder.addCode("$format -> %L", body)
            else builder.addCode("$format -> %L", arrayOf(*parts, body))
        } else {
            builder.beginControlFlow("$format ->", *parts)
            builder.addCode(body)
            builder.endControlFlow()
        }
    }

    fun case(format: String, vararg parts: Any, statements: CodeBlockBuilder.()->Unit) =
            case(format, *parts, body = createCodeBlock(statements))

    infix fun Else(lazyComponent: LazyComponent) =
            case("else", body = lazyComponent.toCodeBlock(), needBrackets = lazyComponent.singleStatement)

    fun Else(statements: CodeBlockBuilder.() -> Unit) = case("else", statements = statements)
    fun Else(string: String) = Else{string.statement()}
}