package com.example.mytorch

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import android.view.Gravity
import android.graphics.PixelFormat
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.MotionEvent
import android.widget.ImageView
import org.greenrobot.eventbus.EventBus
import java.util.*

class FloatingWidgetShowService: Service() {
    lateinit var windowManager: WindowManager
     lateinit var floatingView: View
    lateinit var collapsedView:View
    lateinit var params: WindowManager.LayoutParams
    var clicked:Boolean=false
     var startClickTime:Long=0


    override fun onBind(intent: Intent): IBinder? {
        return null
    }override fun onCreate() {
        super.onCreate()
        /*EventBus.getDefault().register(this)*/
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)
        collapsedView = floatingView.findViewById(R.id.Layout_Collapsed)

        floatingView.findViewById<ImageView>(R.id.Widget_Close_Icon)
            .setOnClickListener(View.OnClickListener { stopSelf() })
        floatingView.findViewById<View>(R.id.MainParentRelativeLayout)
            .setOnTouchListener(object : View.OnTouchListener {
                 var X_Axis: Int = 0
                 var Y_Axis: Int = 0
                var TouchX: Float = 0.toFloat()
                var TouchY: Float = 0.toFloat()
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            X_Axis = params.x
                            Y_Axis = params.y
                            TouchX = event.rawX
                            TouchY = event.rawY
                            startClickTime= Calendar.getInstance().timeInMillis
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            //code start
                            Log.d("BubbleWidgetService","Action Up Event Occur")
                         var clickDuration=Calendar.getInstance().timeInMillis - startClickTime
                            if(clickDuration < MyApp.MAX_CLICK_DURATION){
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                                    EventBus.getDefault().post(TourchEventbubble())
                                   // windowManager.removeView(floatingView)
                                    stopSelf()

                                    //  floatingView.findViewById<View>(R.id.Logo_Icon).visibility=View.GONE

                                }
                            }
                           /* floatingView.findViewById<View>(R.id.MainParentRelativeLayout).setOnClickListener {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                                    EventBus.getDefault().post(TourchEventbubble())


                                }
                            }*/
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = X_Axis + (event.rawX - TouchX).toInt()
                            params.y = Y_Axis + (event.rawY - TouchY).toInt()
                            windowManager.updateViewLayout(floatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (floatingView != null) windowManager.removeView(floatingView)
    }

}