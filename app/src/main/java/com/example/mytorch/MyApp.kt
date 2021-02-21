package com.example.mytorch

import android.app.Application

class MyApp : Application() {
    companion object {
        var isFlashOne = false
        var flashRotateValue = 0
        var MAX_CLICK_DURATION = 200
    }
}