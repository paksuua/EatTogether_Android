package com.example.eattogether_neep.UI

import android.content.Context
import android.provider.Settings
fun String.makeUUID(context: Context?):String {
    return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)

}