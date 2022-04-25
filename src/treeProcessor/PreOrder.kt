package treeProcessor

import treeGenerator.Node
import treeGenerator.State.*

val variables = mutableListOf<MutableList<Triple>>() //name, datatype, value
val arrays = mutableListOf<Pair<Int, MutableList<Any?>>>() //array size, contents
val structs = mutableListOf<Pair<String, List<Pair<String, Any?>>>>()
//struct variable name, struct name, HashMap<String, Any?>
var currentVariableBlockIndex = -1

class PreOrder(val functions: ArrayList<Node>, val functionName: String = "main", val parameters: List<Node>? = null) {
    companion object {
        fun find(name: String): Triple? {
            for (i in currentVariableBlockIndex downTo 0)
                for (j in variables[i].indices)
                    if (variables[i][j].first == name) {
                        return variables[i][j]
                    }
            return null
        }
    }

    val stateChecker = TypeChecker()
    lateinit var returnValue: Pair<String, Any>
    val expressionHelper = ExpressionHelper(functions)
    val ioHelper = IOHelper(expressionHelper)
    val conditionalHelper = ConditionalHelper(functions, functionName, expressionHelper)
    val loopHelper = LoopHelper(functions, functionName, expressionHelper)
    var currentFuncIndex = 0

    init {
        for (i in functions.indices)
            if (functions[i].value == functionName) currentFuncIndex = i
        currentVariableBlockIndex++
        if (currentVariableBlockIndex < variables.size) {
            variables[currentVariableBlockIndex].clear()
        } else {
            variables.add(mutableListOf())
        }
    }

    fun start(node: Node? = null): Pair<String, Any>? {
        return if(node != null) preOrder(node)
        else preOrder(functions[currentFuncIndex])
    }

    private fun preOrder(node: Node): Pair<String, Any>? { // return non-null if there is a return node.
        if (!process(node)) {
            when(node.state) {
                RETURN -> return Pair(functions[currentFuncIndex].value as String, node.value!!)
                BREAK, CONTINUE -> return Pair("", node.state)
            }
        } else for (link in node.links) {
            val result = preOrder(link)
            if (result != null) return result //RETURN
        }
        return null
    }

    private fun process(node: Node): Boolean { // return false if node is a root of expression . . .
        return when (node.state) {
            HEADER -> { // function header
                parameters?.let {
                    for (i in node.links.indices) {
                        processAssign(node.links[i], parameters[i])
                    }
                }
                false
            }
            FUNCTION_CALL -> {

                false
            }
            CONDITIONAL -> {
                conditionalHelper.start(node)
                false
            }
            ASSIGN -> {
                processAssign(node)
                false
            }
            LOOP -> {
                loopHelper.start(node)
                false
            }
            RETURN -> {

                false
            }
            IO -> {
                ioHelper.start(node)
                false
            }
            BREAK, CONTINUE -> {
                false
            }
            PRE_OPERATOR, POST_OPERATOR, OPERATOR -> {
                with(expressionHelper){
                    setUp()
                    start(node)
                }
                false
            }
            //NULL -> {}
            else -> true
        }

    }

    private fun processAssign(root: Node, parameter: Any? = null) {
        //pointer?
        var isArray = false
        val name = root.links[0].value as String
        val dataType = (root.links[1].value as Pair<*, *>).first as String?
        val expression = (root.links[1].value as Pair<*, *>).second as Node?
        val value = if(expression?.state != DYNAMIC) with(expressionHelper) {
            expression?.let {
                setUp()
                //println("....$expression")
                expressionHelper.start(expression)
                returnStack[0]
            } ?: parameter
        } else {
            if(expression.links.size != 0) {
                isArray = true
                println(expression.links[0])
                val size = expressionHelper.process(expression.links[0].value as Node)
                arrays.add(Pair(size as Int, mutableListOf()))
                arrays.last()
            } else parameter
        }

        if (dataType == null || dataType == "dynamic") { // function parameter or existing variable
            val variable = find(name)
            variable?.let {
                //typecheck
                variable.third = value.toString()
            } ?: throw IllegalAccessException("There is no $name")
        } else if(isArray) {
            variables[currentVariableBlockIndex].add(Triple(name, dataType, value)) //datatype의 *로 인식
        }
        else {
            variables[currentVariableBlockIndex].add(Triple(name, dataType, value.toString()))
        }
    }


}
