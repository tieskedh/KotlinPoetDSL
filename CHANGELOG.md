V0.2.0

# breaking changes
- replaces "build" in functions now start with "create": KotlinPoet started using build
- lazyComponent now needs a parameter which says if the component can be invoked as single statement (no ; is needed)

# additions
- added repeat(TIMES, ARG, BODY) to codeblock
- added String.addCode, String.addMarginedCode and String.addIndentedCode()
- switch has now pre- and postfix
- switch-cases now accept more arguments
- Adds topLevel Getter and  Setter

# improvements
- switch now emits brack
- Variable now supports inline