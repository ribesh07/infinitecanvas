package com.j1soft.infinitecanvas


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import kotlin.math.log

class InfiniteCanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var currentPath = Path()
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private val undopaths= mutableListOf<Pair<Path,Paint>>()

    private var paint = createPenPaint()
    private var currentTool = Tool.PEN

    private var scaleFactor = 1.0f
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())


    private val gestureDetector = GestureDetector(context, GestureListener())
    private var offsetX = 0f
    private var offsetY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var focusX = 0f
    private var focusY = 0f
//    private var focalX=0f
//    private var focalY=0f
    private var isPanning=false
    private var isZooming=false



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        // Apply zoom and pan
         canvas.translate(offsetX, offsetY)
        canvas.scale(scaleFactor, scaleFactor, focusX, focusY)

            for ((path, paint) in paths) {
                val scaledPaint = Paint(paint).apply {
                    strokeWidth = paint.strokeWidth
                }
                canvas.drawPath(path, scaledPaint)

            }
            if (!currentPath.isEmpty) {
                val scaledCurrentPaint = Paint(paint).apply {
                    strokeWidth = paint.strokeWidth
                }
                canvas.drawPath(currentPath, scaledCurrentPaint)


            }





        canvas.restore()
    }






    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        scaleDetector.onTouchEvent(event)
//        if (isZooming) {// this will also cause auto straight line draw during pinch to zoom due to motion event
//            if (event.actionMasked == MotionEvent.ACTION_UP) {
//                isZooming = false // Reset flag on touch release
//            }
//            return true
//        }
//        if (scaleDetector.isInProgress) {// this will cause a little zigger in zooming and panning as it checks every time for scaledetector action
//            isZooming = true
//            return true
//        }
        gestureDetector.onTouchEvent(event)
      val pointerCount=event.pointerCount


//        if(pointerCount>1 || scaleDetector.isInProgress)// this was the issue for auto straight line drawing
//        {
//            isPanning=false
//            return true
//        }


//             val x = (event.x - offsetX) / scaleFactor
//             val y = (event.y - offsetY) /scaleFactor
        val x = (event.x / scaleFactor) - (offsetX / scaleFactor)// revised formula for position mapping
        val y = (event.y / scaleFactor) - (offsetY / scaleFactor)

       if(currentTool==Tool.PEN)
       {
           paint=createPenPaint()

       }
        else if(currentTool==Tool.PEN)
        {
            paint=createPencilPaint()
        }
        else if(currentTool==Tool.ERASER)
       {
            paint=createEraserPaint()
       }


            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount > 1 || isZooming || isPanning) return true
                    isPanning = false


                    lastTouchX = event.x
                    lastTouchY = event.y


                    if (currentTool == Tool.PEN || currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {

                        currentPath = Path()
                        currentPath.moveTo(x, y)
                        undopaths.clear()

                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isPanning || isZooming) return true

                    if (currentTool == Tool.PEN || currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {

                        // Draw lines when a tool is selected
                        if (!scaleDetector.isInProgress) {

                            currentPath.lineTo(x, y)



                            // offset your objects with (dX, dY)
                            invalidate()
                        }


                    }

                }

                MotionEvent.ACTION_UP -> {
                    if (!isPanning && !isZooming && (currentTool == Tool.PEN || currentTool == Tool.PENCIL || currentTool == Tool.ERASER)) {
                        currentPath.lineTo(x, y)
                        //paths.add(Pair(currentPath, Paint(paint)))
                        val pathCopy = Path(currentPath)
                        val paintCopy = Paint(paint)
                        paths.add(Pair(pathCopy, paintCopy))
                        currentPath = Path()

                        invalidate()
                    }
                    isPanning = false

                }
            }




            return true




    }

    fun setTool(tool: Tool) {
        currentTool = tool
        paint = when (tool) {
            Tool.PEN -> createPenPaint()
            Tool.PENCIL ->createPencilPaint()
            Tool.ERASER -> createEraserPaint()
        }
    }
