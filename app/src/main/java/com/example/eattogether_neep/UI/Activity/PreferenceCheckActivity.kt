package com.example.eattogether_neep.UI.Activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.eattogether_neep.R
import kotlinx.android.synthetic.main.activity_preference_check.*
import java.text.FieldPosition


class PreferenceCheckActivity : AppCompatActivity() {
    private var roomName = ""

    data class Food(val id:Int, val name:String)

    inner class FoodAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val allPois: List<Food>):
        ArrayAdapter<Food>(context, layoutResource, allPois),
        Filterable {
        private var mPois: List<Food> = allPois

        override fun getCount(): Int {
            return mPois.size
        }

        override fun getItem(p0: Int): Food? {
            return mPois.get(p0)

        }
        override fun getItemId(p0: Int): Long {
            // Or just return p0
            return mPois.get(p0).id.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
            view.text = "${mPois[position].name}"
            return view
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                    mPois = filterResults.values as List<Food>
                    notifyDataSetChanged()
                }

                override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                    val queryString = charSequence?.toString()?.toLowerCase()

                    val filterResults = Filter.FilterResults()
                    filterResults.values = if (queryString==null || queryString.isEmpty())
                        allPois
                    else
                        allPois.filter {
                            it.name.toLowerCase().contains(queryString) }

                    return filterResults
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_check)

        val foodArray = listOf(
            Food(1,"불오징어덮밥"), Food(2,"알리오올리오"), Food(3,"어묵우동"), Food(4,"사케동"), Food(5,"연어스테이크")
            , Food(6,"삼계탕"), Food(7,"함박스테이크"), Food(8,"육전"), Food(9,"장칼국수"), Food(10,"조개크림파스타")
            , Food(11,"중국식볶음우동"), Food(12,"참치아보카도김밥"), Food(13,"치킨마요덮밥"), Food(14,"카레우동"), Food(15,"칠리새우오므라이스")
            , Food(16,"크림카레우동"), Food(17,"타마고산도"), Food(18,"탄탄면"), Food(19,"해물라면"), Food(20,"해산물파스타")
            , Food(21,"고추참치볶음밥"), Food(22,"곱창볶음밥"), Food(23,"김치비빔국수"), Food(24,"꼬막비빔밥"), Food(25,"돈까스김치나베")
            , Food(26,"된장삼겹살덮밥"), Food(27,"떡볶이"), Food(28,"알밥"), Food(29,"감바스"), Food(30,"메밀소바")
            , Food(31,"명란비빔우동"), Food(32,"명란아보카도비빔밥"), Food(33,"바질새우파스타"), Food(34,"크림리조또"), Food(35,"햄버거")
            , Food(36,"브루스케타"), Food(37,"삼겹살덮밥"), Food(38,"삼치스테이크"), Food(39,"애호박국수"), Food(40,"야끼소바")
            , Food(41,"열무김치냉면"), Food(42,"채끝스테이크"), Food(43,"치즈밥"), Food(44,"토마토홍합스튜"), Food(45,"닭죽")
            , Food(46,"홍합탕"), Food(47,"명란크림우동"), Food(48,"경양식 돈까스"), Food(49,"김치칼국수"), Food(50,"까르보나라")
            , Food(51,"비빔밥"), Food(52,"돈까스덮밥"), Food(53,"투움바파스타"), Food(54,"우동"), Food(55,"순대국밥")
            , Food(56,"스팸마요덮밥"), Food(57,"오징어짬뽕"), Food(58,"고등어구이정식"), Food(59,"치즈베이컨그라탕"), Food(60,"해장라면")
            , Food(61,"냉우동"), Food(62,"라비올리"), Food(63,"콩나물밥"), Food(64,"고구마콘그라탕"), Food(65,"골뱅이비빔면")
            , Food(66,"김치리조또"), Food(67,"김치전"), Food(68,"달걀죽"), Food(69,"닭가슴살샐러드"), Food(70,"간장게장")
            , Food(71,"양념게장"), Food(72,"돈코츠라멘"), Food(73,"동치미국수"), Food(74,"크림파스타"), Food(75,"라볶이")
            , Food(76,"리코타치즈샐러드"), Food(77,"만두그라탕"), Food(78,"수제비"), Food(79,"피자"), Food(80,"월남쌈")
            , Food(81,"보쌈"), Food(82,"족발"), Food(83,"삼겹살"), Food(84,"쌀국수"), Food(85,"치킨")
            , Food(86,"순두부찌개"), Food(87,"탕수육"), Food(88,"쌈밥"), Food(89,"티본스테이크"), Food(90,"대창덮밥")
            , Food(91,"매운탕"), Food(92,"돈까스김밥"), Food(93,"돼지불백"), Food(94,"미소라멘"), Food(95,"마늘볶음밥")
            , Food(96,"명란파스타"), Food(97,"묵사발"), Food(98,"물냉면"), Food(99,"봉골레 파스타"), Food(100,"뚝배기불고기")
            , Food(101,"제육덮밥"), Food(102,"떡만두국"), Food(103,"떡국"), Food(104,"김치참치컵밥"), Food(105,"베이컨볶음밥")
            , Food(106,"하우스샐러드"), Food(107,"가츠동"), Food(108,"연어회덮밥"), Food(109,"감자탕"), Food(110,"오야코동")
            , Food(111,"오징어순대"), Food(112,"크림스프"), Food(113,"짜장면"), Food(114,"쫄면"), Food(115,"초계국수")
            , Food(116,"닭발"), Food(117,"카프레제샐러드"), Food(118,"토마토파스타"), Food(119,"김치볶음밥"), Food(120,"목살샐러드")
            , Food(121,"잔치국수"), Food(122,"치즈불닭"), Food(123,"돼지갈비"), Food(124,"육회"), Food(125,"해물파전")
            , Food(126,"야채곱창"), Food(127,"곱창구이"), Food(128,"초밥"), Food(129,"샤브샤브"), Food(130,"설렁탕")
            , Food(131,"회"), Food(132,"일식돈까스"), Food(133,"샌드위치"), Food(134,"김치찌개"), Food(135,"된장찌개")
            , Food(136,"닭볶음탕"), Food(137,"갈비탕"), Food(138,"갈치조림"), Food(139,"낙지볶음"), Food(140,"마라탕")
        )

        val adapter = FoodAdapter(this, android.R.layout.simple_dropdown_item_1line, foodArray)
        edt_favorite.setAdapter(adapter)
        edt_hate.setAdapter(adapter)

        edt_favorite.setOnItemClickListener() { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as Food?
            edt_favorite.setText(selectedPoi?.name)
        }
        edt_hate.setOnItemClickListener() { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as Food?
            edt_hate.setText(selectedPoi?.name)
        }


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
