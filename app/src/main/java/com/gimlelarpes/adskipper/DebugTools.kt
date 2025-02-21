package com.gimlelarpes.adskipper

import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.text.iterator

object DebugTools {

    fun traverseChild(parent: AccessibilityNodeInfo) {
        val tag = parent.viewIdResourceName
        val nChildren = parent.childCount

        for (i in 0..nChildren-1) {
            val child = parent.getChild(i)

            val name = child.viewIdResourceName
            val isClickable = child.isClickable
            val text = child.text
            val description = child.contentDescription
            Log.i(tag, "idx $i :: $name - text: $text - desc: $description - isClickable: $isClickable")
        }
    }
    @Suppress("SENSELESS_COMPARISON")
    fun scanClickable(origin: AccessibilityNodeInfo, path: String = "") {
        val tag = origin.viewIdResourceName
        val nChildren = origin.childCount

        for (i in 0..nChildren-1) {

            val child = origin.getChild(i)
            val newPath = path + i.toString() // Trace steps taken

            val isClickable = child.isClickable
            val name = child.viewIdResourceName
            val text = child.text
            val description = child.contentDescription
            if (isClickable) {
                Log.i(
                    tag,
                    "pth $newPath :: $name - text: $text - desc: $description - isClickable: $isClickable"
                )
            } else {
                Log.v(
                    tag,
                    "pth $newPath :: $name - text: $text - desc: $description - isClickable: $isClickable"
                )
            }
            scanClickable(child, newPath)
        }

    }
    fun getChildFromPath(parent: AccessibilityNodeInfo?, path: String): AccessibilityNodeInfo? {
        // Construct tree
        var steps = arrayListOf<Int>()
        for (n in path) {
            steps.add(n.digitToInt())
        }

        // Traverse tree
        var currentParent = parent
        try {
            if (currentParent != null) {
                for (i in steps) {
                    // Prevent error spam by exiting early
                    if (i > currentParent!!.childCount - 1) {
                        return null
                    }

                    currentParent = currentParent.getChild(i)
                }
            }

        } catch (error: Exception) {
            Log.e("getChildFromPath", R.string.error_outdated.toString(), error)
            return null
        }
        return currentParent
    }
    fun scanFromXML(fileId: Int, sourcePackage: String, context: Context, rootInActiveWindow: AccessibilityNodeInfo?) {
        val tag = "scanFromXML"
        val viewIds = arrayListOf<String>()
        try { // Parse XML
            val stream: InputStream = context.resources.openRawResource(fileId)
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document: Document = documentBuilder.parse(stream)

            document.documentElement.normalize()
            Log.w(tag, "Scanning from: " + document.documentElement.nodeName)

            val publicNodes: NodeList = document.getElementsByTagName("public")

            for (i in 0 until publicNodes.length) {
                val publicElement: Element = publicNodes.item(i) as Element
                val nameAttribute: String? = publicElement.getAttribute("name")

                if (!nameAttribute.isNullOrBlank()) {
                    viewIds.add(nameAttribute)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error parsing XML: ${e.message}")
        }

        // Check viewIds
        var target: AccessibilityNodeInfo?
        for (id in viewIds) {
            target = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$sourcePackage$id")?.getOrNull(0)

            if (target != null) {
                val isClickable = target.isClickable
                val text = target.text
                val description = target.contentDescription
                if (isClickable) {
                    Log.i(
                        tag,
                        "viewId $id :: text: $text - desc: $description - isClickable: $isClickable"
                    )
                } else {
                    Log.v(
                        tag,
                        "viewId $id :: text: $text - desc: $description - isClickable: $isClickable"
                    )
                }
            }
        }
    }

}