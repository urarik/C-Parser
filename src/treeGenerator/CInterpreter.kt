package treeGenerator

class CInterpreter(val tokens: List<String>) {
    lateinit var line: String
    val processor = Processor()

    fun start() {
        for(token in tokens) {
            parse(token)
        }
    }

    fun parse(untrimmedLine: String) {
        var prevIndex = 0
        var i = 0
        line = untrimmedLine.trim()
        while(i < line.length) {
            if(isDelimiter(line[i].toString())) {
                processSubString(prevIndex, i)
                prevIndex = if(line[i] == '"') { //string
                    while(line[++i] != '"' || line[i-1] == '\\') ;
                    i++ //include '"'
                    processSubString(prevIndex, i)
                    i
                }
                else if ((i + 1 < line.length && line[i] == '-' && line[i+1] == '>') // ->
                        || (i + 1 < line.length && isLogical(line[i]) && line[i+1] == '=') // ==
                        || (i + 1 < line.length && line[i] == '+' && line[i+1] == '+') //++
                        || (i + 1 < line.length && line[i] == '-' && line[i+1] == '-')) { // --
                    processor.start(line.substring(i..i+1))
                    i++ + 2
                }
                else {
                    if(line[i] != ' ') processor.start(line[i].toString())
                    i + 1
                }
            }
            if(i + 1 < line.length && getToken(line.substring(i..i + 1)) == Tokens.OPERATOR){ //LOGICAL_OPERATOR
                processSubString(prevIndex, i)
                processor.start(line.substring(i..i+1))
                prevIndex = i + 2
                i++
            }
            i++
        }
    }

    fun isDelimiter(str: String): Boolean {
        return when(getToken(str)) {
            Tokens.DELIMITER, Tokens.OPERATOR -> true
            else -> false
        }
    }

    fun processSubString(prevIndex: Int, i: Int) {
        if(prevIndex < i) processor.start(line.substring(prevIndex until i))
    }
}