package treeGenerator

import treeGenerator.StateProcess.*

class ConditionalHelper(val variables: Variables, val expressionHelper: ExpressionHelper) {
    companion object {
        const val WAITING_VALUE = 1 // in code 'case 'A':', waiting 'A'
        const val NOT_WAITING = 2
        const val WAITING_COLON = 3
        const val WAITING_IF = 4
    }

    private lateinit var currentNode: Node
    private lateinit var root: Node
    var isIf = false
    var isFirst = false
    lateinit var basicProcessor: BasicProcessor
    lateinit var value: java.lang.StringBuilder
    var isBlock = false
    var isEnd = false
    var isWaitingValue = 0

    fun processConditional(token: Tokens, str: String): Boolean {
        //println(currentNode)

        if (isWaitingValue == WAITING_IF) { //else까지 들어온 상태
            if (str == "if") { //else if
                currentNode.value = currentNode.value as String + " if"
                isWaitingValue = NOT_WAITING
                return false
            } else {
                variables.state = BODY //else, HEADER 생략
                isFirst = true
                expressionHelper.setUp()
                basicProcessor.setNode(currentNode.links[1]) // BODY
            }
            isWaitingValue = NOT_WAITING
        }

        if (isFirst) {
            if (str == "{") {
                isBlock = true
                isFirst = false
            } else if (str == ";") isFirst = false
        }

        if (variables.state == HEADER) {
            if (expressionHelper.processBracketExpression(token, str)) {
                currentNode.links[0].links.add(expressionHelper.nodeStack[0]) //HEADER
                variables.state = BODY
                isFirst = true
                expressionHelper.setUp()
                basicProcessor.setNode(currentNode.links[1]) // BODY
                return false
            }
        } else { //BODY
            var result = basicProcessor.process(token, str) // end of block with last '}' or "break"
            if (result) {
                if (str == "}") return true  // end of Switch
            }

            if (isWaitingValue == WAITING_VALUE) {
                value = java.lang.StringBuilder()
                isWaitingValue = WAITING_COLON
            }

            if (str == "case") {
                if (currentNode.state != State.BODY) { // end of case(2): another 'case'
                    result = false
                }
                isWaitingValue = WAITING_VALUE
                if (isIf) throw IllegalAccessException("Can't put 'case' in If block")
            } else if (str == "default") {
                basicProcessor.setNode(root)
                basicProcessor.currentNode!!.links.add(Node(State.DEFAULT, null))
                basicProcessor.setNode(basicProcessor.currentNode!!.links.last())
            }

            if (isWaitingValue == WAITING_COLON) {
                if (str == ":") {
                    basicProcessor.currentNode = root
                    basicProcessor.currentNode!!.links.add(Node(State.CASE, value.toString()))
                    basicProcessor.setNode(basicProcessor.currentNode!!.links.last())
                    isWaitingValue = NOT_WAITING
                } else if(str != "'") value.append(str)
            }

            isEnd = (isBlock && result) || (!isBlock && str == ";") // (Switch / If with block) || (If without block)
            return isEnd
        }
        return false
    }

    fun setUp(str: String, currentNode: Node) {
        isIf = (str == "if")
        this.currentNode = currentNode // CONDITIONAL
        root = currentNode.links[1]
        isBlock = false
        isFirst = false
        if (str == "else")
            isWaitingValue = WAITING_IF
        variables.state = HEADER
        variables.name = str
        expressionHelper.setUp()
        basicProcessor = BasicProcessor(currentNode.links[1]) //BODY
    }
}