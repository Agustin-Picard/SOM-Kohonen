package demo

import kohonen.*
import tomasvolker.kyscript.KyScriptConfig
import tomasvolker.numeriko.core.dsl.I
import tomasvolker.numeriko.core.interfaces.factory.doubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import tomasvolker.numeriko.core.primitives.sqrt
import kotlin.random.Random

fun demo4() {
    KyScriptConfig.defaultPythonPath = "python"

    val nodeCount = 8
    val shape = I[sqrt(nodeCount).toInt(), sqrt(nodeCount).toInt()]
    val inputData = DataFactory.fromFile("data/set1.txt")
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

    som.plotMapDataset(inputData)
}