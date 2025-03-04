import org.opencv.core.*
import org.opencv.features2d.ORB
import org.opencv.features2d.DescriptorMatcher

class FeatureMatcher {
    private val orb = ORB.create(500)
    private val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

    fun detectFeatures(image: Mat): Pair<MatOfKeyPoint, Mat> {
        val keyPoints = MatOfKeyPoint()
        val descriptors = Mat()
        orb.detectAndCompute(image, Mat(), keyPoints, descriptors)
        return Pair(keyPoints, descriptors)
    }

    fun matchFeatures(descriptors1: Mat, descriptors2: Mat): List<DMatch> {
        if (descriptors1.empty() || descriptors2.empty()) return emptyList()

        val matches = MatOfDMatch()
        matcher.match(descriptors1, descriptors2, matches)

        return matches.toList().sortedBy { it.distance }.take(30) // Giữ 50 đặc trưng tốt nhất
    }
}
