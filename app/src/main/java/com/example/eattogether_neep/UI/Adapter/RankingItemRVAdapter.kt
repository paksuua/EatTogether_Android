package com.example.eattogether_neep.UI.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eattogether_neep.Data.RankingItem
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.Activity.MainActivity
import com.example.eattogether_neep.UI.Activity.PreferenceCheckActivity

class RankingItemRVAdapter(val ctx: Context, var dataList: ArrayList<RankingItem>): RecyclerView.Adapter<RankingItemRVAdapter.Holder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx)
            .inflate(R.layout.rv_ranking_overview, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
       if (Build.VERSION.SDK_INT >= 21) {
            holder.rank_img.setClipToOutline(true)
        }
        Glide.with(ctx).load(dataList[position].img_url).into(holder.rank_img)
        if(dataList[position].ranking == 1){
            holder.rank_num.text = dataList[position].ranking.toString()+"위"
            holder.rank_name.text = dataList[position].name
        } else if(dataList[position].ranking <= 3){
            holder.rank_crown.visibility = View.INVISIBLE
            holder.rank_num.text = dataList[position].ranking.toString()+"위"
            holder.rank_name.text = dataList[position].name
        } else {
            holder.rank_bd.setImageResource(R.drawable.ranking_else)
            holder.rank_crown.visibility = View.INVISIBLE
            holder.rank_num.text = dataList[position].ranking.toString()+"위"
            holder.rank_name.text = dataList[position].name
        }

        holder.container.setOnClickListener {
            //val intent = Intent(ctx, MainActivity::class.java)
            //ctx?.startActivity(intent)
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container = itemView.findViewById(R.id.rv_ranking_next) as LinearLayout
        var rank_crown = itemView.findViewById(R.id.rv_ranking1_crown) as ImageView
        var rank_num = itemView.findViewById(R.id.rv_ranking_num) as TextView
        var rank_bd = itemView.findViewById(R.id.rv_ranking_border) as ImageView
        var rank_img = itemView.findViewById(R.id.rv_ranking_img) as ImageView
        var rank_name = itemView.findViewById(R.id.rv_ranking_food_name) as TextView

    }
}