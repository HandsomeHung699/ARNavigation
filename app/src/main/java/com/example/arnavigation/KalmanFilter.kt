package com.example.arnavigation

import org.ejml.simple.SimpleMatrix
import org.opencv.core.Point3

//class KalmanFilter {
//
//    private var state = SimpleMatrix(3, 1) // Vị trí X, Y, Z
//    private var P = SimpleMatrix.identity(3) // Ma trận hiệp phương sai
//
//    fun predict(control: SimpleMatrix) {
//        state = state.plus(control)
//        P = P.plus(SimpleMatrix.identity(3).scale(0.1))
//    }
//
//    fun correct(point: Point3) {
//        // Convert single point to SimpleMatrix
//        val measurement = SimpleMatrix(3, 1)
//        measurement.set(0, 0, point.x)
//        measurement.set(1, 0, point.y)
//        measurement.set(2, 0, point.z)
//
//        // Existing Kalman filter correction logic
//        val K = P.mult(P.plus(SimpleMatrix.identity(3).scale(1.0)).invert())
//        state = state.plus(K.mult(measurement.minus(state)))
//        P = (SimpleMatrix.identity(3).minus(K)).mult(P)
//    }
//
//    fun getState(): SimpleMatrix {
//        return state
//    }
//}

import kotlin.math.sqrt
class KalmanFilter {
    private var state = SimpleMatrix(3, 1)
    private var P = SimpleMatrix.identity(3)

    fun correct(point: Point3): SimpleMatrix {
        if (distance(state, point) < 0.05) return state // Ignore small changes

        val measurement = SimpleMatrix(3, 1).apply {
            set(0, 0, point.x)
            set(1, 0, point.y)
            set(2, 0, point.z)
        }

        val K = P.mult(P.plus(SimpleMatrix.identity(3).scale(1.0)).invert())
        state = state.plus(K.mult(measurement.minus(state)))
        P = (SimpleMatrix.identity(3).minus(K)).mult(P)

        return state
    }

    fun getState(): SimpleMatrix {
        return state
    }

    private fun distance(a: SimpleMatrix, b: Point3): Double {
        return sqrt(
            (a[0, 0] - b.x) * (a[0, 0] - b.x) + (a[1, 0] - b.y) * (a[1, 0] - b.y) + (a[2, 0] - b.z) * (a[2, 0] - b.z)
        )
    }
}

