import org.opencv.core.*
import org.opencv.calib3d.Calib3d
import android.content.Context

class CameraPoseEstimator(private val context: Context, private val graph: Graph) {
    private val featureMatcher = FeatureMatcher()

    fun estimatePose(image: Mat, pointCloudDescriptors: Mat): Point3? {
        val (imageKeyPoints, imageDescriptors) = featureMatcher.detectFeatures(image)
        val matches = featureMatcher.matchFeatures(imageDescriptors, pointCloudDescriptors)

        if (matches.isEmpty()) return null

        val imagePoints = mutableListOf<Point>()
        val objectPoints = mutableListOf<Point3>()

        for (match in matches) {
            val imgPt = imageKeyPoints.toArray()[match.queryIdx].pt
            val objPt = graph.getNodeByFeature(match.trainIdx) ?: continue
            imagePoints.add(Point(imgPt.x.toDouble(), imgPt.y.toDouble()))
            objectPoints.add(objPt)
        }

        if (imagePoints.size < 4) return null // Cần ít nhất 4 điểm để chạy PnP

        val cameraMatrix = CameraUtils.getCameraMatrix(context).first
        val imageMat = MatOfPoint2f(*imagePoints.toTypedArray())
        val objectMat = MatOfPoint3f(*objectPoints.toTypedArray())
        val rvec = Mat()
        val tvec = Mat()

        val success = Calib3d.solvePnP(objectMat, imageMat, cameraMatrix, MatOfDouble(), rvec, tvec)

        return if (success) {
            Point3(tvec[0, 0][0], tvec[1, 0][0], tvec[2, 0][0])
        } else {
            null
        }
    }
}
