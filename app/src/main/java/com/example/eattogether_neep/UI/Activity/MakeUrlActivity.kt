package com.example.eattogether_neep.UI.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.Post.PostMakeUrlRequest
import com.example.eattogether_neep.Network.Post.PostMakeUrlResponse
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_make_url.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MakeUrlActivity : AppCompatActivity() {
    private lateinit var random_code: String
    private lateinit var uuid: String
    // Get the clipboard system service
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val requestToServer=ApplicationController // 싱글톤 그대로 가져옴
    var flag_joincode=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_url)




        // 뒤로 가기
        btn_makeurl_back.setOnClickListener {   finish()   }

        // 인원 입력 시 버튼 활성화
        btn_makeurl_url.doOnTextChanged{ text1, start, count, after->
            if(!text1.isNullOrBlank()){
                btn_makeurl_url.background =
                    ContextCompat.getDrawable(this,
                        R.drawable.bg_yellow
                    )
                //btn_make_url.isClickable=true
            }else{
                btn_makeurl_url.background =
                    ContextCompat.getDrawable(this,
                        R.drawable.bg_gray
                    )
                //btn_make_url.isClickable=false
            }
        }

        btn_makeurl_url.setOnClickListener {
            if(edt_makeurl_number.text.isNullOrBlank()){
                Toast.makeText(this,"참여 인원을 입력하세요",Toast.LENGTH_SHORT).show()
            }else{
                // 랜덤 참여코드 생성
                localMakeUrl()

                // 생성 코드 텍스트뷰 visible
                if (flag_joincode){
                    tv_makeurl_code.isVisible
                }else{
                    tv_makeurl_code.isInvisible
                }
            }

            // 참여코드 자동 복사
            val clip = ClipData.newPlainText("RANDOM UUID",random_code)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this,random_code+" 코드가 복사되었습니다.", Toast.LENGTH_LONG ).show()
        }
    }

    // MakeURL By Server
    private fun requestMakeUrl(number:Int){
        uuid = User.getUUID(this)
        
        requestToServer.networkService.postMakeUrlRequest(
            PostMakeUrlRequest(
                uuid, Integer.parseInt(edt_makeurl_number.text.toString())
            )
        ).enqueue(object : Callback<PostMakeUrlResponse> {
            override fun onFailure(call: Call<PostMakeUrlResponse>, t: Throwable){
                // 통신 실패
                Log.e("에러", t.toString())
                Toast.makeText(this@MakeUrlActivity, "MakeUrl 통신 실패",
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<PostMakeUrlResponse>,
                response: Response<PostMakeUrlResponse>
            ) {
                //통신 성공
                if(response.isSuccessful){ // statusCode가 200~300 사이일 때. 응답 body 이용 가능.
                    Toast.makeText(this@MakeUrlActivity, "MakeUrl 통신 성공", Toast.LENGTH_SHORT).show()

                    flag_joincode=true
                    tv_makeurl_code.text="생성 코드 : "+response.body()!!.join_code
                }else{
                    flag_joincode=false
                }
            }
        })
    }

    // MakeURL By Local
    private fun localMakeUrl(){
        // 랜덤 참여코드 생성
        random_code= String.format("%06d", Random.nextInt(0 , 999999))
        btn_makeurl_url.text=random_code
    }
}
