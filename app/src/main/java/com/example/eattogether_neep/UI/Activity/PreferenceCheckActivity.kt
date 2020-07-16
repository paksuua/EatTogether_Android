package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.NetworkService
import com.example.eattogether_neep.Network.Post.PostPreferenceResponse
import com.example.eattogether_neep.R
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_preference_check.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreferenceCheckActivity : AppCompatActivity() {

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_check)

        val intent = Intent(this, WaitingActivity::class.java)

        btn_preference_check.setOnClickListener {
            if(edt_favorite.text.isNullOrBlank()||edt_hate.text.isNullOrBlank()){
                Toast.makeText(this, "입력 내용을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(intent)

                val favorite: String = edt_favorite.text.toString()
                val hate: String = edt_hate.text.toString()
                //postPreferenceResponse(favorite, hate)
            }
        }
    }
    fun postPreferenceResponse(u_favorite:String, u_hate: String) {
        var jsonObject = JSONObject()
        jsonObject.put("favorite", u_favorite)
        jsonObject.put("hate", u_hate)

        val gsonObject = JsonParser().parse(jsonObject.toString()) as JsonObject
        val postPreferenceResponse: Call<PostPreferenceResponse> =
            networkService.postPreferenceResponse("application/json", gsonObject)
        postPreferenceResponse.enqueue(object: Callback<PostPreferenceResponse> {
            override fun onFailure(call: Call<PostPreferenceResponse>, t: Throwable) {
                Log.e("tag", "회원가입 실패")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<PostPreferenceResponse>, response: Response<PostPreferenceResponse>) {
                if (response.isSuccessful){
                    //Toast.makeText(this, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    if (response.body()!!.status == 200){
                        startActivity(intent)
                    }
                }
            }
        })
    }

}
