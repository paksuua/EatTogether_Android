package com.example.eattogether_neep.Data

data class FoodItem(
   val data: ArrayList<MenuItem>
)

data class MenuItem(
    val name: String,
    val image:String
)