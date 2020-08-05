package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.eattogether_neep.R
import kotlinx.android.synthetic.main.activity_ranking.*
import kotlinx.android.synthetic.main.activity_waiting.*


class WaitingActivity : AppCompatActivity() {
    var second: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        // loading git
        Glide.with(this).load(R.drawable.loading).into(img_rotate)


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


