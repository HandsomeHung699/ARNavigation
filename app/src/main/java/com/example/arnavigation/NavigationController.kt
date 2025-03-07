package com.example.arnavigation

import Graph
import Pathfinding
import android.content.Context
import android.os.Handler
import android.os.Looper
//import com.google.android.filament.Engine
//import com.google.android.filament.utils.*
//import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.ArFragment
import io.github.sceneview.ar.node.ArModelNode
import org.opencv.core.Point3
//import com.google.android.filament.utils.Float3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation

//import java.nio.ByteBuffer

class NavigationController(
    private val context: Context,
    private val arFragment: ArFragment,
    private var sceneView: ArSceneView,
    private val graph: Graph
) {
    private val pathfinding = Pathfinding(graph)

    private var lastPath: List<Point3> = emptyList()

    fun updateNavigation(cameraPosition: Vector3) {
        val currentNode = graph.getNearestNode(
            Point3(cameraPosition.x.toDouble(), cameraPosition.y.toDouble(), cameraPosition.z.toDouble())
        )
        val destinationNode = graph.getDestinationNode()

        if (currentNode != null && destinationNode != null) {
            if (lastPath.isEmpty() || currentNode != lastPath.first()) {
                lastPath = pathfinding.aStar(currentNode, destinationNode)
                displayPath(lastPath)
            }
        }
    }

    private fun displayPath(path: List<Point3>) {
        path.forEach { node ->
            addArrow(Vector3(node.x.toFloat(), node.y.toFloat(), node.z.toFloat()))
        }
    }

    /**
     * Loads and displays a .glb navigation arrow using Filament under the hood. This code
     * illustrates how you might integrate Filament-based loading into a typical ARCore Sceneform flow.
     * Note that sceneform uses Filament internally, but direct Filament usage can be more complex.
     */
    fun addArrow(position: Vector3) {
        Handler(Looper.getMainLooper()).post {
            val arrowNode = ArModelNode().apply {
                loadModelGlbAsync(R.raw.arrow_model.toString()) {
                    this.position = Position(position.x, position.y, position.z) // ✅ Correct position type
                    this.rotation = Rotation(0f, 180f, 0f) // ✅ Correct rotation type
                    isVisible = true  // Ensure it's visible after loading
                }
            }

            // ✅ Add the arrow model node to the AR scene
            sceneView.addChild(arrowNode)
        }
    }

}