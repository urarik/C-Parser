package treeProcessor

import treeGenerator.Node
import treeGenerator.State

class IOHelper(val expressionHelper: ExpressionHelper) {
    fun start(root: Node) {
        var string = root.links[0].value as String
        string = string.substring(1 until string.length - 1)
        //문제점 - 바로 %가 나오면?
        if (root.value == "printf") {
            val tokens = string.split("%")
            val output = StringBuilder(tokens[0])
            for (i in 1 until tokens.size) {
                var token = tokens[i]
                val argument = root.links[i]
                when (argument.state) {
                    State.CONSTANT -> {
                        val value = if (tokens[i][0] == 'c') (argument.value as String).toInt().toChar().toString()
                        else (argument.value as String).toInt().toString()
                        token = tokens[i].replaceRange(0..0, value)
                    }
                    State.VARIABLE -> {
                        val variable = PreOrder.find((argument.value as Pair<*, *>).first as String)
                        variable?.let {
                            it.third?.let {
                                token = tokens[i].replaceRange(0..0, if (tokens[i][0] == 'c') (it as String).toInt().toChar().toString() else it as String)
                            } ?: throw IllegalAccessException("uninitialized variable ${argument.value}")
                        }
                    }
                    State.OPERATOR -> {
                        val result = with(expressionHelper) {
                            setUp()
                            start(argument)
                            returnStack[0]
                        } as Int
                        token = tokens[i].replaceRange(0..0, if (tokens[i][0] == 'c') result.toChar().toString() else result.toString())
                                ?: throw IllegalAccessException("uninitialized variable ${argument.value}")
                    }
                }
                output.append(token)
            }
            println("printf = $output")
        } else if (root.value == "scanf") {

        }
    }
}