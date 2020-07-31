package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        btn_close_preference.setOnClickListener {
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1)
        }

        edt_favorite.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edt_favorite.text.toString() != "")
                    null_check_bd1.setBackgroundResource(R.drawable.yellow_bd)
                else if(edt_favorite.text.toString() == "")
                    null_check_bd1.setBackgroundResource(R.drawable.gray_bd)

                if (edt_favorite.text.toString() != "" && edt_hate.text.toString() != "") {
                    btn_preference_check.isEnabled = true
                    btn_preference_check.setBackgroundResource(R.drawable.btn_yellow)
                    btn_preference_check.setTextColor(Color.parseColor("#101010"))
                    btn_preference_check.setOnClickListener {
                        val favorite: String = edt_favorite.text.toString()
                        val hate: String = edt_hate.text.toString()
                        //postPreferenceResponse(favorite, hate)
                    }
                }
                else {
                    btn_preference_check.isEnabled = false
                    btn_preference_check.setBackgroundResource(R.drawable.btn_gray)
                    btn_preference_check.setTextColor(Color.parseColor("#959595"))
                }
            }
        })
        edt_hate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edt_hate.text.toString() != "")
                    null_check_bd2.setBackgroundResource(R.drawable.yellow_bd)
                else if(edt_hate.text.toString() == "")
                    null_check_bd2.setBackgroundResource(R.drawable.gray_bd)

                if (edt_favorite.text.toString() != "" && edt_hate.text.toString() != "") {
                    btn_preference_check.isEnabled = true
                    btn_preference_check.setBackgroundResource(R.drawable.btn_yellow)
                    btn_preference_check.setTextColor(Color.parseColor("#101010"))
                    btn_preference_check.setOnClickListener {
                        val favorite: String = edt_favorite.text.toString()
                        val hate: String = edt_hate.text.toString()
                        //postPreferenceResponse(favorite, hate)
                    }
                }
                else {
                    btn_preference_check.isEnabled = false
                    btn_preference_check.setBackgroundResource(R.drawable.btn_gray)
                    btn_preference_check.setTextColor(Color.parseColor("#959595"))                }
            }
        })
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
                Log.e("tag", "입력 실패")
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
