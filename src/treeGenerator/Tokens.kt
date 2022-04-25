package treeGenerator

fun getToken(str: String) : Tokens {
    return when(str) {
        " ", "\"", "\'", "(", ")", ";", ",", ".", "{", "}", ":" -> Tokens.DELIMITER
        //"ptr", "temp", "root" -> TreeGenerator.Tokens.NEED_NODE_DRAWING
        "int", "char", "void", "struct" -> Tokens.DATA_TYPE // struct와 *는 추후 생각
        "+", "-", "/", "*", "%", "=", "++", "--",
        "!", "&&", "||" ,
        "<", ">", "==", "!=", "<=", ">=",
        "->", "[", "]" -> Tokens.OPERATOR
        "printf", "scanf" -> Tokens.INPUT_OUTPUT
        "if", "else", "switch" -> Tokens.CONDITIONAL
        "for", "while" -> Tokens.LOOP
        "return", "case", "break", "continue", "default" -> Tokens.KEYWORD // 추후 사용자가 바꿀 수 있게
        //"addq", "delq", "push", "pop" -> TreeGenerator.Tokens.COLLECTION
        else -> Tokens.NAME
    }
}
fun getOperatorType(str: String): Tokens {
    return when(str) {
        "+", "-", "/", "*", "%", "=" -> Tokens.ARITHMETIC_OPERATOR
        "!", "&&", "||" -> Tokens.LOGICAL_OPERATOR
        "<", ">", "==", "!=", "<=", ">=" -> Tokens.RELATIONAL_OPERATOR
        "->", "[", "]", "*" -> Tokens.REFERENCE_OPERATOR //dereferencing operator *?
        else -> throw IllegalAccessException()
    }
}
fun isLogical(char: Char): Boolean {
    return when(char) {
        '!', '<', '>', '=' -> true
        else -> false
    }
}

enum class Tokens {
    DELIMITER, NEED_NODE_DRAWING, DATA_TYPE, OPERATOR, ARITHMETIC_OPERATOR, LOGICAL_OPERATOR, REFERENCE_OPERATOR, RELATIONAL_OPERATOR, INPUT_OUTPUT, CONDITIONAL, LOOP, KEYWORD, NAME, COLLECTION;
}