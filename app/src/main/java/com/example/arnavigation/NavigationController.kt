package com.example.arnavigation

import Graph
import Pathfinding
import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import org.opencv.core.Point3

import android.os.Handler
import android.os.Looper
import com.example.arnavigation.R.raw.arrow_model as arrow_model1

class NavigationController(
    private val context: Context,
    private val arFragment: ArFragment,
    private val graph: Graph
) {
    private val pathfinding = Pathfinding(graph)

//    fun updateNavigation(cameraPosition: Vector3) {
//        val currentNode = graph.getNearestNode(Point3(cameraPosition.x.toDouble(), cameraPosition.y.toDouble(), cameraPosition.z.toDouble()))
//
//        // For testing purpose:
//        graph.getNearestNode(Point3(cameraPosition.x.toDouble(), cameraPosition.y.toDouble(), cameraPosition.z.toDouble()))?.let {
//            graph.setDestinationNode(it)
//        }
//
//        val destinationNode = graph.getDestinationNode() // Giả sử điểm đích được xác định trước
//
//        if (currentNode != null && destinationNode != null) {
//            val path = pathfinding.aStar(currentNode, destinationNode)
//
//            if (path.isNotEmpty()) {
//                displayPath(path)
//            }
//        }
//    }

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
            ModelRenderable.builder()
                .setSource(context, arrow_model1)
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
