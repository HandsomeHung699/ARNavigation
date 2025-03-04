import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import org.opencv.core.Point3

class PointCloudLoader(private val context: Context) {
    fun loadPointCloud(resourceId: Int): List<Point3> {
        val points = mutableListOf<Point3>()
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.forEachLine { line ->
            val tokens = line.trim().split(Regex("\\s+"))
            if (tokens.size >= 3) {
                try {
                    val x = tokens[0].toDouble()
                    val y = tokens[1].toDouble()
                    val z = tokens[2].toDouble()
                    points.add(Point3(x, y, z))
                } catch (e: NumberFormatException) {
                    println("Skip invalid line: $line")
                }
            }
        }
        reader.close()
        return points
    }
}
