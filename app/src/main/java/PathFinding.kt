import org.opencv.core.Point3
import kotlin.math.abs
import kotlin.math.sqrt

class Pathfinding(private val graph: Graph) {
    fun aStar(start: Point3, goal: Point3): List<Point3> {
        val openSet = mutableListOf(start)
        val cameFrom = mutableMapOf<Point3, Point3?>()
        val gScore = mutableMapOf<Point3, Double>().withDefault { Double.MAX_VALUE }
        val fScore = mutableMapOf<Point3, Double>().withDefault { Double.MAX_VALUE }

        gScore[start] = 0.0
        fScore[start] = heuristic(start, goal)

        while (openSet.isNotEmpty()) {
            val current = openSet.minByOrNull { fScore.getValue(it) } ?: break
            if (current == goal) return reconstructPath(cameFrom, current)

            openSet.remove(current)

            for (neighbor in graph.getNeighbors(current)) {
                val tentativeGScore = gScore.getValue(current) + distance(current, neighbor)
                if (tentativeGScore < gScore.getValue(neighbor)) {
                    cameFrom[neighbor] = current
                    gScore[neighbor] = tentativeGScore
                    fScore[neighbor] = tentativeGScore + heuristic(neighbor, goal)
                    if (!openSet.contains(neighbor)) openSet.add(neighbor)
                }
            }
        }
        return emptyList()
    }

    private fun reconstructPath(cameFrom: Map<Point3, Point3?>, current: Point3): List<Point3> {
        val path = mutableListOf(current)
        var step = current
        while (cameFrom.containsKey(step)) {
            step = cameFrom[step] ?: break
            path.add(step)
        }
        return path.reversed()
    }

    private fun distance(a: Point3, b: Point3): Double {
        return sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z))
    }

    private fun heuristic(a: Point3, b: Point3): Double {
        return abs(a.x - b.x) + abs(a.y - b.y) + abs(a.z - b.z)
    }
}
