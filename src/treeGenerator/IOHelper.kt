package treeGenerator

import treeGenerator.StateProcess.*

class IOHelper(val variables: Variables, val expressionHelper: ExpressionHelper) {
    private lateinit var currentNode: Node
    var isFirst = false

    fun processIO(token: Tokens, str: String): Boolean {
        if(str ==";" && !isFirst) { //a last element
            if(variables.state == BODY) with(expressionHelper) {
                expression.removeLast() // remove printf("ABC", a, b ')' <--
                makePostFix()
                currentNode.links.add(makeTree())
            }
            return true
        }

        if (variables.state == BODY) {
            if (str == ",") {
                expressionHelper.makePostFix()
                currentNode.links.add(expressionHelper.makeTree())
                expressionHelper.setUp()
            } else expressionHelper.processExpression(token, str)
        } else {
            if (str == ",") {
                variables.state = BODY
                expressionHelper.setUp()
            } else if(str != "(" && str != ")") {
                currentNode.links.add(Node(State.HEADER, str))
                isFirst = false
            }
        }
        return false
    }

    fun setUp(currentNode: Node) {
        this.currentNode = currentNode
        variables.state = HEADER
        isFirst = true
    }
}