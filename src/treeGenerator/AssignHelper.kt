package treeGenerator

import treeGenerator.Tokens.*
import treeGenerator.StateProcess.*

class AssignHelper(val basicProcessor: BasicProcessor, val variables: Variables, val expressionHelper: ExpressionHelper, var currentNode: Node? = null) {
    var dataType: StringBuilder? = null
    var dynamicType: String? = null
    var isArray = false
    var isDynamic = false
    var isDynamicAssign = false
    var arrayInit: MutableList<Node>? = null
    val subExpressionHelper = ExpressionHelper()

    //ELSE --data type--> WAITING or WAITING_STRUCT --name--> EMERGING [--"="--> VARIABLE][--"("--> FUNCTION]
    fun processAssign(token: Tokens, str: String) { //processes variable/function declaration, variable assing

        if (isDynamic) {
            if(variables.state == ELSE) {
                isDynamic = false
                isDynamicAssign = true
            }
            if (str == ";") {
                addAssign(currentNode!!, variables.name, Pair(dataType.toString(), Node(State.DYNAMIC, dynamicType).apply {
                    expressionHelper.makePostFix()
                    if(expressionHelper.postfix.size != 0)
                        links.add(Node(State.CONSTANT, expressionHelper.makeTree())) }))
                variables.state = ELSE
                dynamicType = null
                isDynamic = false
                isArray = false
            }
            if(str == "]") isArray = false
            if(isArray) expressionHelper.processExpression(token, str)
            if(str == "[") isArray = true
            if(dynamicType == null) dynamicType = str

        }

        else {
            when (variables.state) {
                VARIABLE -> { //variable declaration
                    if (str == ";" || (str == "," && !isArray)) { //end of declaration
                        expressionHelper.makePostFix()
                        //adds ASSIGN TreeGenerator.Node which has two children called VARIABLE and CONSTANT
                        if (isArray) {
                            addAssign(currentNode!!, variables.name, Pair(dataType.toString(), expressionHelper.makeTree()), subExpressionHelper.makeTree(), arrayInit!!)
                            isArray = false
                        } else {
                            addAssign(currentNode!!, variables.name, Pair(dataType.toString(), expressionHelper.makeTree()))
                            isDynamicAssign = false
                        }
                        if (str == ";") { //end of declaration
                            dataType = null
                            variables.state = ELSE //resets the state
                        } else variables.state = WAITING // "," next variable with the same data type.
                    } else if (isArray)
                        when (str) {
                            "{" -> {
                                expressionHelper.setUp()
                                arrayInit = mutableListOf()
                            }
                            ",", "}" -> {
                                expressionHelper.makePostFix()
                                arrayInit?.add(expressionHelper.makeTree())
                                expressionHelper.setUp()
                            }
                            else -> {
                                expressionHelper.processExpression(token, str)
                            }
                        }
                    else if (variables.state == VARIABLE && str == "new") {
                        expressionHelper.setUp()
                        isDynamic = true
                    } else {
                        expressionHelper.processExpression(token, str) //build assign expression
                    }
                    return
                }
                ASSIGN -> { //assigns a value to a existing variable.
                    if (str == ";") {
                        basicProcessor.expressionHelper.makePostFix()
                        //adds a ASSIGN node with null data type.
                        addAssign(currentNode!!, variables.name, Pair(if (isDynamicAssign) "dynamic" else null, basicProcessor.expressionHelper.makeTree()))
                        variables.state = ELSE //reset
                    } else {
                        basicProcessor.expressionHelper.processExpression(token, str)
                    }
                }
                EMERGING -> {
                    if (isArray) {
                        if (str == "]") {
                            subExpressionHelper.makePostFix()
                        } else {
                            subExpressionHelper.processExpression(token, str)
                        }
                    } else if (str == "[") {
                        subExpressionHelper.setUp()
                        isArray = true
                    }
                }
            }

            when (token) {
                DELIMITER -> {
                    if ((str == "," || str == ")" || str == ";") && variables.state == EMERGING) {
                        if (isArray) {
                            addAssign(currentNode!!, variables.name, Pair(dataType.toString(), null), subExpressionHelper.makeTree())
                            isArray = false
                        } else addAssign(currentNode!!, variables.name, Pair(dataType.toString(), null))
                        variables.state = if (str == ")" || str == ";") {
                            dataType = null
                            currentNode = basicProcessor.currentNode // convert currentNode into BODY for function
                            ELSE
                        } else CONTINUE
                    } else if (str == ")" && variables.state == FUNCTION || variables.state == WAITING) { //end of function header
                        dataType = null
                        currentNode = basicProcessor.currentNode // convert currentNode into BODY for function
                        variables.state = ELSE // no parameter
                    }

                }
                OPERATOR -> {
                    if (str == "=" && variables.state == ELSE) { //assigns a value to a existing variable
                        variables.state = ASSIGN
                        basicProcessor.expressionHelper.setUp() //setup(reset) expression helper
                    }
                    if (str == "=" && variables.state == EMERGING) { //declares a variable
                        variables.state = VARIABLE
                        expressionHelper.setUp() //setup(reset) expression helper
                    }
                    if (str == "*" && variables.state == WAITING) dataType?.append(" $str") //a pointer variable
                    if (str == "*" && variables.state == ELSE) isDynamic = true
                }
                DATA_TYPE -> { //need to consider type conversion
                    variables.state = if (str == "struct") WAITING_STRUCT
                    else WAITING //waiting variable/function name
                    dataType = java.lang.StringBuilder("$str ") // for struct/pointer (struct node *)
                }
                NAME -> {
                    when (variables.state) {
                        WAITING -> {
                            variables.state = EMERGING // emerges name of func/var
                            variables.name = str
                        }
                        WAITING_STRUCT -> { // struct name
                            variables.state = WAITING
                            dataType?.append(str)
                        }
                        VARIABLE -> {/*
                        if(currentNode?.state == TreeGenerator.State.FUNCTION) { //function parameter
                            println("!@#")
                            addAssign(currentNode!!.links[0], variables.name, Pair(dataType.toString(), str))
                            dataType = null
                            variables.state = ELSE
                        }*/
                        }
                        CONTINUE -> { // function parameter after ',' ex) B, C in func(A, B, C)
                            variables.name = str
                            variables.state = EMERGING
                        }
                        ELSE -> { //memorizes
                            variables.name = str
                        }
                    }
                }
                else -> return
            }
        }
    }

    private fun addAssign(parent: Node, name: String, pair: Pair<String?, Any?>, arrayIndex: Node? = null, arrayInit: List<Node>? = null) { //ASSIGN(VARIABLE, CONSTANT(or expression))
        parent.links.add(Node(State.ASSIGN, arrayIndex).apply {
            links.add(Node(State.VARIABLE, name))
            if (arrayIndex == null) links.add(Node(State.CONSTANT, pair))
            else links.add(Node(State.CONSTANT, Pair(pair.first, arrayInit)))
        })
    }
}