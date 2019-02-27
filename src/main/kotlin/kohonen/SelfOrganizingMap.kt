package kohonen

import com.github.tomasvolker.parallel.mapParallel
import tomasvolker.kyplot.dsl.showLine
import tomasvolker.kyplot.dsl.showScatter
import tomasvolker.kyscript.KyScriptConfig
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.dsl.I
import tomasvolker.numeriko.core.index.All
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array1d.double.elementWise
import tomasvolker.numeriko.core.interfaces.array1d.integer.IntArray1D
import tomasvolker.numeriko.core.interfaces.array1d.integer.elementWise
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.doubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import tomasvolker.numeriko.core.interfaces.factory.toDoubleArray1D
import tomasvolker.numeriko.core.operations.stack
import tomasvolker.numeriko.core.primitives.sqrt
import tomasvolker.numeriko.core.primitives.squared
import java.io.File
import kotlin.math.*
import kotlin.random.Random


typealias NeighborhoodFunction = (IntArray1D, Double) -> Double

private fun DoubleArray1D.normSquared() = elementWise { it.squared() }.sum()

fun gaussianNeighborhood(diff: IntArray1D,
                                 neighborhoodSize: Double) =
    exp(diff.elementWise { -it.squared() }.sum() / (2.0 * neighborhoodSize.squared()))

fun DoubleArray2D.unstack(axis: Int = 0): List<DoubleArray1D> =
    if (axis == 0)
        List(shape0) { i -> this[i, All] }
    else
        List(shape1) { i -> this[All, i] }

private operator fun DoubleArray2D.minus(other: DoubleArray1D) =
    List(shape0) { i -> this[i, All] - other }.stack()

private operator fun DoubleArray1D.minus(other: DoubleArray2D) =
    List(other.shape0) { i -> this - other[i, All] }.stack()

data class Node<T>(val nodePosition: Int, val value: T)

interface Topology<T> {
    var mesh: List<Node<T>>
    val shape: IntArray1D
    fun findFittestNode(input: T): Node<T>
    fun cost(currNode: Node<T>,
             other: Node<T>,
             neighborhoodFunction: NeighborhoodFunction,
             neighborhoodSize: Double): Double
}

open class SelfOrganizingMap(override var mesh: List<Node<DoubleArray1D>>,
                             override val shape: IntArray1D
): Topology<DoubleArray1D> {

    val nodeCount get() = mesh.size

    override fun findFittestNode(input: DoubleArray1D): Node<DoubleArray1D> =
        List(nodeCount) { i -> mesh[i] to (mesh[i].value - input).normSquared() }
            .asSequence()
            .minBy { it.second }?.first ?: error("Empty mesh")

    override fun cost(currNode: Node<DoubleArray1D>,
                      other: Node<DoubleArray1D>,
                      neighborhoodFunction: NeighborhoodFunction,
                      neighborhoodSize: Double): Double =
        neighborhoodFunction(nodeToGrid(currNode) - nodeToGrid(other), neighborhoodSize)

    fun optimize(input: List<DoubleArray1D>,
                 neighborhoodFunction: NeighborhoodFunction = ::gaussianNeighborhood,
                 learningRate: Double = 0.1,
                 neighborhoodInit: Double = 0.3 * nodeCount,
                 nIterations: Int = 1000) {

        var neighborhoodSize = neighborhoodInit

        repeat(nIterations) {
            input.shuffled().forEach { inp ->
                val fittestNeuron = findFittestNode(inp)
                mesh = mesh.map { node ->
                    Node(node.nodePosition, node.value +
                            (inp - node.value)
                                .elementWise { learningRate *
                                        cost(node, fittestNeuron, neighborhoodFunction, neighborhoodSize) * it })
                }
            }
            neighborhoodSize -= (0.3 * nodeCount - 0.001 * nodeCount) / nIterations
        }
    }

    private fun nodeToGrid(node: Node<DoubleArray1D>) =
        with(node) { I[(nodePosition / shape[0]), nodePosition % shape[0]] }

    fun plotMap() = showScatter {
        x = List(nodeCount) { i -> mesh[i].value[0] }
        y = List(nodeCount) { i -> mesh[i].value[1] }
    }

    fun plotPath() = showLine {
        x = List(nodeCount) { i -> mesh[i].value[0] }
        y = List(nodeCount) { i -> mesh[i].value[1] }
    }
}


fun main() {
    KyScriptConfig.defaultPythonPath = "python"

    val nodeCount = 250
    val shape = I[sqrt(nodeCount).toInt(), sqrt(nodeCount).toInt()]
//    val shape = I[nodeCount, 0]
//    val inputData = DataFactory.fromFile("data/Kohonen/set_xor.txt")
    val inputData = DataFactory.fromFile("data/Kohonen/digit10_16x16_learn.txt")//.also { plotDataset(it,5,16,16) }
//    val inputData = DataFactory.nonUniformCircle2D(0.5, D[0.5,0.5], 1000)
    val initMesh = List(nodeCount) { i ->
        Node(i, doubleArray1D(inputData[0].size) { Random.nextGaussian() })
    }

    val som = SelfOrganizingMap(initMesh, shape)

    som.optimize(
        input = inputData,
        neighborhoodFunction = ::gaussianNeighborhood,
        learningRate = 0.1,
        neighborhoodInit = 0.3 * nodeCount,
        nIterations = 1000
    )

    som.plotLearntImageSet(inputData, 5, 16, 16)
}