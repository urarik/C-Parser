package treeProcessor

import treeGenerator.Node
import treeGenerator.Precedence
import treeGenerator.Precedence.*
import treeGenerator.State
import treeGenerator.State.*
import treeProcessor.ExpressionHelper.Type.*
import treeProcessor.PreOrder.Companion.find

class ExpressionHelper(val functions: ArrayList<Node>) {
    enum class Type {Int, Char, String}
    val returnStack = mutableListOf<Any?>() //value, isInt? else character
    val nameStack = mutableListOf<String>() //variable name for ++, --

    fun setUp() {
        returnStack.clear()
        nameStack.clear()
    }

    fun start(root: Node) {
        for (link in root.links)
            start(link)

        when (root.state) {
            State.FUNCTION_CALL -> {
                val parameters = mutableListOf<Node>()
                for (link in root.links)
                    parameters.add(link)
                PreOrder(functions, root.value as String, parameters)
            }
            PRE_OPERATOR, POST_OPERATOR, OPERATOR -> {
                when ((root.value as Pair<*, *>).second) {
                    State.FUNCTION_CALL -> {

                    }
                    ARRAY -> {
                        val arrayIndex = removeLast()
                        val arrayName = removeLast()
                        for (i in currentVariableBlockIndex downTo 0)
                            for (j in variables[i].indices)
                                if (variables[i][j].first == arrayName) {
                                    if ((variables[i][j].third as List<*>).size > arrayIndex as Int)
                                        returnStack.add((variables[i][j].third as List<*>)[arrayIndex])
                                    else throw IllegalAccessError("Array out of bound!")
                                }
                    }
                    STRUCT -> {
                        var isExist = false
                        val member = removeLast()
                        val name = removeLast()

                        for (i in currentVariableBlockIndex downTo 0)
                            for (j in variables[i].indices)
                                if (variables[i][j].first == name) {
                                    isExist = true
                                    (variables[i][j].third as HashMap<*, *>)[member]?.let { returnStack.add(it) } // typecheck??
                                            ?: throw IllegalAccessException("There is no initiated struct memeber ${root.links[1].value}")
                                }
                        if (!isExist) throw IllegalAccessException("There is no variable ${root.value}")
                    }
                    NOT -> {
                        var operand = removeLast()
                        operand = if (isChar(operand)) (operand as Char).toInt()
                        else operand as Int
                        returnStack.add(if (operand == 0) 1 else 0)
                    }
                    UNARY -> {
                        val operand = removeLast() as Int
                        returnStack.add(if ((root.value as Pair<*, *>).first == "+")
                            if (operand > 0) operand else -operand
                        else if (operand > 0) -operand else operand)

                    }
                    DEREFERENCE -> {
                        //typeCheck
                        returnStack.add(removeLast())
                        /*val name = removeLast()
                        var isExist = false

                        for (i in currentVariableBlockIndex downTo 0)
                            for (j in variables[i].indices)
                                if (variables[i][j].first == name) {
                                    isExist = true
                                    returnStack.add(variables[i][j].second)
                                }
                        if (!isExist) throw IllegalAccessException("There is no variable ${root.value}")*/
                    }
                    TYPECAST -> {
                        // constant -> constant || variable -> constant
                        val target = removeLast()
                        val type = removeLast()
                        //use typechecker func?
                        if ((target as Pair<*, *>).second == State.CONSTANT) {
                        } else {
                        } //variable

                    }
                    MULTIPLICATIVE, BINARY, RELATIONAL, EQUALITY, AND, OR -> {
                        //%, * ,/
                        var second = removeLast()
                        second = if (isChar(second)) (second as Char).toInt()
                        else second as Int
                        var first = removeLast()
                        first = if (isChar(first)) (first as Char).toInt()
                        else first as Int

                        //typecheck instead of type conversion
                        when ((root.value as Pair<*, *>).first) {
                            "%" -> returnStack.add(first % second)
                            "*" -> returnStack.add(first * second)
                            "/" -> returnStack.add(first / second)
                            "+" -> returnStack.add(first + second)
                            "-" -> returnStack.add(first - second)
                            "<" -> returnStack.add(if (first < second) 1 else 0)
                            ">" -> returnStack.add(if (first > second) 1 else 0)
                            "<=" -> returnStack.add(if (first <= second) 1 else 0)
                            ">=" -> returnStack.add(if (first >= second) 1 else 0)
                            "==" -> returnStack.add(if (first == second) 1 else 0)
                            "!=" -> returnStack.add(if (first != second) 1 else 0)
                            "&&" -> returnStack.add(if ((first != 0) && (second != 0)) 1 else 0)
                            "||" -> returnStack.add(if ((first != 0) || (second != 0)) 1 else 0)
                        }
                    }
                    PREINCORDEC -> {
                        var first = returnStack.removeLast()
                        first = if (isChar(first)) (first as Char).toInt() + 1
                        else first as Int + 1

                        val variable = find(nameStack.removeLast())
                        variable?.let {
                            variable.third = (variable.third.toString().toInt() + 1).toString() //?
                        } ?: throw IllegalAccessException("no variable")

                        returnStack.add(first)
                    } 
                    POSTINCORDEC -> {
                        var first = returnStack.removeLast()
                        first = if (isChar(first)) (first as Char).toInt()
                        else first as Int

                        val variable = find(nameStack.removeLast())
                        variable?.let {
                            variable.third = (variable.third.toString().toInt() + 1).toString()
                        } ?: throw IllegalAccessException("no variable")

                        returnStack.add(first)
                    }
                }
    
/*                    
                    Precedence.ASSIGN -> {
                        val value = removeLast()
                        removeLast()
                        val name = root.links[0].value

                        for (i in currentVariableBlockIndex downTo 0)
                            for (j in variables[i].indices)
                                if (variables[i][j].first == name) {
                                    variables[i][j].third = value
                                }
                        returnStack.add(value)
                    }
                } */
            }
            State.CONSTANT -> {
                val value = root.value!! as String
                returnStack.add(if(value[0] in '0'..'9') value.toInt() else value[0].toInt())
                nameStack.add("")
            }
            State.VARIABLE -> {
                var isExist = false
                for (i in currentVariableBlockIndex downTo 0)
                    for (j in variables[i].indices)
                        if (variables[i][j].first == (root.value as Pair<*, *>).first) {
                            isExist = true
                            returnStack.add(variables[i][j].third.toString().toInt())
                            nameStack.add(variables[i][j].first)
                        }
                if (!isExist) throw IllegalAccessException("There is no variable ${root.value}")
            }
        }

        //returnStack.add()
    }
    fun isChar(any: Any?): Boolean {
        return any in 'a'..'z' || any in 'A'..'Z'
    }
    
    private fun removeLast(): Any? {
        if(nameStack.isNotEmpty()) nameStack.removeLast()
        return returnStack.removeLast()
    }

    fun process(root: Node): Any? {
        setUp()
        start(root)
        return returnStack[0]
    }

}

