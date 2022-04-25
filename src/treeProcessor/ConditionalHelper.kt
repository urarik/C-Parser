package treeProcessor

import treeGenerator.Node
import treeGenerator.State

class ConditionalHelper(val functions: ArrayList<Node>, val functionName: String = "main", val expressionHelper: ExpressionHelper) {
    lateinit var preOrder: PreOrder
    var previousIf = true

    fun start(root: Node) { //return?
        preOrder = PreOrder(functions, functionName)
        when(root.value) {
            "if" -> {
                setUp()
                checkAndExecute(root)
            }
            "else if" -> {
                if(!previousIf) {
                    checkAndExecute(root)
                }
            }
            "else" -> {
                if(!previousIf) {
                    preOrder.start(root.links[1])
                    currentVariableBlockIndex--
                }
            }
            "switch" -> { //break?
                val value = with(expressionHelper) {
                    setUp()
                    start(root.links[0].links[0])
                    returnStack[0]
                }
                for(case in root.links[1].links) {
                    val a = if(case.value as String in "a".."z" || case.value as String in "A".."Z") (case.value as String)[0].toInt()
                        else (case.value as String).toInt()

                    if((case.state == State.DEFAULT) || (a == value)) {
                        preOrder = PreOrder(functions, functionName)
                        preOrder.start(case)
                        currentVariableBlockIndex--
                        return
                    }
                }

            }
        }
    }
    private fun checkAndExecute(root: Node) {
        val value = with(expressionHelper) {
            setUp()
            start(root.links[0].links[0])
            returnStack[0]
        }
        if (value == 0) { // false header
            previousIf = false
            return
        } else {
            previousIf = true
            preOrder.start(root.links[1])
        }

        currentVariableBlockIndex--
    }

    private fun setUp() {
        previousIf = true
    }
}