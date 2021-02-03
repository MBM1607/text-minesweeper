package minesweeper

import kotlin.random.Random
import kotlin.system.exitProcess

fun main() {
    print("How many mines do you want on the field? ")
    val numMines = readLine()!!.toInt()
    val mineField = MineField(9, 9)
    mineField.placeMines(numMines)
    mineField.printField()
    println()

    while (!mineField.isWin()) {
        print("Set/unset mines marks or claim a cell as free: ")
        try {
            val (jStr, iStr, command) = readLine()!!.split(" ", limit = 3)
            val i = iStr.toInt() - 1
            val j = jStr.toInt() - 1
            if (command == "free") {
                mineField.free(i , j)
            } else if (command == "mine") {
                if (mineField.placeMark(i, j)) {
                    mineField.printField()
                } else {
                    println("This cell is already explored")
                }
            } else {
                println("Invalid command")
            }
        } catch (e: Exception) {
            println("Invalid indices or command")
        }
    }

    println("Congratulations! You found all the mines!")
}


class MineField(private val rows: Int, private val columns: Int) {
    private val field = Array(rows) { CharArray(columns) { '.' } }
    private val userMarks = mutableListOf<Pair<Int, Int>>()
    private val mines = mutableListOf<Pair<Int, Int>>()

    fun printField(printMines: Boolean = false) {
        var str = " |"
        for (i in 1..columns) str += i.toString()
        str += "|\n"
        str += "-|"
        for (i in 1..columns) str += "-"
        str += "|\n"
        for (i in 1..rows) {
            str += "${i}|"
            for (j in 1..columns) {
                str += when {
                    printMines && Pair(i - 1, j - 1) in mines -> 'X'
                    Pair(i - 1, j - 1) in userMarks -> '*'
                    else -> field[i - 1][j - 1]
                }
            }
            str += "|\n"
        }
        str += "-|"
        for (i in 1..columns) str += "-"
        str += "|"

        println(str)
    }

    fun placeMines(numMines: Int) {
        var placedMines = 0
        while (numMines != placedMines) {
            val i = Random.nextInt(rows)
            val j = Random.nextInt(columns)
            if (Pair(i, j) !in mines) {
                mines.add(Pair(i, j))
                placedMines++
            }
        }
    }

    // Place a mark at the index and return true if successful
    // and false if that cell is explored
    fun placeMark(i: Int, j: Int): Boolean {
        when {
            field[i][j] != '.' -> return false
            Pair(i, j) in userMarks -> userMarks.remove(Pair(i, j))
            else -> userMarks.add(Pair(i, j))
        }
        return true
    }

    fun free(i: Int, j: Int) {
        val indices = Pair(i, j)
        when {
            indices in mines -> {
                if (allUnexplored()) {
                    mines.remove(indices)
                    placeMines(1)
                    free(i, j)
                    return
                }
                fail()
            }
            field[i][j] == '.' -> freeCell(i, j)
            else -> {
                println("Cell is already free")
                return
            }
        }
        printField()
    }

    private fun fail() {
        printField(true)
        println("You stepped on a mine and failed!")
        exitProcess(0)
    }

    private fun freeCell(i: Int, j: Int) {
        val indices = Pair(i, j)
        if (indices in userMarks) userMarks.remove(indices)
        val mines = countNeighbourMine(i, j)
        if (mines > 0) {
            field[i][j] = mines.toString().first()
        }
        else if (mines == 0 && field[i][j] == '.') {
            field[i][j] = '/'
            if (i != 0) {
                freeCell(i - 1, j)
                if (j != 0) freeCell(i - 1, j - 1)
                if (j != columns - 1) freeCell(i - 1, j + 1)
            }
            if (i != rows - 1) {
                freeCell(i + 1, j)
                if (j != 0) freeCell(i + 1, j - 1)
                if (j != columns - 1) freeCell(i + 1, j + 1)
            }
            if (j != 0) freeCell(i, j - 1)
            if (j != columns - 1) freeCell(i, j + 1)
        }
    }

    private fun countNeighbourMine(i: Int, j: Int): Int {
        var result = 0
        if (i != 0) {
            if (Pair(i - 1, j) in mines) result++
            if (j != 0 && Pair(i - 1, j - 1) in mines) result++
            if (j != columns - 1 && Pair(i - 1, j + 1) in mines) result++
        }
        if (i != rows - 1) {
            if (Pair(i + 1, j) in mines) result++
            if (j != 0 && Pair(i + 1, j - 1) in mines) result++
            if (j != columns - 1 && Pair(i + 1, j + 1) in mines) result++
        }
        if (j != 0 && Pair(i, j - 1) in mines) result++
        if (j != columns - 1 && Pair(i, j + 1) in mines) result++
        return result
    }

    // Check if all field other then the mines is explored
    private fun allExplored(): Boolean {
        for ((i, row) in field.withIndex())
            for((j, cell) in row.withIndex())
                if (cell == '.' && Pair(i, j) !in mines)
                    return false
        return true
    }

    // Check if the field is untouched
    private fun allUnexplored() = field.all { row -> row.all { it == '.'}}

    private fun allMinesMarked(): Boolean {
        for ((i, row) in field.withIndex())
            for (j in row.indices) {
                val indices = Pair(i, j)
                if (indices in mines && indices !in userMarks ||
                    indices !in mines && indices in userMarks)
                    return false
            }
        return true
    }

    fun isWin(): Boolean {
        return allMinesMarked() || allExplored()
    }
}