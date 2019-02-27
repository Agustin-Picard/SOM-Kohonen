package kohonen

import tomasvolker.kyplot.dsl.*
import tomasvolker.kyplot.model.Color
import tomasvolker.kyplot.model.LineType
import tomasvolker.kyplot.model.MarkerType
import tomasvolker.numeriko.core.functions.transpose
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array1d.double.elementWise
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.linearalgebra.linearSpace
import tomasvolker.numeriko.core.operations.concatenate
import tomasvolker.numeriko.core.operations.stack
import tomasvolker.numeriko.core.primitives.sqrt
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


fun DoubleArray2D.stack(other: DoubleArray2D, axis: Int = 0): DoubleArray2D {
    return if (axis == 0) {
        other.unstack().let {
            this.unstack()
                .mapIndexed { index, array1D -> array1D.concatenate(it[index]) }
                .stack()
        }
    } else {
        this.transpose()
            .unstack()
            .let {
                other.transpose()
                    .unstack()
                    .mapIndexed { index, array1D -> array1D.concatenate(it[index]) }
                    .stack()
                    .transpose()
            }
    }
}

fun plotDataset(inputData: List<DoubleArray1D>, nClasses: Int, shape0: Int, shape1: Int) = showImage {
    data = inputData.map { it.withShape(shape0, shape1) }
        .chunked(nClasses)
        .map { it.reduce { acc, digit -> acc.stack(digit, axis = 0) } }
        .reduce { acc, digitClass -> acc.stack(digitClass, axis = 1) }
        .unstack()
}

fun SelfOrganizingMap.plotLearntImageSet(inputData: List<DoubleArray1D>,
                                         nClasses: Int,
                                         shape0: Int,
                                         shape1: Int)
        = showFigure {
    allPlots {
        position {
            columnCount = 2
            rowCount = 1
        }
    }

    plot {
        image {
            data = inputData.map { it.withShape(shape0, shape1) }
                .chunked(nClasses)
                .map { it.reduce { acc, digit -> acc.stack(digit, axis = 0) } }
                .reduce { acc, digitClass -> acc.stack(digitClass, axis = 1) }
                .unstack()
        }

        position {
            column = 0
            row = 0
        }
    }

    plot {
        image {
            data = mesh.map { it.value }.map { it.withShape(shape0, shape1) }
                .chunked(sqrt(nodeCount).toInt())
                .map { it.reduce { acc, digit -> acc.stack(digit, axis = 0) } }
                .reduce { acc, block -> acc.stack(block, axis = 1) }
                .unstack()
        }

        position {
            column = 1
            row = 0
        }
    }
}

fun SelfOrganizingMap.plotMapCircle(radius: Double,
                                    center: DoubleArray1D,
                                    inputData: List<DoubleArray1D>)
        = showFigure {
    allPlots {
        position {
            columnCount = 1
            rowCount = 1
        }
    }

    plot {
        line {
            x = linearSpace(0.0, 2 * PI, 1000).elementWise { radius * cos(it) + center[0] }
            y = linearSpace(0.0, 2 * PI, 1000).elementWise { radius * sin(it) + center[1] }
            lineStyle.color = Color.BLACK
        }
    }

    plot {
        scatter {
            x = List(inputData.size) { i -> inputData[i][0] }
            y = List(inputData.size) { i -> inputData[i][1] }

            markerStyle {
                color = Color.BLUE
                type = MarkerType.POINT
                alpha = 0.4
            }
        }
    }

    plot {
        scatter {
            x = List(nodeCount) { i -> mesh[i].value[0] }
            y = List(nodeCount) { i -> mesh[i].value[1] }

            markerStyle {
                color = Color.RED
                type = MarkerType.X
            }
        }
    }
}

fun SelfOrganizingMap.plotPathCircle(radius: Double,
                                     center: DoubleArray1D,
                                     inputData: List<DoubleArray1D>)
        = showFigure {
    allPlots {
        position {
            columnCount = 1
            rowCount = 1
        }
    }

    plot {
        line {
            x = linearSpace(0.0, 2 * PI, 1000).elementWise { radius * cos(it) + center[0] }
            y = linearSpace(0.0, 2 * PI, 1000).elementWise { radius * sin(it) + center[1] }
            lineStyle.color = Color.BLACK
        }
    }

    plot {
        scatter {
            x = List(inputData.size) { i -> inputData[i][0] }
            y = List(inputData.size) { i -> inputData[i][1] }

            markerStyle {
                color = Color.BLUE
                type = MarkerType.POINT
                alpha = 0.4
            }
        }
    }

    plot {
        line {
            x = List(nodeCount) { i -> mesh[i].value[0] }
            y = List(nodeCount) { i -> mesh[i].value[1] }

            lineStyle {
                color = Color.RED
                type = LineType.DOTTED
                width = 3
            }
        }
    }
}

fun SelfOrganizingMap.plotMapDataset(inputData: List<DoubleArray1D>) = showFigure {
    allPlots {
        position {
            columnCount = 1
            rowCount = 1
        }
    }

    plot {
        scatter {
            x = List(inputData.size) { i -> inputData[i][0] }
            y = List(inputData.size) { i -> inputData[i][1] }

            markerStyle {
                color = Color.BLUE
                type = MarkerType.CIRCLE
                alpha = 0.4
            }
        }
    }

    plot {
        scatter {
            x = List(nodeCount) { i -> mesh[i].value[0] }
            y = List(nodeCount) { i -> mesh[i].value[1] }

            markerStyle {
                color = Color.RED
                type = MarkerType.X
            }
        }
    }
}

fun SelfOrganizingMap.plotPathDataset(inputData: List<DoubleArray1D>) = showFigure {
    allPlots {
        position {
            columnCount = 1
            rowCount = 1
        }
    }

    plot {
        scatter {
            x = List(inputData.size) { i -> inputData[i][0] }
            y = List(inputData.size) { i -> inputData[i][1] }

            markerStyle {
                color = Color.BLUE
                type = MarkerType.CIRCLE
                alpha = 0.4
            }
        }
    }

    plot {
        line {
            x = List(nodeCount) { i -> mesh[i].value[0] }
            y = List(nodeCount) { i -> mesh[i].value[1] }

            lineStyle {
                color = Color.RED
                type = LineType.DOTTED
                width = 3
            }
        }
    }
}