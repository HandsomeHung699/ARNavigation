package com.example.arnavigation

import Graph
import Pathfinding
import android.content.Context
//import android.net.Uri
//import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import org.opencv.core.Point3

import android.os.Handler
import android.os.Looper

class NavigationController(
    private val context: Context,
    private val arFragment: ArFragment,
    private val graph: Graph
) {
    private val pathfinding = Pathfinding(graph)
    private var arrowRenderable: ModelRenderable? = null

    init {
        // Preload the model
        loadArrowModel()
    }

    private fun loadArrowModel() {
        // Use the new GLB loading approach
        ModelRenderable.builder()
            .setSource(context, R.raw.scene) // Path to your GLB file in assets
            // Alternatively, if you have the GLB file in your raw resources:
            // .setSource(context, R.raw.arrow_model)
            .setIsFilamentGltf(true) // Important: Set this to true for GLB files
            .build()
            .thenAccept { renderable ->
                arrowRenderable = renderable
            }
            .exceptionally { throwable ->
                throwable.printStackTrace()
                null
            }
    }

    private var lastPath: List<Point3> = emptyList()

    fun updateNavigation(cameraPosition: Vector3) {
        val currentNode = graph.getNearestNode(Point3(cameraPosition.x.toDouble(), cameraPosition.y.toDouble(), cameraPosition.z.toDouble()))
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

    fun addArrow(position: Vector3) {
        Handler(Looper.getMainLooper()).post {
            // Use the preloaded renderable if available
            arrowRenderable?.let { renderable ->
                val arrowNode = AnchorNode()
                arrowNode.renderable = renderable
                arrowNode.localPosition = position
                arFragment.arSceneView.scene.addChild(arrowNode)
            } ?: run {
                // If not yet loaded, load on demand
                ModelRenderable.builder()
                    .setSource(context, R.raw.scene)
                    .setIsFilamentGltf(true)
                    .build()
                    .thenAccept { renderable ->
                        val arrowNode = AnchorNode()
                        arrowNode.renderable = renderable
                        arrowNode.localPosition = position
                        arFragment.arSceneView.scene.addChild(arrowNode)
                    }
                    .exceptionally { throwable ->
                        throwable.printStackTrace()
                        null
                    }
            }
        }
    }
}
//package com.example.arnavigation
//
//import Graph
//import Pathfinding
//import android.content.Context
//import android.net.Uri
//import com.google.ar.sceneform.AnchorNode
//import com.google.ar.sceneform.math.Vector3
//import com.google.ar.sceneform.rendering.ModelRenderable
//import com.google.ar.sceneform.ux.ArFragment
//import org.opencv.core.Point3
//import android.os.Handler
//import android.os.Looper
//
//class NavigationController(
//    private val context: Context,
//    private val arFragment: ArFragment,
//    private val graph: Graph
//) {
//    private val pathfinding = Pathfinding(graph)
//    private var arrowRenderable: ModelRenderable? = null
//
//    init {
//        // Preload the model
//        loadArrowModel()
//    }
//
//    private fun loadArrowModel() {
//        ModelRenderable.builder()
//            .setSource(context, R.raw.Arrow5)
//            .build()
//            .thenAccept { renderable ->
//                arrowRenderable = renderable
//            }
//            .exceptionally { throwable ->
//                throwable.printStackTrace()
//                null
//            }
//    }
//
//    private var lastPath: List<Point3> = emptyList()
//
//    fun updateNavigation(cameraPosition: Vector3) {
//        val currentNode = graph.getNearestNode(Point3(
//            cameraPosition.x.toDouble(),
//            cameraPosition.y.toDouble(),
//            cameraPosition.z.toDouble()
//        ))
//        val destinationNode = graph.getDestinationNode()
//
//        if (currentNode != null && destinationNode != null) {
//            if (lastPath.isEmpty() || currentNode != lastPath.first()) {
//                lastPath = pathfinding.aStar(currentNode, destinationNode)
//                displayPath(lastPath)
//            }
//        }
//    }
//
//    private fun displayPath(path: List<Point3>) {
//        path.forEach { node ->
//            addArrow(Vector3(node.x.toFloat(), node.y.toFloat(), node.z.toFloat()))
//        }
//    }
//
//    fun addArrow(position: Vector3) {
//        Handler(Looper.getMainLooper()).post {
//            arrowRenderable?.let { renderable ->
//                val arrowNode = AnchorNode()
//                arrowNode.renderable = renderable
//                arrowNode.localPosition = position
//                arFragment.arSceneView.scene.addChild(arrowNode)
//            } ?: run {
//                ModelRenderable.builder()
//                    .setSource(context, R.raw.Arrow5)
//                    .build()
//                    .thenAccept { renderable ->
//                        val arrowNode = AnchorNode()
//                        arrowNode.renderable = renderable
//                        arrowNode.localPosition = position
//                        arFragment.arSceneView.scene.addChild(arrowNode)
//                    }
//                    .exceptionally { throwable ->
//                        throwable.printStackTrace()
//                        null
//                    }
//            }
//        }
//    }
//}