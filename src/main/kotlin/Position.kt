import kotlin.math.min

data class Position(var startLine: Int, var pos: MutableList<Line>) {

    fun startSolving(depth: Int): MutableSet<Position> = generateMoves(depth, depth)

    override fun toString(): String {
        var str = startLine.toString()
        pos.forEach {
            str += it.toString()
        }
        return str
    }

    fun display() {
        pos.forEach {
            it.display()
            println()
        }
    }

    fun hasWon(): Boolean {
        pos.forEachIndexed { index, line ->
            if (index != startLine) {
                if (line.isFull()) {
                    return true
                }
            }
        }
        return false
    }

    fun possibleMove(): MutableSet<Pair<Int, Int>> {
        val list = mutableSetOf<Pair<Int, Int>>()
        pos.forEachIndexed { index1, it1 ->
            if (!it1.isEmpty()) pos.forEachIndexed { index, it ->
                if ((it.topOne() > it1.topOne() || it.isEmpty()) && it != it1) {
                    list.add(Pair(index1, index))
                }
            }
        }
        return list
    }

    tailrec fun generateMoves(depth: Int, originalDepth: Int): MutableSet<Position> {
        if (depth == 1) {
            val listToCheck = generateMoves()
            listToCheck.forEach {
                if (it.hasWon()) {
                    solvedStep = min(originalDepth - depth + 1, solvedStep)
                    isSolved = true
                    return mutableSetOf(it)
                }
            }
            // if we weren't able to find a solution, what's the point of returning it?
            return mutableSetOf()
        }
        val list = mutableSetOf<Position>()
        generateMoves().forEach {
            if (it.hasWon()) {
                solvedStep = min(originalDepth - depth + 1, solvedStep)
                isSolved = true
                return mutableSetOf(it)
            }
            it.generateMoves(depth - 1, originalDepth).forEach { it1 ->
                list.add(it1)
            }
        }
        return if (list.isNotEmpty()) mutableSetOf(list.first())
        else list
    }

    fun generateMoves(): MutableSet<Position> {
        occuredPositions[this.toString()]?.let {
            return it
        }
        val generatedList = mutableSetOf<Position>()
        possibleMove().forEach {
            generatedList.add(applyMove(it))
        }
        occuredPositions.put(this.toString(), generatedList)
        return generatedList
    }

    fun applyMove(move: Pair<Int, Int>): Position {
        val lineToRemove = move.first
        val lineToAdd = move.second
        val elementToMove = this.pos[lineToRemove].topOne()
        val copy = pos.toMutableList()
        copy[lineToRemove] = copy[lineToRemove].removeTopElement()
        copy[lineToAdd] = copy[lineToAdd].addElement(elementToMove)
        return Position(startLine, copy)
    }
}