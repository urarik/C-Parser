package treeGenerator

import java.io.BufferedReader
import java.io.FileReader

object testCorrectness {

    lateinit var fileReader: BufferedReader
    lateinit var inputText: MutableList<String>

    fun sendForTest(node: Node) {
        inputText.add(node.toString())
    }
    fun test() :Boolean{
        return inputText == fileReader.readLines()
    }

    fun setUp(filename: String) {
        fileReader = BufferedReader(FileReader(filename))
        inputText = mutableListOf()
    }
}