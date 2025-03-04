import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.SizeF
import org.opencv.core.Mat
import org.opencv.core.CvType

object CameraUtils {

    fun getCameraMatrix(context: Context): Pair<Mat, SizeF?> {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0] // Lấy camera đầu tiên (thường là camera sau)
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)

        // Lấy tiêu cự của camera
        val focalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.get(0)
        // Lấy kích thước cảm biến
        val sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)

        if (focalLength != null && sensorSize != null) {
            val fx = focalLength.toDouble() * 1000 // Chuyển đổi từ mm sang px
            val fy = focalLength.toDouble() * 1000
            val cx = sensorSize.width.toDouble() / 2
            val cy = sensorSize.height.toDouble() / 2

            // Tạo Camera Matrix (Intrinsic Matrix)
            val cameraMatrix = Mat(3, 3, CvType.CV_64F).apply {
                put(0, 0, fx)
                put(0, 1, 0.0)
                put(0, 2, cx)
                put(1, 0, 0.0)
                put(1, 1, fy)
                put(1, 2, cy)
                put(2, 0, 0.0)
                put(2, 1, 0.0)
                put(2, 2, 1.0)
            }
            return Pair(cameraMatrix, sensorSize)
        }
        return Pair(Mat.eye(3, 3, CvType.CV_64F), null)
    }
}
