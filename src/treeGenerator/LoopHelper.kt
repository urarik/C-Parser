package treeGenerator

import treeGenerator.StateProcess.*

class LoopHelper(val variables: Variables, val expressionHelper: ExpressionHelper) {
    var isFor = false
    var isFirst = false
    var isBlock = false
    var isEnd = false
    var isDeclaration = true
    var forStack = 0
    lateinit var basicProcessor: BasicProcessor
    private lateinit var currentNode: Node
    val headerVariables = Variables()
    lateinit var assignHelper: AssignHelper

    fun processLoop(token: Tokens, str: String): Boolean {
        if (isFirst) {
            if (str == "{") {
                isBlock = true
                isFirst = false
            } else if (str == ";") isFirst = false
        }

        if (variables.state == HEADER) {
            if (!isFor) {
                if (expressionHelper.processBracketExpression(token, str)) {
                    endCalculate(false)
                }
            } else {
                if (isDeclaration) {
                    assignHelper.processAssign(token, str)
                    if (str == ";") {
                        expressionHelper.setUp()
                        if (currentNode.links[0].links.size == 0)
                            currentNode.links[0].links.add(Node(State.NULL, null))
                        currentNode.links[0].links.add(Node(State.NULL, null))
                        isDeclaration = false
                    }
                } else when (str) {
                    "(" -> forStack++
                    ")" -> {
                        if (--forStack == 0) endCalculate(false)
                    }
                    ";", "," -> {
                        endCalculate(true)
                        expressionHelper.setUp()
                    }
                    else -> expressionHelper.processExpression(token, str)
                }
            }
            return false
        } else { //BODY
            val result = basicProcessor.process(token, str)

            isEnd = (isBlock && result) || (!isBlock && str == ";") // (block & last '}') || (without block)
            return isEnd
        }
    }

    fun setUp(str: String, currentNode: Node) {
        isFor = (str == "for")
        this.currentNode = currentNode // CONDITIONAL
        isFirst = false
        isDeclaration = true
        forStack = 1
        variables.state = HEADER
        variables.name = str
        expressionHelper.setUp() //HEADER
        basicProcessor = BasicProcessor().apply { setNode(currentNode.links[0]) } //HEADER
        assignHelper = AssignHelper(basicProcessor, headerVariables, expressionHelper, currentNode.links[0])
    }

    private fun endCalculate(isContinue: Boolean) {
        expressionHelper.makePostFix()
        currentNode.links[0].links.add(expressionHelper.makeTree()) //HEADER->EXPRESSION TREE ROOT
        isFirst = true
        if (!isContinue) {
            variables.state = BODY
            basicProcessor.setNode(currentNode.links[1])
        }
        expressionHelper.setUp()
    }
}