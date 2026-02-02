package com.j1soft.infinitecanvas



import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.j1soft.infinitecanvas.Tool
import com.j1soft.infinitecanvas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim) }

    private var clicked=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tool selection
        binding.penButton.setOnClickListener { binding.canvasView.setTool(Tool.PEN) }
        binding.pencilButton.setOnClickListener { binding.canvasView.setTool(Tool.PENCIL) }
        binding.eraserButton.setOnClickListener { binding.canvasView.setTool(Tool.ERASER) }
        binding.newButton.setOnClickListener { binding.canvasView.createNew() }
        binding.undoButton.setOnClickListener { binding.canvasView.undo() }
        binding.redoButton.setOnClickListener { binding.canvasView.redo() }

        binding.expand.setOnClickListener{onExpandBtnClicked()}




        // Zoom controls
//        binding.zoomInButton.setOnClickListener { binding.canvasView.zoomIn() }
//        binding.zoomOutButton.setOnClickListener { binding.canvasView.zoomOut() }

        // Save/export
//        binding.saveButton.setOnClickListener {
//            val result = binding.canvasView.saveDrawing()
//            if (result) {
//                Toast.makeText(this, "Drawing saved successfully!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Failed to save drawing.", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun onExpandBtnClicked() {

            setVisibility(clicked)
            setAnimation(clicked)
            setOnclickable(clicked)
            clicked=!clicked

    }

    private fun setOnclickable(clicked:Boolean) {
        if(!clicked)
        {
            binding.redoButton.visibility= View.VISIBLE
            binding.undoButton.visibility= View.VISIBLE
            binding.eraserButton.visibility= View.VISIBLE
            binding.pencilButton.visibility= View.VISIBLE
            binding.newButton.visibility= View.VISIBLE

            binding.penButton.visibility= View.VISIBLE
        }
        else
        {
            binding.redoButton.visibility= View.INVISIBLE
            binding.undoButton.visibility= View.INVISIBLE
            binding.eraserButton.visibility= View.INVISIBLE
            binding.pencilButton.visibility= View.INVISIBLE
            binding.newButton.visibility= View.INVISIBLE
            binding.penButton.visibility= View.INVISIBLE

        }



    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked)
        {
            binding.redoButton.startAnimation(fromBottom)
            binding.undoButton.startAnimation(fromBottom)
            binding.eraserButton.startAnimation(fromBottom)
            binding.pencilButton.startAnimation(fromBottom)
            binding.penButton.startAnimation(fromBottom)
            binding.newButton.startAnimation(fromBottom)
            binding.expand.startAnimation(rotateOpen)

        }
        else
        {
            binding.redoButton.startAnimation(toBottom)
            binding.undoButton.startAnimation(toBottom)
            binding.eraserButton.startAnimation(toBottom)
            binding.pencilButton.startAnimation(toBottom)
            binding.penButton.startAnimation(toBottom)
            binding.newButton.startAnimation(toBottom)
            binding.expand.startAnimation(rotateClose)

        }


    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked)
        {
            binding.redoButton.isClickable=true
            binding.undoButton.isClickable=true
            binding.eraserButton.isClickable=true
            binding.pencilButton.isClickable=true
            binding.penButton.isClickable=true
            binding.newButton.isClickable=true

        }
        else
        {
            binding.redoButton.isClickable=false
            binding.undoButton.isClickable=false
            binding.eraserButton.isClickable=false
            binding.pencilButton.isClickable=false
            binding.penButton.isClickable=false
            binding.newButton.isClickable=false
        }



    }
}