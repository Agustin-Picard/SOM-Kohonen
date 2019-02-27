package kohonen

import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.doubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import tomasvolker.numeriko.core.interfaces.factory.toDoubleArray1D
import tomasvolker.numeriko.core.primitives.squared
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class DataFactory(val random: Random = Random.Default) {
    companion object {
        fun uniformSquare(side: Double, center: DoubleArray1D, nSamples: Int) =
            List(nSamples) {
                doubleArray1D(center.size) { side * Random.nextDouble() } + center
            }

        fun gaussianCircle(radius: Double, center: DoubleArray1D, nSamples: Int) =
            List(nSamples) {
                doubleArray1D(center.size) { radius * Random.nextGaussian() } + center
            }

        fun nonUniformCircle2D(radius: Double, center: DoubleArray1D, nSamples: Int) =
            List(nSamples) {
                val r = Random.nextDouble() * radius
                val theta = Random.nextDouble() * 2 * PI
                D[r * cos(theta), r * sin(theta)] + center
            }

        fun uniformCircle2D(radius: Double, center: DoubleArray1D, nSamples: Int) =
            List(nSamples) {
                var x = Random.nextDouble()
                var y = Random.nextDouble()

                while (sqrt((x - center[0]).squared() + (y - center[1]).squared()) > radius) {
                    x = Random.nextDouble()
                    y = Random.nextDouble()
                }
                D[x, y]
            }

        fun fromFile(filename: String) =
            File(filename).useLines {
                it.filter { it.isNotEmpty() && it.isNotBlank() }
                    .drop(3)  // drop the header
                    .map {
                        it.trim()
                            .split(" ")
                            .map { it.toDouble() }
                            .dropLast(1)  // drop the class
                    }.toList()
            }.map { it.toDoubleArray1D() }

        fun labeledFromFile(filename: String) =
            File(filename).useLines {
                it.filter { it.isNotEmpty() && it.isNotBlank() }
                    .drop(3)  // drop the header
                    .map {
                        it.trim()
                            .split(" ")
                            .map { it.toDouble() }
                    }.toList()
            }.map { it.toDoubleArray1D() }
    }
}