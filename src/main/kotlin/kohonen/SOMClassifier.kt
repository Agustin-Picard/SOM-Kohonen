package kohonen

import tomasvolker.kyscript.KyScriptConfig
import tomasvolker.numeriko.core.dsl.I
import tomasvolker.numeriko.core.functions.norm2
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array1d.integer.IntArray1D
import tomasvolker.numeriko.core.interfaces.factory.doubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import tomasvolker.numeriko.core.primitives.sqrt
import kotlin.random.Random


interface Classifier<T,R> {
    fun fit(trainData: List<T>)
    fun classify(testData: T): R
}

class SOMClassifier(override var mesh: List<Node<DoubleArray1D>>,
                    override val shape: IntArray1D,
                    val kNeighbors: Int = 3):
    SelfOrganizingMap(mesh, shape), Classifier<DoubleArray1D,Int> {

    private val classMap = HashMap<DoubleArray1D, Int>()

    override fun fit(trainData: List<DoubleArray1D>) {
        optimize(trainData)
        classMap.putAll(List(trainData.size) { i -> trainData[i] to trainData[i].last().toInt() })
    }

    override fun classify(testData: DoubleArray1D): Int =
        closestNodes(testData, kNeighbors)
            .map { node -> classMap[classMap.keys.maxBy { (it - node.value).norm2() }] ?: error("empty map") }
            .average()
            .toInt()

    private fun closestNodes(input: DoubleArray1D, nNodes: Int) =
        mesh.sortedBy { (it.value - input).norm2() }.take(nNodes)
}


fun main() {
    KyScriptConfig.defaultPythonPath = "python"

    val nodeCount = 250
    val shape = I[sqrt(nodeCount).toInt(), sqrt(nodeCount).toInt()]
    val inputData = DataFactory.labeledFromFile("data/Kohonen/digit10_16x16_learn.txt")
    val initMesh = List(nodeCount) { i ->
        Node(i, doubleArray1D(inputData[0].size) { Random.nextGaussian() })
    }
    val somClassifier = SOMClassifier(initMesh, shape)
    somClassifier.fit(inputData)

    val testData = DataFactory.labeledFromFile("data/Kohonen/digit10_16x16_test.txt")
    val resultList = mutableListOf<Boolean>()

    repeat(testData.size) { i ->
        resultList.add(somClassifier.classify(testData[i]) == testData[i].last().toInt())
    }

    println("model's accuracy: ${resultList.count { it } * 100.0 / testData.size}")
}