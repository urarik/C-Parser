package treeGenerator

import jdk.jfr.ContentType
import treeGenerator.Tokens.*
import treeGenerator.Precedence.*

enum class Precedence(val isp: Int, val icp: Int) { //increment, decrement는 prefix냐 postfix냐에 따라 달라짐.
    LEFT_PARENTHESIS(0, 20), RIGHT_PARENTHESIS(19, 19), ARRAY(19, 19), FUNCTION_CALL(19, 19), STRUCT(19, 19),
    PREINCORDEC(18, 18), POSTINCORDEC(18, 18),
    NOT(16, 17), UNARY(16, 17), DEREFERENCE(16, 17),
    TYPECAST(14, 15),
    MULTIPLICATIVE(13, 13), BINARY(12, 12),
    RELATIONAL(10, 10),
    EQUALITY(9, 9),
    AND(8, 8), OR(7, 7),
    ASSIGN(2, 3),
    OPERAND(1, 1), VARIABLE(1, 1), NOTHING(0, 0), CONSTANT(0,0);
    //val inStackPrecedence = arrayListOf(0, 19, 19, 18, 18, 16, 16, 16, 14, 13, 12, 10, 9, 8, 7, 2)
    //val inComingPrecedence = arrayListOf(20, 19, 19, 18, 18, 17, 17, 17, 15, 13, 12, 10, 9, 8, 7, 3)
}

class ExpressionHelper {
    var expression: ArrayList<Pair<String, Precedence?>> = arrayListOf()
    val postfix: ArrayList<Pair<Any, Precedence>> = arrayListOf()
    val operator: ArrayList<Pair<String, Precedence>> = arrayListOf()
    val nodeStack: ArrayList<Node> = arrayListOf()
    val dataType: StringBuilder = StringBuilder()
    var expressionStack: Int = 0
    var prevPrecedence: Precedence = NOTHING
    var arrayIndex = 0
    var isTypeConversion = false
    var isConstant = -1

    fun processExpression(token: Tokens, str: String) {
        if(str == "{")
            return
        if(isTypeConversion) {
            if(str == ")") {
                expression.add(Pair(dataType.toString(), TYPECAST))
                dataType.clear()
                isTypeConversion = false
            } else dataType.append("$str ")
            return
        }

        when (token) {
            DELIMITER -> {
                expression.add(Pair(str, null))
            }
            OPERATOR -> {
                expression.add(Pair(str, null))
            }
            DATA_TYPE -> { //need to consider type conversion
                isTypeConversion = true
                expression.removeLast()
                dataType.append("$str ")
            }
            NAME -> {
                expression.add(Pair(str, null))
            }

        }
    }

    fun processBracketExpression(token: Tokens, str: String): Boolean {
        processExpression(token, str)
        //println(str)
        if (token == DELIMITER) {
            if (str == "(") {
                expressionStack++
            } else if (str == ")") {
                if (--expressionStack == 0) { //conditional/loop -> EXPRESSION(header) -> BODY -> add node
                    makePostFix()
                    makeTree()
                    return true
                }
            }

        }
        return false
    }

    fun makePostFix() {
        var functionCall: ArrayList<String>? = null
        var functionStack = 0
        var isIncOrDec = false // ++, --

        for ((token, typeCast) in expression) {
            var precedence = typeCast ?: getPrecedence(token)

            if(typeCast != null) {
                postfix.add(Pair(token, CONSTANT))
                precedence = TYPECAST
            }
            if(prevPrecedence == ARRAY || precedence == ARRAY) {
                if(token != "]") {
                    prevPrecedence = ARRAY
                    if (token != "[") {
                        arrayIndex = token.toInt()
                    }
                } else {
                    prevPrecedence = OPERAND // assign precedence something to exit if statement
                    continue
                }

            }
            if (isIncOrDec) {
                if (precedence == OPERAND) {
                    val temp = operator.removeLast()
                    operator.add(Pair(temp.first, PREINCORDEC))
                }
                isIncOrDec = false
            }
            if (precedence == POSTINCORDEC) isIncOrDec = true

            if (precedence == DEREFERENCE) {
                if (prevPrecedence == OPERAND) precedence = MULTIPLICATIVE
            } else if (precedence == UNARY)
                if (prevPrecedence == OPERAND || prevPrecedence == PREINCORDEC || precedence == POSTINCORDEC) precedence = BINARY
            if (prevPrecedence == FUNCTION_CALL) {
                functionCall!!.add(token)
                if (token == "(") {
                    functionStack++
                    continue
                } else if (token == ")" && --functionStack == 0) {
                    functionCall.removeLast()
                    precedence = OPERAND
                } else continue
            }
            if (prevPrecedence == OPERAND && token == "(") {
                functionCall = if (postfix.isNotEmpty()) arrayListOf(postfix.removeLast().first as String) else arrayListOf()
                prevPrecedence = FUNCTION_CALL
                functionStack = 1
                continue
            }
            if (precedence == OPERAND) {
                if (token == "'") {
                    isConstant = if(isConstant == 1) -1 else 0
                    continue
                }

                if (functionCall != null) {
                    postfix.add(Pair(functionCall, FUNCTION_CALL))
                    functionCall = null
                } else {
                    postfix.add(if(isConstant == 0 || isNumeric(token)) {
                        isConstant = 1
                        Pair(token, CONSTANT)
                    }else Pair(token, if(isConstant == -1) VARIABLE else precedence))
                }
            } else if (precedence == RIGHT_PARENTHESIS) {
                while (operator.last().second != LEFT_PARENTHESIS)
                    postfix.add(operator.customRemoveLast())
                operator.removeLast()
            } else {
                while (operator.isNotEmpty() && operator.last().second.isp >= precedence.icp)
                    postfix.add(operator.customRemoveLast())

                operator.add(Pair(token, precedence))
            }
            prevPrecedence = precedence
        }
        while (operator.isNotEmpty()) postfix.add(operator.customRemoveLast())

        println(postfix)
    }

