package treeProcessor

import treeGenerator.Node
import treeGenerator.State.*

class LoopHelper(val functions: ArrayList<Node>, val functionName: String = "main", val expressionHelper: ExpressionHelper) {
    lateinit var preOrder: PreOrder

    fun start(root: Node) { //return?
        preOrder = PreOrder(functions, functionName)

        when (root.value) {
            "while" -> {
                loop@ while (true) {
                    val value = with(expressionHelper) {
                        setUp()
                        start(root.links[0].links[0])
                        returnStack[0]
                    }
                    if (value == 0) break
                    else {
                        val state = preOrder.start(root.links[1])
                        if(state != null) {
                            when(state.second) {
                                BREAK -> break@loop
                                CONTINUE -> continue@loop
                            }
                        }
                    }
                }
            }
            "for" -> {
                var conditional = 0
                for(i in root.links[0].links.indices) {
                    val node = root.links[0].links[i]
                    if(node.state == NULL) {
                        conditional = i + 1
                        break
                    }
                    preOrder.start(node)
                }

                loop@ while(true) {
                    val value = with(expressionHelper) {
                        setUp()
                        start(root.links[0].links[conditional])
                        returnStack[0]
                    }
                    if(value == 0) break
                    else {
                        val state = preOrder.start(root.links[1])
                        if(state != null) {
                            when(state.second) {
                                BREAK -> break@loop
                                CONTINUE -> continue@loop
                            }
                        }
                        for(i in conditional+1 until root.links[0].links.size) {
                            val node = root.links[0].links[i]
                            expressionHelper.setUp()
                            expressionHelper.start(node)
                        }
                    }
                }


            }
        }
        currentVariableBlockIndex--
    }
}