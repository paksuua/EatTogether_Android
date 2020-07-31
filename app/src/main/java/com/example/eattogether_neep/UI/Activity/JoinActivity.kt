package com.example.eattogether_neep.UI.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {
    private lateinit var uuid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        uuid = User.getUUID(this)

        btn_join_url.setOnClickListener{
            if (edt_url.text.isNullOrBlank()){
                Toast.makeText(this, "참여코드를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, PreferenceCheckActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
