package com.example.arnavigation

import CameraPoseEstimator
import FeatureMatcher
import Graph
import PointCloudLoader
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import org.opencv.core.Mat
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private lateinit var navigationController: NavigationController
    private lateinit var graph: Graph
    private lateinit var cameraPoseEstimator: CameraPoseEstimator
    private lateinit var kalmanFilter: KalmanFilter
    private lateinit var featureMatcher: FeatureMatcher
    private var pointCloudDescriptors: Mat = Mat()

    companion object {
        init {
            System.loadLibrary("opencv_java4") // or "opencv_java3" depending on your version
        }
    }

    private val processingScope = CoroutineScope(Dispatchers.Default) // Background thread

    override fun onCreate(savedInstanceState: Bundle?) {
        println("111111")
        super.onCreate(savedInstanceState)
//        maybeEnableArButton()
        setContentView(R.layout.activity_ar)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        graph = Graph()
        val pointCloud = PointCloudLoader(this).loadPointCloud(R.raw.point_cloud)
        graph.buildFromPointCloud(pointCloud)

        cameraPoseEstimator = CameraPoseEstimator(this, graph)
        kalmanFilter = KalmanFilter()
        featureMatcher = FeatureMatcher()

        // Tạo descriptor cho point cloud
        pointCloudDescriptors = featureMatcher.detectFeatures(Mat()).second

        navigationController = NavigationController(this, arFragment, graph)

        println("222222_6_so_2")
        arFragment.arSceneView.scene.addOnUpdateListener {
            processingScope.launch {
                println("333333_6_so_3")
                processCameraFrame()
            }
        }

//        // Cập nhật vị trí camera
//        arFragment.arSceneView.scene.addOnUpdateListener {
//            val cameraPose = arFragment.arSceneView.arFrame?.camera?.pose
//            if (cameraPose != null) {
//                val cameraImage = getCameraImage() // Hàm lấy ảnh từ camera
//                val estimatedNode = cameraPoseEstimator.estimatePose(cameraImage, pointCloudDescriptors)
//
//                if (estimatedNode != null) {
//                    val filteredNode = kalmanFilter.correct(estimatedNode) // 🛠 Sử dụng Kalman Filter
//                    val filteredState = kalmanFilter.getState()
//                    val filteredVector = Vector3(
//                        filteredState.get(0, 0).toFloat(),
//                        filteredState.get(1, 0).toFloat(),
//                        filteredState.get(2, 0).toFloat()
//                    )
//                    navigationController.updateNavigation(filteredVector)
//
//                }
//            }
//        }
    }

    private suspend fun processCameraFrame() {
        val cameraPose = withContext(Dispatchers.Main) { arFragment.arSceneView.arFrame?.camera?.pose }
        if (cameraPose != null) {
            println("444444_6_so_4")
            val cameraImage = getCameraImage()
            println("555555_6_so_5")
            navigationController.addArrow(Vector3((1).toFloat(), (1).toFloat(), (1).toFloat()))
            val estimatedNode = cameraPoseEstimator.estimatePose(cameraImage, pointCloudDescriptors)

            if (estimatedNode != null) {
                val filteredNode = kalmanFilter.correct(estimatedNode) // 🛠 Sử dụng Kalman Filter
                val filteredState = kalmanFilter.getState()
                val filteredVector = Vector3(
                    filteredState.get(0, 0).toFloat(),
                    filteredState.get(1, 0).toFloat(),
                    filteredState.get(2, 0).toFloat()
                )
                println("Been here")
                withContext(Dispatchers.Main) {
                    navigationController.updateNavigation(filteredVector)
                }
            }
        }
    }

    private fun getCameraImage(): Mat {
        // Chuyển ảnh camera từ ARCore thành OpenCV Mat
        return Mat()
    }

    private var mUserRequestedInstall = true
    private var mSession: Session? = null

    override fun onResume() {
        super.onResume()
        // Check camera permission.

        // Ensure that Google Play Services for AR and ARCore device profile data are
        // installed and up to date.
        try {
            if (mSession == null) {
                when (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        // Success: Safe to create the AR session.
                        mSession = Session(this)
                    }

                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        // When this method returns `INSTALL_REQUESTED`:
                        // 1. ARCore pauses this activity.
                        // 2. ARCore prompts the user to install or update Google Play
                        //    Services for AR (market://details?id=com.google.ar.core).
                        // 3. ARCore downloads the latest device profile data.
                        // 4. ARCore resumes this activity. The next invocation of
                        //    requestInstall() will either return `INSTALLED` or throw an
                        //    exception if the installation or update did not succeed.
                        this.mUserRequestedInstall = false
                        return
                    }
                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                .show()
            return
        }
    }

//    private fun maybeEnableArButton() {
//        ArCoreApk.getInstance().checkAvailabilityAsync(this) { availability ->
//            if (availability.isSupported) {
//                println("Your device supports ARCore")
//            } else {
//                println("The device does not support ARCore or unknown.")
//            }
//        }
//    }
}
