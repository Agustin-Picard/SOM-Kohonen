package demo
fun readInput(): Int {
    while (true) {
        val choice: Int? = readLine()?.toIntOrNull()
        if (choice != null && choice <= 7 && choice >= 0)
            return choice
        else
            println("Invalid choice, please try again")
    }
}

fun main() {
    println("""
        Kohonen Self Organizing Map Demo
        --------------------------------

        0 For 2D map learning a Non-Uniform Distribution
        1 For 2D map learning a Uniform Distribution
        2 For 1D map learning a Non-Uniform Distribution
        3 For 1D map learning a Uniform Distribution
        4 For 2D map learning dataset 1
        5 For 1D map learning dataset 2
        6 For 2D map learning XOR
        7 For 2D map learning digits
    """.trimIndent())

    when(readInput()) {
        0 -> demo0()
        1 -> demo1()
        2 -> demo2()
        3 -> demo3()
        4 -> demo4()
        5 -> demo5()
        6 -> demo6()
        7 -> demo7()
    }
}