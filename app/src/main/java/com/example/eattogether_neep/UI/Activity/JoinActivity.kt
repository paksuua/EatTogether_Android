package com.example.eattogether_neep.UI.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.Post.PostJoinRequest
import com.example.eattogether_neep.Network.Post.PostMakeUrlResponse
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.activity_make_url.*
import retrofit2.Call
import retrofit2.Response
import com.example.eattogether_neep.Network.Post.PostJoinResponse
import retrofit2.Callback

class JoinActivity : AppCompatActivity() {
    private lateinit var uuid: String
    //val requestToServer= ApplicationController // 싱글톤 그대로 가져옴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_join)

                uuid = User.getUUID(this)
                Log.d("Device UUID:",uuid)

                // 인원 입력 시 버튼 활성화
                edt_join_url.doOnTextChanged{ text1, start, count, after->
                    if(!text1.isNullOrBlank()){
                        btn_join_url.background =
                            ContextCompat.getDrawable(this, R.drawable.btn_yellow)
                        edt_join_url.background=
                            ContextCompat.getDrawable(this, R.drawable.yellow_bd)
                        btn_join_url.setTextColor(getColor(R.color.text_black))
                    }else{
                        btn_join_url.background =
                            ContextCompat.getDrawable(this,
                                R.drawable.btn_gray
                            )
                        btn_join_url.setTextColor(getColor(R.color.text_gray2))
            }
        }

        btn_join_url.setOnClickListener{
            if (edt_join_url.text.isNullOrBlank()){
                Toast.makeText(this, "참여코드를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                //requestJoin(Integer.parseInt(edt_join_url.text.toString()))
                localJoin(edt_join_url.text.toString())
            }
        }
    }

    // Join By Server
    /*private fun requestJoin(number:Int){
        uuid = User.getUUID(this)


        requestToServer.networkService.postJoinRequest(
            PostJoinRequest(
                uuid, edt_join_url.text.toString()
            )
        ).enqueue(object : Callback<PostJoinResponse> {
            override fun onFailure(call: Call<PostJoinResponse>, t: Throwable){
                // 통신 실패
                Log.e("에러", t.toString())
                Toast.makeText(this@JoinActivity, "Join 통신 실패",Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<PostJoinResponse>,
                response: Response<PostJoinResponse>
            ) {
                //통신 성공
                if(response.isSuccessful){ // statusCode가 200~300 사이일 때. 응답 body 이용 가능.
                    Toast.makeText(this@JoinActivity, "Join 통신 성공", Toast.LENGTH_SHORT).show()

                    val intent=Intent(this@JoinActivity, PreferenceCheckActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Log.d("error message: ",response.errorBody()!!.string())
                }
            }
        })
    }*/

    // Join By Local
    private fun localJoin(number:String) {
        val intent=Intent(this@JoinActivity, PreferenceCheckActivity::class.java)
        startActivity(intent)
        finish()
    }
}
