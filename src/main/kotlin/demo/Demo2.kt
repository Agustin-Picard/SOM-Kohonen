package demo

import kohonen.*
import tomasvolker.kyscript.KyScriptConfig
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.dsl.I
import tomasvolker.numeriko.core.interfaces.factory.doubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import kotlin.random.Random

fun demo2() {
    KyScriptConfig.defaultPythonPath = "python"

    val nodeCount = 50
    val shape = I[nodeCount, 0]
    val inputData = DataFactory.nonUniformCircle2D(0.5, D[0.5,0.5], 1000)
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

    som.plotPathCircle(0.5, D[0.5, 0.5], inputData)
}

