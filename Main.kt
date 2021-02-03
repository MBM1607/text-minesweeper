package minesweeper

import kotlin.random.Random

fun main() {
    print("How many mines do you want on the field? ")
    val mineField = MineField(9, 9)
    mineField.placeMines(readLine()!!.toInt())
    println()
    println(mineField)

    while (!mineField.isWin()) {
        print("Set/delete mine marks (x and y coordinates): ")
        try {
            val (j, i) = readLine()!!.split(" ", limit = 2).map { it.toInt() }
            if (mineField.placeMark(i - 1, j - 1)) {
                println(mineField)
            } else {
                println("There is a number here!")
            }
        } catch (e: Exception) {
            println("Invalid indices")
        }
    }

    println("Congratulations! You found all the mines!")
}


class MineField(private val rows: Int, private val columns: Int) {
    private val field = Array(rows) { CharArray(columns) { '.' } }
    private val userMarks = mutableListOf<Pair<Int, Int>>()

    override fun toString(): String {
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
                    Pair(i - 1, j - 1) in userMarks -> '*'
                    field[i - 1][j - 1] == 'X' -> '.'
                    else -> field[i - 1][j - 1]
                }
            }
            str += "|\n"
        }
        str += "-|"
        for (i in 1..columns) str += "-"
        str += "|"

        return str
    }

    fun placeMines(numMines: Int) {
        var mines = 0 // Number of mines placed on the field
        while (numMines != mines) {
            val mineRow = Random.nextInt(rows)
            val mineCol = Random.nextInt(columns)
            mines += placeMine(mineRow, mineCol)
        }
    }

    // Place a mark at the index and return true if successful
    // and false if a number exists on the location
    fun placeMark(i: Int, j: Int): Boolean {
        when {
            field[i][j].isDigit() -> return false
            Pair(i, j) in userMarks -> userMarks.remove(Pair(i, j))
            else -> userMarks.add(Pair(i, j))
        }
        return true
    }

    private fun isMine(i: Int, j: Int) = field[i][j] == 'X'

    private fun placeMine(i: Int, j: Int): Int {
        if (!isMine(i, j)) {
            if (i != 0) {
                incrementCell(i - 1, j)
                if (j != 0) incrementCell(i - 1, j - 1)
                if (j != columns - 1) incrementCell(i - 1, j + 1)
            }
            if (i != rows - 1) {
                incrementCell(i + 1, j)
                if (j != 0) incrementCell(i + 1, j - 1)
                if (j != columns - 1) incrementCell(i + 1, j + 1)
            }
            if (j != 0) incrementCell(i, j - 1)
            if (j != columns - 1) incrementCell(i, j + 1)
            field[i][j] = 'X'
            return 1
        }
        return 0
    }

    // Increase the neighbour count of the cell
    private fun incrementCell(i: Int, j: Int) {
        if (field[i][j].isDigit()) field[i][j] = (field[i][j].toInt() + 1).toChar()
        else if (field[i][j] != 'X') field[i][j] = '1'
    }

    fun isWin(): Boolean {
        for ((i, row) in field.withIndex())
            for ((j, cell) in row.withIndex())
                if (cell == 'X' && Pair(i, j) !in userMarks)
                    return false
        for ((i, j) in userMarks)
            if (field[i][j] != 'X') return false
        return true
    }
}