    private fun ArrayList<Pair<String, Precedence>>.customRemoveLast(): Pair<String, Precedence> {
        var last = this.removeLast()
        if(last.second == ARRAY) last = Pair(arrayIndex.toString(), ARRAY)
        return last
    }

    fun makeTree(): Node {
        if (postfix.size == 0) {
            return makeNullNode()
        }
        nodeStack.clear()
        for (token in postfix) {
            val node = when {
                token.second == FUNCTION_CALL -> {
                    val name = (token.first as List<*>)[0]
                    val temp = Node(State.FUNCTION_CALL, name)
                    val headerExpression: ExpressionHelper = ExpressionHelper().apply { setUp() }
                    for (i in 1..(token.first as List<*>).size) {
                        if (i == (token.first as List<*>).size || (token.first as List<*>)[i] == ",") {
                            headerExpression.makePostFix()
                            temp.links.add(headerExpression.makeTree())
                            headerExpression.setUp()
                            continue
                        }
                        headerExpression.processBracketExpression(getToken((token.first as List<*>)[i] as String), (token.first as List<*>)[i] as String)
                    }
                    temp
                }
                token.second == PREINCORDEC -> Node(State.PRE_OPERATOR, token)
                token.second == POSTINCORDEC -> Node(State.POST_OPERATOR, token)
                token.second == VARIABLE -> Node(State.VARIABLE, token)
                token.second == CONSTANT || token.second == OPERAND -> Node(State.CONSTANT, token.first)
                else ->
                    Node(State.OPERATOR, token) // OPERATOR / CONSTANT(char, int) / VARIABLE
            }

            if (token.second == OPERAND || token.second == CONSTANT || token.second == VARIABLE || token.second == FUNCTION_CALL) {
            } else if (isUnary(token.second)) {
                node.links.add(nodeStack.removeLast())
            } else {
                node.links.add(nodeStack[nodeStack.size - 2])
                node.links.add(nodeStack.removeLast())
                nodeStack.removeLast()
            }
            nodeStack.add(node)
        }
        //inOrder(nodeStack[0])
        //println()
        return nodeStack[0]
    }

    private fun inOrder(node: Node) {
        if (node.links.size == 1) {
            print("${node.value} ")
            if (node.links.size > 0) inOrder(node.links[0])
        } else {
            if (node.links.size > 0) inOrder(node.links[0])
            print("${node.value} ")
        }
        if (node.links.size == 2) inOrder(node.links[1])
    }

    private fun isNumeric(str: String): Boolean {
        for (char in str) {
            if (char in '0'..'9') continue
            else return false
        }
        return true
    }

    private fun isUnary(precedence: Precedence): Boolean {
        return when (precedence) {
            UNARY, PREINCORDEC, POSTINCORDEC, NOT, DEREFERENCE -> true
            else -> false
        }
    }

    fun setUp() {
        expressionStack = 0
        prevPrecedence = NOTHING
        isConstant = -1
        expression.clear()
        postfix.clear()
        operator.clear()
    }

    private fun getPrecedence(token: String): Precedence {
        return when (token) {
            "(" -> LEFT_PARENTHESIS
            ")" -> RIGHT_PARENTHESIS
            "->", "." -> STRUCT
            "[", "]" -> ARRAY
            "++", "--" -> POSTINCORDEC
            "!" -> NOT
            "-", "+" -> UNARY
            "*" -> DEREFERENCE
            "%", "*", "/" -> MULTIPLICATIVE
            "+", "-" -> BINARY
            "<", ">", "<=", ">=" -> RELATIONAL
            "==", "!=" -> EQUALITY
            "&&" -> AND
            "||" -> OR
            "=" -> ASSIGN
            else -> OPERAND
        }
    }

    private fun makeNullNode(): Node {
        return Node(State.NULL, null)
    }
}