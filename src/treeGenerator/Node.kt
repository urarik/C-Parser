package treeGenerator

class Node(val state: State, var value: Any?, val links: ArrayList<Node> = ArrayList<Node>(3)) {
    init {
        if(state == State.FUNCTION || state == State.CONDITIONAL || state == State.LOOP) {
            links.add(Node(State.HEADER, null))
            links.add(Node(State.BODY, null))
        }
        if(state == State.FUNCTION || state == State.BODY) {
            val variables = arrayListOf<Pair<String, Pair<String, Any>>>()
        }
        //println(toString())
    }

    override fun toString(): String {
        return "($state, $value, link = ${links.size})"
    }
}