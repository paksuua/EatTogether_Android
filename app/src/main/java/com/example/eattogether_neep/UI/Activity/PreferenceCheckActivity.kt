package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.MultiAutoCompleteTextView
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
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
    private var roomName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_check)

        val FOODS = arrayOf(
            "불오징어덮밥",
            "알리오올리오",
            "어묵우동",
            "사케동",
            "연어스테이크",
            "삼계탕",
            "함박스테이크",
            "육전",
            "장칼국수",
            "조개크림파스타",
            "중국식볶음우동",
            "참치아보카도김밥",
            "치킨마요덮밥",
            "카레우동",
            "칠리새우오므라이스",
            "크림카레우동",
            "타마고산도",
            "탄탄면",
            "해물라면",
            "해산물파스타",
            "고추참치볶음밥",
            "곱창볶음밥",
            "김치비빔국수",
            "꼬막비빔밥",
            "돈까스김치나베",
            "된장삼겹살덮밥",
            "떡볶이",
            "알밥",
            "감바스",
            "메밀소바",
            "명란비빔우동",
            "명란아보카도비빔밥",
            "바질새우파스타",
            "크림리조또",
            "햄버거",
            "브루스케타",
            "삼겹살덮밥",
            "삼치스테이크",
            "애호박국수",
            "야끼소바",
            "열무김치냉면",
            "채끝스테이크",
            "치즈밥",
            "토마토홍합스튜",
            "닭죽",
            "홍합탕",
            "명란크림우동",
            "경양식 돈까스",
            "김치칼국수",
            "까르보나라",
            "비빔밥",
            "돈까스덮밥",
            "투움바파스타",
            "우동",
            "순대국밥",
            "스팸마요덮밥",
            "오징어짬뽕",
            "고등어구이정식",
            "치즈베이컨그라탕",
            "해장라면",
            "냉우동",
            "라비올리",
            "콩나물밥",
            "고구마콘그라탕",
            "골뱅이비빔면",
            "김치리조또",
            "김치전",
            "달걀죽",
            "닭가슴살샐러드",
            "간장게장",
            "양념게장",
            "돈코츠라멘",
            "동치미국수",
            "크림파스타",
            "라볶이",
            "리코타치즈샐러드",
            "만두그라탕",
            "수제비",
            "피자",
            "월남쌈",
            "보쌈",
            "족발",
            "삼겹살",
            "쌀국수",
            "치킨",
            "순두부찌개",
            "탕수육",
            "쌈밥",
            "티본스테이크",
            "대창덮밥",
            "매운탕",
            "돈까스김밥",
            "돼지불백",
            "미소라멘",
            "마늘볶음밥",
            "명란파스타",
            "묵사발",
            "물냉면",
            "봉골레 파스타",
            "뚝배기불고기",
            "제육덮밥",
            "떡만두국",
            "떡국",
            "김치참치컵밥",
            "베이컨볶음밥",
            "하우스샐러드",
            "가츠동",
            "연어회덮밥",
            "감자탕",
            "오야코동",
            "오징어순대",
            "크림스프",
            "짜장면",
            "쫄면",
            "초계국수",
            "닭발",
            "카프레제샐러드",
            "토마토파스타",
            "김치볶음밥",
            "목살샐러드",
            "잔치국수",
            "치즈불닭",
            "돼지갈비",
            "육회",
            "해물파전",
            "야채곱창",
            "곱창구이",
            "초밥",
            "샤브샤브",
            "설렁탕",
            "회",
            "일식돈까스",
            "샌드위치",
            "김치찌개",
            "된장찌개",
            "닭볶음탕",
            "갈비탕",
            "갈치조림",
            "낙지볶음",
            "마라탕"
        )

        val color_adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, FOODS)
        edt_favorite.setAdapter(color_adapter)
        edt_hate.setAdapter(color_adapter)


        roomName = intent.getStringExtra("roomName")

        btn_close_preference.setOnClickListener {
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1)
        }

        var flag1:Boolean = false
        var flag2:Boolean = false

        edt_favorite.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edt_favorite.text.toString() != "") {
                    null_check_bd1.setBackgroundResource(R.drawable.yellow_bd)
                    flag1 = true
                } else if(edt_favorite.text.toString() == "") {
                    null_check_bd1.setBackgroundResource(R.drawable.gray_bd)
                    flag1 = false
                }
                if (flag1 == true && flag2 == true) {
                    btn_preference_check.isEnabled = true
                    btn_preference_check.setBackgroundResource(R.drawable.btn_yellow)
                    btn_preference_check.setTextColor(Color.parseColor("#101010"))
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
                if(edt_hate.text.toString() != ""){
                    null_check_bd2.setBackgroundResource(R.drawable.yellow_bd)
                    flag2 = true
                } else if(edt_hate.text.toString() == ""){
                    null_check_bd2.setBackgroundResource(R.drawable.gray_bd)
                    flag2 = false
                }
                if (flag1 == true && flag2 == true) {
                    btn_preference_check.isEnabled = true
                    btn_preference_check.setBackgroundResource(R.drawable.btn_yellow)
                    btn_preference_check.setTextColor(Color.parseColor("#101010"))
                }
                else {
                    btn_preference_check.isEnabled = false
                    btn_preference_check.setBackgroundResource(R.drawable.btn_gray)
                    btn_preference_check.setTextColor(Color.parseColor("#959595"))
                }
            }
        })
        if(btn_preference_check.isEnabled == true){
                btn_preference_check.setOnClickListener {
                    val intent = Intent(this, WaitingActivity::class.java)
                    intent.putExtra("like", edt_favorite.text.toString())
                    intent.putExtra("hate", edt_hate.text.toString())
                    intent.putExtra("roomName", roomName)
                    intent.putExtra("fullNum", 5)
                    startActivity(intent)
                }
        }
    }
}
