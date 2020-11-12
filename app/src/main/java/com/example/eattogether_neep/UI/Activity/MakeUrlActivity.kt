package com.example.eattogether_neep.UI.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.activity_make_url.*
import kotlinx.android.synthetic.main.activity_make_url.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MakeUrlActivity : AppCompatActivity() {
    private var random_code: String =""
    private var joincode: String =""
    private var clickFlag:Boolean=false
    private lateinit var uuid: String
    val requestToServer=ApplicationController // 싱글톤 그대로 가져옴
    var flag_joincode=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_url)

        // 뒤로 가기
        btn_makeurl_back.setOnClickListener {   finish()   }

        // 인원 입력 시 버튼 활성화
        edt_makeurl_number.doOnTextChanged{ text1, start, count, after->
            if(!text1.isNullOrBlank()){
                btn_makeurl_url.background =
                    ContextCompat.getDrawable(this, R.drawable.btn_yellow)
                edt_makeurl_number.background=
                    ContextCompat.getDrawable(this, R.drawable.yellow_bd)
                btn_makeurl_url.setTextColor(getColor(R.color.text_black))
            }else{
                btn_makeurl_url.background =
                    ContextCompat.getDrawable(this,
                        R.drawable.btn_gray
                    )
                btn_makeurl_url.setTextColor(getColor(R.color.text_gray2))
            }
        }

        btn_makeurl_url.setOnClickListener {
            if(edt_makeurl_number.text.isNullOrBlank()){
                Toast.makeText(this,"참여 인원을 입력하세요",Toast.LENGTH_SHORT).show()
            }else if (clickFlag){ // 다시 누르는 경우
                copyCode(joincode)
                //clickFlag=true
            }
            else{ // 처음 누르는 경우
                // 랜덤 참여코드 요청
                requestMakeUrl()
                clickFlag=!clickFlag
                //localMakeUrl()

                // 생성 코드 텍스트뷰 visible
                if (flag_joincode){   tv_makeurl_code.isVisible
                }else{  tv_makeurl_code.isInvisible  }
            }
        }
    }

    // MakeURL By Server
    private fun requestMakeUrl(){
        uuid = User.getUUID(this)

        requestToServer.networkService.postMakeUrlRequest(
            PostMakeUrlRequest(
                Integer.parseInt(edt_makeurl_number.text.toString())
            )
        ).enqueue(object : Callback<PostMakeUrlResponse> {
            override fun onFailure(call: Call<PostMakeUrlResponse>, t: Throwable){
                // 통신 실패
                Log.d("에러", t.message.toString())
                //Toast.makeText(this@MakeUrlActivity, "MakeUrl 통신 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<PostMakeUrlResponse>,
                response: Response<PostMakeUrlResponse>
            ) {
                //통신 성공
                Log.d("통신 성공", ": status "
                        +response.body()!!.status+ "  success "+response.body()!!.success+"  data "+response.body()!!.data!!.roomID)

                if(response.isSuccessful){ // statusCode가 200~300 사이일 때. 응답 body 이용 가능.
                    //Toast.makeText(this@MakeUrlActivity, "MakeUrl 통신 성공", Toast.LENGTH_SHORT).show()

                    flag_joincode=true
                    joincode=response.body()!!.data!!.roomID.toString()
                    tv_makeurl_code.text="생성 코드 : "+joincode
                    showCode()
                    copyCode(joincode)
                    }else{
                    Toast.makeText(this@MakeUrlActivity, "통신 Status Error", Toast.LENGTH_SHORT).show()
                    flag_joincode=false
                }
            }
        })
    }

    // MakeURL By Local
    private fun localMakeUrl(){
        // 랜덤 참여코드 생성
        random_code= String.format("%06d", Random.nextInt(0 , 999999))
        tv_makeurl_code.text="생성코드 : "+random_code
        showCode()
    }

    private fun showCode(){
        tv_makeurl_code.visibility= View.VISIBLE
        btn_makeurl_url.text="코드 복사"
    }

    private fun copyCode(code: String){
        // 참여코드 자동 복사
        // Get the clipboard system service
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RANDOM UUID",code)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this,code+" 코드가 복사되었습니다.", Toast.LENGTH_LONG ).show()
    }
}
