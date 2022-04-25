package treeGenerator

import treeGenerator.Tokens.*
import treeGenerator.StateProcess.*

enum class State {
    FUNCTION, FUNCTION_CALL, HEADER, BODY, CONDITIONAL, DYNAMIC, CASE, DEFAULT, VARIABLE, ASSIGN, LOOP, RETURN, OPERATOR, PRE_OPERATOR, POST_OPERATOR, CONSTANT, IO, BREAK, CONTINUE, NULL
}

enum class StateProcess {
    ELSE, WAITING, WAITING_STRUCT, EMERGING, CONTINUE, CONDITIONAL, LOOP, FUNCTION, VARIABLE, IO, RETURN, ASSIGN, BODY, HEADER, FUNCTION_CALL
}

open class BasicProcessor(var currentNode: Node? = null, val variables: Variables = Variables()) { //exclude function part

    var blockStack: Int = 0 //the number of '{'
    var currentFunctionIndex = -1
    var isIncOrDec = false
    var currentState: StateProcess = ELSE // a wide state
    val expressionHelper = ExpressionHelper() //used for assign, loop, conditional . . .
    val assignHelper = AssignHelper(this, variables, expressionHelper) // always invoked to change narrow state(variables.state)
    private val conditionalHelper = ConditionalHelper(variables, expressionHelper) // invoked if wide state is CONDITIONAL
    private val loopHelper = LoopHelper(variables, expressionHelper) // invoked if wide state is LOOP
    private val iOHelper = IOHelper(variables, expressionHelper)

    open fun start(str: String) {  //TreeGenerator.CInterpreter -> start -> process -> . . .
        process(getToken(str), str)
    }


    open fun process(token: Tokens, str: String): Boolean { // returns true if a block ends.
        when (currentState) { //a wide state
            StateProcess.CONDITIONAL ->
                if (conditionalHelper.processConditional(token, str)) {
                    variables.state = ELSE
                    currentState = ELSE //changes a wide state if a conditional statement is ended; when last '}' is processed.
                }
            StateProcess.LOOP ->
                if (loopHelper.processLoop(token, str)) {
                    variables.state = ELSE
                    currentState = ELSE //changes a wide state if a loop statement is ended; when last '}' is processed.
                }
            StateProcess.IO ->
                if (iOHelper.processIO(token, str)) {
                    variables.state = ELSE
                    currentState = ELSE
                }
            FUNCTION_CALL -> {
                if (str == ";") {
                    expressionHelper.makePostFix()
                    currentNode!!.links.add(expressionHelper.makeTree())
                    currentState = ELSE
                } else expressionHelper.processExpression(token, str)
            }
            RETURN -> {
                if (str == ";") {
                    expressionHelper.makePostFix()
                    currentNode!!.links.add(Node(State.RETURN, null).apply {
                        links.add(expressionHelper.makeTree())
                    })
                    currentState = ELSE
                } else expressionHelper.processExpression(token, str)
            }

            else -> {
                assignHelper.processAssign(token, str) //detects function/variable declaration and assign value.

                if (variables.state == ELSE) {
                    if (str == ";") {
                        if(expressionHelper.expression.contains(Pair("++", null))
                                || expressionHelper.expression.contains(Pair("--", null))) {
                            expressionHelper.makePostFix()
                            currentNode!!.links.add(expressionHelper.makeTree())
                        }
                        expressionHelper.setUp()
                    } else expressionHelper.processExpression(token, str)

                }
                when (token) {
                    DELIMITER -> {
                        if (str == "{") blockStack++
                        else if (str == "}" && --blockStack == 0) {
                            return true //end of a block
                        }
                        if (str == "(" && (variables.state == ELSE || variables.state == BODY)) {
                            expressionHelper.setUp()
                            with(expressionHelper) {
                                processExpression(NAME, variables.name)
                                processExpression(DELIMITER, "(")
                            }
                            currentState = FUNCTION_CALL
                        }
                    }
                    INPUT_OUTPUT -> {
                        currentState = IO
                        currentNode!!.links.add(Node(State.IO, str))
                        iOHelper.setUp(currentNode!!.links.last())

                    }
                    OPERATOR -> {
                    }
                    Tokens.CONDITIONAL -> { // str == "if" || "switch"
                        // change wide state to CONDITIONAL so that it invokes processConditional instead of it.
                        currentState = StateProcess.CONDITIONAL
                        //add a new node representing the beginning of conditional statement.
                        currentNode!!.links.add(Node(State.CONDITIONAL, str))
                        conditionalHelper.setUp(str, currentNode!!.links.last()) //CONDITIONAL node
                    }
                    Tokens.LOOP -> { //same as a case of CONDITIONAL
                        currentState = StateProcess.LOOP
                        currentNode!!.links.add(Node(State.LOOP, str))
                        loopHelper.setUp(str, currentNode!!.links.last())
                    }
                    KEYWORD -> {
                        when (str) {
                            "return" -> {
                                currentState = RETURN //output of entire function process
                                expressionHelper.expressionStack = 0
                                expressionHelper.setUp()
                            }
                            "break" -> {
                                currentNode!!.links.add(Node(State.BREAK, null))
                            }
                            "continue" -> {
                                currentNode!!.links.add(Node(State.CONTINUE, null))
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    fun setNode(node: Node) { //sets currentNode to assign correctly
        currentNode = node
        assignHelper.currentNode = node
    }
}