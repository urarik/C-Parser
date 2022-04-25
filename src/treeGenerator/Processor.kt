package treeGenerator

import treeGenerator.Tokens.*

class Processor : BasicProcessor() { // function part
    val functions = ArrayList<Node>() //functions of a code

    override fun start(str: String) {
        process(getToken(str), str)
    }

    override fun process(token: Tokens, str: String): Boolean {

        //println("str = $str \t\t, state = ${currentState}")
        //println(currentState)
        //assignHelper의 currentNode 업데이트 주의 !. 생성할 때마다 설정
        val result = super.process(token, str)
        when(token) {
            DELIMITER -> {
                if(str == "(" && variables.state == StateProcess.EMERGING) {
                    variables.state = StateProcess.FUNCTION
                    functions.add(Node(State.FUNCTION, variables.name))
                    currentFunctionIndex = functions.size - 1
                    assignHelper.currentNode = functions[currentFunctionIndex].links[0] // HEADER
                    currentNode = functions[currentFunctionIndex].links[1] // BODY
                }
            }
            OPERATOR -> {
            }
            DATA_TYPE -> {
            }
            NAME -> {
            }
            CONDITIONAL -> {
            }
            KEYWORD -> {
            }
        }
        return result
    }
    fun updateCurrentFunctionIndex(currentFunctionIndex: Int) {

    }
}