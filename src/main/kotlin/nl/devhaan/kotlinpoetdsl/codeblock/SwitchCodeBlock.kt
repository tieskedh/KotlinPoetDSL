package nl.devhaan.kotlinpoetdsl.codeblock

import com.squareup.kotlinpoet.CodeBlock
import nl.devhaan.kotlinpoetdsl.CodeBlockLevel
import nl.devhaan.kotlinpoetdsl.helpers.BlockWrapper
import nl.devhaan.kotlinpoetdsl.helpers.createCodeBlock

private fun _switch(format: String, vararg parts: Any, prefix: CodeBlock, postFix: CodeBlock, switchBuilder: SwitchDSL.() -> Unit) = createLazyComponent(true) { builder ->
    if (format == "") {
        builder.beginControlFlow("%Lwhen", prefix)
    } else {
        builder.beginControlFlow("%Lwhen($format)", prefix, *parts)
    }
    SwitchDSL(builder).switchBuilder()
    builder.endControlFlow("%L", postFix)
}


interface ISwitch {
    fun switch(
            format: String = "",
            vararg parts: Any,
            prefix: String = "",
            postFix: String = "",
            switchBuilder: SwitchDSL.() -> Unit
    ) = switch(format, *parts, prefix = CodeBlock.of(prefix), postFix = CodeBlock.of(postFix), switchBuilder = switchBuilder)

    fun switch(
            format: String,
            vararg parts: Any,
            prefix: CodeBlock,
            postFix: CodeBlock,
            switchBuilder: SwitchDSL.() -> Unit
    )
}

class SwitchStart(private val builder: BlockWrapper<*, *, *>) : ISwitch {
    override fun switch(format: String, vararg parts: Any, prefix: CodeBlock, postFix: CodeBlock, switchBuilder: SwitchDSL.() -> Unit) {
        _switch(format, *parts, prefix = prefix, postFix = postFix, switchBuilder = switchBuilder).wrapper(builder)
    }
}


@Suppress("unused")
fun LazyComponentAcceptor.switch(
        format: String = "",
        vararg parts: Any,
        prefix: String = "",
        postFix: String = "",
        switchBuilder: SwitchDSL.() -> Unit
) = switch(format, parts, switchBuilder = switchBuilder, postFix = CodeBlock.of(postFix), prefix = CodeBlock.of(prefix))

@Suppress("unused")
fun LazyComponentAcceptor.switch(
        format: String,
        vararg parts: Any,
        prefix: CodeBlock,
        postFix: CodeBlock,
        switchBuilder: SwitchDSL.() -> Unit
) = _switch(format, *parts, prefix = prefix, postFix = postFix, switchBuilder = switchBuilder)

@CodeBlockLevel
class SwitchDSL(private val builder: BlockWrapper<*, *, *>) : LazyComponentAcceptor {
    @JvmName("thenComponent")
    infix fun String.then(lazyComponent: LazyComponent) =
            case(this, body = lazyComponent.toCodeBlock(), needBrackets = lazyComponent.singleStatement)


    private fun CodeBlock.singleLined() = !toString().dropLast(1).run{
        contains('\n') || contains(';')
    }
    infix fun String.then(statements: CodeBlockBuilder.() -> Unit) =
            case(this, body = createCodeBlock(statements))

    infix fun String.then(codeBlock: CodeBlock) = case(this, body = codeBlock)
    fun String.then(format: String, vararg parts: Any) = then { statement(format, *parts) }
    infix fun String.then(statement: String) = then { statement.statement() }

    fun case(
            format: String,
            vararg parts: Any,
            body: CodeBlock,
            needBrackets: Boolean = body.singleLined()
    ) {
        if (needBrackets) {
            if (parts.isEmpty()) builder.addCode("$format -> %L", body)
            else builder.addCode("$format -> %L", arrayOf(*parts, body))
        } else {
            builder.beginControlFlow("$format ->", *parts)
            builder.addCode(body)
            builder.endControlFlow()
        }
    }


    fun case(format: String, vararg parts: Any, statements: CodeBlockBuilder.() -> Unit) =
            case(format, *parts, body = createCodeBlock(statements))


    infix fun Else(codeBlock: CodeBlock) = case("else", body = codeBlock)
    infix fun Else(lazyComponent: LazyComponent) = case(
            "else",
            body = lazyComponent.toCodeBlock(),
            needBrackets = lazyComponent.singleStatement
    )

    fun Else(statements: CodeBlockBuilder.() -> Unit) = case("else", statements = statements)
    fun Else(string: String, vararg parts: Any) = Else { string.statement(*parts) }
}