fun createNew()
{
    paths.clear()
    undopaths.clear()
    currentPath.reset()
    currentTool=Tool.PEN
    offsetX=0f
    offsetY=0f
    scaleFactor=1.0f
    invalidate()
}

    fun undo()
    {

        if(paths.isNotEmpty()) {

            undopaths.add(paths.removeAt(paths.size - 1))
            currentPath.reset()
            postInvalidate()
        }



    }

   fun redo()
   {


       if(undopaths.isNotEmpty()) {
           paths.add(undopaths.removeAt(undopaths.size - 1))

           currentPath.reset()
           postInvalidate()
       }

   }






//    fun zoomIn() {
//        scaleFactor *= 1.1f
//        invalidate()
//    }
//
//    fun zoomOut() {
//        scaleFactor /= 1.1f
//
//        invalidate()
//    }

//    fun saveDrawing(): Boolean {
//        return try {
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(bitmap)
//            draw(canvas)
//
//            val file = File(context.getExternalFilesDir(null), "drawing_${System.currentTimeMillis()}.png")
//            FileOutputStream(file).use { out ->
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//            }
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

    private fun createPenPaint() = Paint().apply {
       // xfermode=xfermode.apply { PorterDuff.Mode.SRC_OUT }
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f/scaleFactor
        isAntiAlias = true
    }
    private fun createPencilPaint() = Paint().apply {
      //  xfermode=xfermode.apply { PorterDuff.Mode.SRC_OUT }
        color = Color.rgb(140,140,140)
        style = Paint.Style.STROKE
        strokeWidth = 10f/scaleFactor
        isAntiAlias = true
    }

    private fun createEraserPaint() = Paint().apply {
     //   xfermode = xfermode.apply { PorterDuff.Mode.CLEAR }
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 50/scaleFactor
        isAntiAlias = true

    }


    // Scale listener for pinch-to-zoom
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        private var lastFocusX=0f
//        private var lastFocusY=0f
//        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
//            detector?.let {
//                focalX = (it.focusX - offsetX) / scaleFactor
//                focalY = (it.focusY - offsetX) / scaleFactor
//            }
//            return true
//        }
//        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
//            lastFocusX=detector.focusX
//            lastFocusY=detector.focusY
//            return true
//        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isZooming=true
            return true
        }
        override fun onScale(detector: ScaleGestureDetector): Boolean {
         val prevScale=scaleFactor
//    val dx= detector.focusX-lastFocusX
//    val dy= detector.focusY-lastFocusY
//    offsetX+=dx
//    offsetY+=dy
            scaleFactor *= detector.scaleFactor
//          offsetX-=(detector.focusX-(detector.focusX-offsetX)*(scaleFactor/prevScale-1))
//          offsetY-=(detector.focusY-(detector.focusY-offsetY)*(scaleFactor/prevScale-1))
//
//            lastFocusX = detector.focusX
//            lastFocusY = detector.focusY
    //scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)
            // focusX=detector.focusX// these two are problems for touch not matching drawing point due to no val
            // focusY=detector.focusY

    val focusX=detector.focusX
    val focusY=detector.focusY
//    offsetX = focusX - (focusX - offsetX) * (scaleFactor / prevScale)
//    offsetY = focusY - (focusY - offsetY) * (scaleFactor / prevScale)
    offsetX += (focusX - offsetX) * (1 - scaleFactor / prevScale)
    offsetY += (focusY - offsetY) * (1 - scaleFactor / prevScale)
            paint.strokeWidth = paint.strokeWidth * (prevScale / scaleFactor)

            invalidate()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isZooming=false


        }
    }

    // Gesture listener for panning (drag to move canvas)
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                p1: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {


                if (p1.pointerCount > 1 ) {
                    isPanning=true
                    offsetX -= distanceX
                    offsetY -= distanceY
                    invalidate()
                    return true
                }
                return false

        }
    }
}