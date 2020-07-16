package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.eattogether_neep.R
import kotlinx.android.synthetic.main.activity_waiting.*

class WaitingActivity : AppCompatActivity() {
    private lateinit var img_rotate: ImageView
    var second: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        img_rotate=findViewById(R.id.img_rotate)
        animationRotate()

        cst_wait.setOnClickListener {
            val intent = Intent(this, LaunchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun animationRotate(){
        val rotate=AnimationUtils.loadAnimation(this,
            R.anim.rotate_figure
        )
        img_rotate.animation=rotate
    }
}


