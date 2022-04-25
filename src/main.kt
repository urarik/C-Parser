import treeGenerator.CInterpreter
import treeGenerator.Node
import treeGenerator.TestString
import treeProcessor.PreOrder

fun main() {
    val cInterpreter = CInterpreter(TestString.str2.split("\n"))
    cInterpreter.start()
    //setUp("switch.txt")
    println(TestString.str2)
    for(function in cInterpreter.processor.functions) {
        preOrderFunc(function)
        println()
    }
    //val preOrder = PreOrder(cInterpreter.processor.functions)
    //preOrder.start()
}

fun preOrderFunc(root: Node) {
    println(root)
    //sendForTest(root)
    for(link in root.links) {
        preOrderFunc(link)
    }
}

class Solution {
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val answer = mutableListOf<Int>()
        for(i in temperatures.indices) {
            var counter = 0;
            for(j in i+1 until temperatures.size) {
                counter++;
                if(temperatures[i] < temperatures[j]) {
                    answer.add(counter);
                    break;
                }
            }
            if(answer.size == i) answer.add(0);
        }

        return answer.toIntArray();
    }
}
