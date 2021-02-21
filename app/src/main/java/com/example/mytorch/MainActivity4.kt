package com.example.mytorch


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main4.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity4 : AppCompatActivity(), SensorEventListener {
    var LAST_SELECTED_POS = -1
    private val LOG_TAG = "MAIN_ACTIVITY"
    lateinit var mSnappingRecyclerView: SnappingRecyclerView
    var prev_LAST_SELECTED_POS = -2
    //mainactivity 2 components of my torch
    lateinit var cameramanager: CameraManager
    lateinit var cameraid: String
    private var isFlashLightOn: Boolean = false
    private var isFlashLightButtonOn: Boolean = false
    var timer: Timer? = null
    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor
    var currentDegree = 0.0f
    var lastAccelerometer = FloatArray(3)
    var lastMagnetometer = FloatArray(3)
    var lastAccelerometerSet = false
    var lastMagnetometerSet = false
    var b: Int = 0
    var btry: Int = 0
    lateinit var mediaPlayer: MediaPlayer
    lateinit var switch_on_off: SwitchCompat
    val SYSTEM_ALERT_WINDOW_PERMISSION = 7
    var bubblewidgetswitch: Boolean = false
    var PREFNAME="My torch"
    lateinit var sharedPref: SharedPreferences
    var sharedPrefereStatus:Boolean=false
    var valueRetured:Boolean=false
    var compassValueReturned=false
    var paintBorder : Paint ? = null
    var shareApp=false
    var sound=false
    lateinit var defaultPrefs:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // eventbus start
        EventBus.getDefault().register(this)
        //eventbus end


        val sb = StringBuilder()
        sb.append(this.LOG_TAG)
        sb.append(" onCreate()")
        setContentView(R.layout.activity_main4)
         sharedPref = getSharedPreferences(PREFNAME, 0)
         defaultPrefs=   PreferenceManager.getDefaultSharedPreferences(this)
        compassOnOff()

    /* val defaultPrefs=   PreferenceManager.getDefaultSharedPreferences(this)
      // val needCompass= defaultPrefs.getBoolean("show_compass",false)
         valueRetured = defaultPrefs.getBoolean(PREFNAME, false)N
        if (valueRetured==true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

                RuntimePermissionForUser()
                sharedPrefereStatus=true
            }
        }
        compassValueReturned=defaultPrefs.getBoolean("show_compass",false)
        if (compassValueReturned==true){
            compass_image.visibility=View.VISIBLE
            compass_textview.visibility=View.VISIBLE
        }
        else{
            compass_image.visibility=View.GONE
            compass_textview.visibility=View.GONE
        }*/
        //bubble service start
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            RuntimePermissionForUser()
        }*/
        bttn_sttngs.setOnClickListener(View.OnClickListener {
           val intent=Intent(this,Settings2Activity::class.java)
            startActivity(intent)


        })



        //staring sound level
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_tick1)
        //start my own torch component

        switch_on_off = findViewById(R.id.switch_onn_of)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameramanager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraid = cameramanager.cameraIdList[0]
        }
        switch_on_off.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleFlashLightSwitch(isChecked)
            bubblewidgetswitch = isChecked
            // TourchEventbubble.bubble_widget_switch=isChecked

        }
        //end my own torch component
        this.mSnappingRecyclerView = findViewById(R.id.my_recycler_view) as SnappingRecyclerView
        this.mSnappingRecyclerView.enableViewScaling(false)
        this.mSnappingRecyclerView.setAdapter(SampleAdapter(getSampleList()))
        this.mSnappingRecyclerView.setOnViewSelectedListener { view, i ->
            if (this@MainActivity4.LAST_SELECTED_POS != i) {
                val findViewByPosition =
                    this@MainActivity4.mSnappingRecyclerView.getLayoutManager()!!
                        .findViewByPosition(this@MainActivity4.LAST_SELECTED_POS)
                if (findViewByPosition != null) {
                    (findViewByPosition.findViewById(R.id.item_tview) as TextView)
                        .setTextColor(ContextCompat.getColor(this@MainActivity4, R.color.text_lght))

                    (findViewByPosition.findViewById(R.id.circle_view) as View)
                        .setBackgroundResource(R.drawable.rectangle1)
                }
            }
            val textView = view.findViewById(R.id.item_tview) as TextView
            this@MainActivity4.LAST_SELECTED_POS = i
            textView.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity4,
                    R.color.green_highlight
                )
            )
            (view.findViewById(R.id.circle_view) as View).setBackgroundResource(R.drawable.rectangle2)
            //MainActivity4.this.onToggleClicked();
            //  mediaPlayer.setVolume(10f,10f)
            SoundOnOff()
           /* sound=defaultPrefs.getBoolean("sound_key",false)
            if(sound){
                mediaPlayer.start()
            }
            else{
                mediaPlayer.stop()
            }*/

            b = i % 11
            MyApp.flashRotateValue = b
            if (switch_on_off.isChecked) {
                setSwitchButtonStatus(true)
                updateFlashLightStatusWithProgress(i % 11)
            }
            /*else{
                timer?.cancel()
                setSwitchButtonStatus(false)
                turnFlashOff()
            }*/
        }
        this.mSnappingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, i: Int) {
                super.onScrollStateChanged(recyclerView, i)
                if (i == 1 && this@MainActivity4.LAST_SELECTED_POS > 0) {
                    val MainActivity4 = this@MainActivity4
                    MainActivity4.prev_LAST_SELECTED_POS = MainActivity4.LAST_SELECTED_POS
                    val findViewByPosition =
                        this@MainActivity4.mSnappingRecyclerView.getLayoutManager()!!
                            .findViewByPosition(this@MainActivity4.LAST_SELECTED_POS)

                    if (findViewByPosition != null) {
                        (findViewByPosition.findViewById(R.id.item_tview) as TextView)
                            .setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity4,
                                    R.color.text_lght
                                )
                            )
                        (findViewByPosition.findViewById(R.id.circle_view) as View)
                            .setBackgroundResource(R.drawable.rectangle1)
                    }
                }
                if (i == 0 && this@MainActivity4.prev_LAST_SELECTED_POS == this@MainActivity4.LAST_SELECTED_POS) {
                    val findViewByPosition2 =
                        this@MainActivity4.mSnappingRecyclerView.getLayoutManager()!!
                            .findViewByPosition(this@MainActivity4.LAST_SELECTED_POS)
                    if (findViewByPosition2 != null) {
                        (findViewByPosition2.findViewById(R.id.item_tview) as TextView)
                            .setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity4,
                                    R.color.green_highlight
                                )
                            )
                        (findViewByPosition2.findViewById(R.id.circle_view) as View)
                            .setBackgroundResource(R.drawable.rectangle2)

                    }
                }

            }
        })
    }

    private fun getSampleList(): List<String> {
        val arrayList = ArrayList<String>()

        for (i in 0..9) {
            val sb = StringBuilder()
            sb.append(i)
            sb.append("")
            arrayList.add(sb.toString())
        }
        arrayList.add("SOS")
        return arrayList
    }

    //start functions of my torch
    private fun toggleFlashLightSwitch(ischecked: Boolean) {
        // isFlashLightButtonOn = !isFlashLightButtonOn
        isFlashLightButtonOn = ischecked
        MyApp.isFlashOne = isFlashLightButtonOn
        Log.d("MainActivity", "toggleFlashLightSwitch: $isFlashLightButtonOn")
        setSwitchButtonStatus(isFlashLightButtonOn)
        updateFlashLightStatus()
        //toggleFlashLight()
    }

    private fun setSwitchButtonStatus(buttonStatus: Boolean) {
       // isFlashLightButtonOn = buttonStatus
        MyApp.isFlashOne=buttonStatus
        Log.d("MainActivity", "setSwithcButtonStatus: $isFlashLightButtonOn")
        if (MyApp.isFlashOne) {
            getPercentagebattery(this)
            if (btry in 81..100) {
                showBattery()
                bat_lvl_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bat_100))
            }
            if (btry in 51..80) {
                showBattery()
                bat_lvl_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bat_80))
            }
            if (btry in 11..50) {
                showBattery()
                bat_lvl_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bat_50))

            }
            if (btry <= 10) {
                showBattery()
                bat_lvl_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bat_min))

            }
            switch_on_off.isChecked=true
            led_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_led_on))
        } else {

            bat_lvl_img.visibility = View.GONE
            bat_lvl_tv.visibility = View.GONE
            led_image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_led_off))
            switch_on_off.isChecked=false
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun bubble_torch_head_close(myBroadcastReceiver: TourchEventbubble) {
        timer?.cancel()
        turnFlashOff()
        switch_on_off.isChecked = false
        //  TourchEventbubble.bubble_widget_switch=false
    }

    override fun onBackPressed() {
        //moveTaskToBack(true)
        super.onBackPressed()
       if(valueRetured==true){
           if (bubblewidgetswitch) {
               if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                   startService(Intent(this, FloatingWidgetShowService::class.java))
                   finish()
               } else if (Settings.canDrawOverlays(this)) {

                   startService(Intent(this, FloatingWidgetShowService::class.java))
                   finish()

               } else {
                   RuntimePermissionForUser()

                   Toast.makeText(
                       this, "System Alert Window Permission Is Required For Floating Widget.",
                       Toast.LENGTH_LONG
                   ).show()
               }
           }
       }
       /* if (bubblewidgetswitch) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startService(Intent(this, FloatingWidgetShowService::class.java))
                finish()
            } else if (Settings.canDrawOverlays(this)) {

                startService(Intent(this, FloatingWidgetShowService::class.java))
                finish()

            } else {
                RuntimePermissionForUser()

                Toast.makeText(
                    this, "System Alert Window Permission Is Required For Floating Widget.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/
    }


    private fun updateFlashLightStatus() {

        if (isFlashLightButtonOn) {
            updateFlashLightStatusWithProgress(b)
        } else {
            timer?.cancel()
            turnFlashOff()
        }
    }

    private fun updateFlashLightStatusWithProgress(i: Int) {
        Log.i("ActivigtyMain", "value:$i")
        if (i > 0) {
            timer?.cancel()
            timer = Timer()
            timer!!.scheduleAtFixedRate(
                0, (1000 / i).toLong()
            ) {
                if (MyApp.isFlashOne) {
                    toggleFlashLight()
                } else {
                    timer?.cancel()
                }
            }

        } else {
                //start
            if (isFlashLightButtonOn){
                turnFlashOn()
                timer?.cancel()
            }else{
                timer?.cancel()
                turnFlashOff()
            }
            //end
           /* turnFlashOn()
            timer?.cancel()*/

        }
    }

    private fun toggleFlashLight() {
        Log.d("MainActivity", "toggleFlashLight: $isFlashLightOn")
        if (isFlashLightOn) {
            turnFlashOff()
        } else {
            turnFlashOn()
        }
    }

    private fun turnFlashOn() {
        isFlashLightOn = true
        Log.d("MainActivity", "turnFlashOn")
        //  switch_on_off.background = getDrawable(R.drawable.multi_color)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            cameramanager.setTorchMode(cameraid, true)
    }

    private fun turnFlashOff() {
        isFlashLightOn = false
        Log.d("MainActivity", "turnFlashOff")
        //  switch_on_off.background = getDrawable(R.drawable.gray_round)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            cameramanager.setTorchMode(cameraid, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        // EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        //start
      //  val defaultPrefs=   PreferenceManager.getDefaultSharedPreferences(this)
        // val needCompass= defaultPrefs.getBoolean("show_compass",false)
        valueRetured = defaultPrefs.getBoolean(PREFNAME, false)
        if (valueRetured==true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

                RuntimePermissionForUser()
                sharedPrefereStatus=true
            }
        }
        compassOnOff()
       // SoundOnOff()
      /* fun compassOnOff(){
           compassValueReturned=defaultPrefs.getBoolean("show_compass",false)
           if (compassValueReturned){
               compass_image.visibility=View.VISIBLE
               compass_textview.visibility=View.VISIBLE
           }
           else{
               compass_image.visibility=View.GONE
               compass_textview.visibility=View.GONE
           }
       }*/

        //end
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
        val mContext: Context = this@MainActivity4
        mContext.stopService(
            Intent(
                mContext, FloatingWidgetShowService::class.java
            )
        )
        //var b=TourchEventbubble.cons
        setSwitchButtonStatus(MyApp.isFlashOne)
        updateFlashLightStatusWithProgress(MyApp.flashRotateValue)
        //  updateFlashLightStatusWithProgress(TourchEventbubble.cons)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)

        /*if (bubblewidgetswitch == true) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startService(Intent(this, FloatingWidgetShowService::class.java))
                    finish()
            } else if (Settings.canDrawOverlays(this)) {

                startService(Intent(this, FloatingWidgetShowService::class.java))
                     finish()

            } else {
                RuntimePermissionForUser()

                Toast.makeText(
                    this, "System Alert Window Permission Is Required For Floating Widget.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/

    }

    //sensor event listener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    var compassValue = 0
    var compassHandler: Handler? = null
    val compassRunner = Runnable {
        compassHandler = null
        compass_textview.text = " D${compassValue}°"
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor === accelerometer) {
            lowPass(event.values, lastAccelerometer)
            lastAccelerometerSet = true
        } else if (event?.sensor === magnetometer) {
            lowPass(event.values, lastMagnetometer)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                var degree = (Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360
                // var string_degree=degree.toString()
                compassValue = degree.toInt()
                if (compassHandler == null) {
                    compassHandler = Handler()
                    compassHandler?.postDelayed(compassRunner, 1000)
                }
                //  compass_textview.setText(" D$degreetoint°")


                val rotateAnimation = RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                compass_image.startAnimation(rotateAnimation)
                currentDegree = -degree
            }
        }


    }

    fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

    fun getPercentagebattery(context: Context): Int {
        val bm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getSystemService(BATTERY_SERVICE) as BatteryManager
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        btry = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return btry
    }

    fun showBattery() {
        bat_lvl_tv.text = "$btry%"
        bat_lvl_img.visibility = View.VISIBLE
        bat_lvl_tv.visibility = View.VISIBLE
    }

    fun RuntimePermissionForUser() {

        val permissionIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package: $packageName")
        )

        startActivityForResult(permissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }
    fun compassOnOff(){
        compassValueReturned=defaultPrefs.getBoolean("show_compass",false)
        if (compassValueReturned){
            compass_image.visibility=View.VISIBLE
            compass_textview.visibility=View.VISIBLE
        }
        else{
            compass_image.visibility=View.GONE
            compass_textview.visibility=View.GONE
        }
    }
    fun SoundOnOff(){
        sound=defaultPrefs.getBoolean("sound_key",false)
        if(sound) mediaPlayer.start()
       /* else{
            mediaPlayer.stop()
        }*/
    }

}

class TourchEventbubble {
    val i = 8

}
