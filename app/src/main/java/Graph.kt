//import org.opencv.core.Point3
import kotlin.math.sqrt

//class Graph {
//    private val nodes = mutableMapOf<Point3, MutableList<Point3>>()
//    private var destinationNode: Point3? = null
//
//    fun buildFromPointCloud(points: List<Point3>) {
//        for (point in points) {
//            nodes[point] = mutableListOf()
//            for (neighbor in points) {
//                if (distance(point, neighbor) < 1.0) { // Chỉ kết nối điểm gần nhau
//                    nodes[point]?.add(neighbor)
//                }
//            }
//        }
//    }
//
//    fun getNodeByFeature(featureIndex: Int): Point3? {
//        return nodes.keys.elementAtOrNull(featureIndex)
//    }
//
//    fun getNearestNode(position: Point3): Point3? {
//        return nodes.keys.minByOrNull { distance(it, position) }
//    }
//
//    fun setDestinationNode(destination: Point3) {
//        this.destinationNode = destination
//    }
//
//    fun getDestinationNode(): Point3? {
//        return destinationNode
//    }
//
//    fun getNeighbors(point: Point3): List<Point3> = nodes[point] ?: emptyList()
//
//    private fun distance(a: Point3, b: Point3) = sqrt(
//        (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z)
//    )
//}

import org.opencv.core.Point3
import kotlin.math.sqrt

class Graph {
    private val nodes = mutableListOf<Point3>()
    private var destinationNode: Point3? = null

    fun buildFromPointCloud(points: List<Point3>) {
        nodes.clear()
        nodes.addAll(points)
    }

    fun getNodeByFeature(featureIndex: Int): Point3? {
        return nodes.getOrNull(featureIndex) // ✅ Correct way to access an element in a list
    }

    fun getNearestNode(position: Point3): Point3? {
        return nodes.minByOrNull { distance(it, position) }
    }

    fun setDestinationNode(destination: Point3) {
        this.destinationNode = destination
    }

    fun getDestinationNode(): Point3? {
        return destinationNode
    }

    fun getNeighbors(point: Point3): List<Point3> {
        return nodes.filter {
            distance(it, point) < 1.0
        } // ✅ Find nearby points instead of indexing
    }

    private fun distance(a: Point3, b: Point3) = sqrt(
        (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z)
    )
}


