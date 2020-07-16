package com.example.eattogether_neep.UI.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.eattogether_neep.R
import kotlinx.android.synthetic.main.activity_make_url.*
import kotlin.random.Random

class MakeUrlActivity : AppCompatActivity() {
    private lateinit var random_code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_url)

        // Get the clipboard system service
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        // 뒤로 가기
        btn_make_url_back.setOnClickListener {   finish()   }

        // 인원 입력 시 버튼 활성화
        edt_join_num.doOnTextChanged{ text1, start, count, after->
            if(!text1.isNullOrBlank()){
                btn_make_url.background =
                    ContextCompat.getDrawable(this,
                        R.drawable.bg_yellow
                    )
                //btn_make_url.isClickable=true
            }else{
                btn_make_url.background =
                    ContextCompat.getDrawable(this,
                        R.drawable.bg_gray
                    )
                //btn_make_url.isClickable=false
            }
        }

    btn_make_url.setOnClickListener {
        // 랜덤 참여코드 생성
        random_code= String.format("%06d", Random.nextInt(0 , 999999))
        btn_make_url.text=random_code

        // 참여코드 자동 복사
        val clip = ClipData.newPlainText("RANDOM UUID",random_code)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this,random_code+" 코드가 복사되었습니다.", Toast.LENGTH_LONG ).show()
        }
    }
}
