package neuberfran.com.jfran

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.map
import neuberfran.com.jfran.model.FireFran
import neuberfran.com.jfran.repository.FireRepository
import timber.log.Timber
import java.io.IOException
import java.util.logging.Logger

var db = FirebaseFirestore.getInstance()
var alarmState = db.collection("products").document("tutorial")
var garageState  = db.collection("products").document("tutorial")

var data1=hashMapOf("alarmstate" to true)
var data2=hashMapOf("alarmstate" to false)

var data3=hashMapOf("garagestate" to true)
var data4=hashMapOf("garagestate" to false)

class MainActivity : Activity() {

 //   private lateinit var iotestadoViewModel: FireViewModel

    private val LOG = Logger.getLogger(this.javaClass.name)
    private val TAG = MainActivity::class.java.simpleName

    var db = FirebaseFirestore.getInstance()
    var gpalarmstate = db.collection("products").document("tutorial")
    var gpgaragestate = db.collection("products").document("tutorial")


    private var gpioalarmstateb: Gpio? = null

    private var gpiogaragestateb: Gpio? = null

    private lateinit var buttonAlarm: Gpio

    private lateinit var buttonGarage: Gpio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())
        val manager = PeripheralManager.getInstance()

        var gpioalarmstateb: Gpio? = null

        try {

            gpioalarmstateb = manager.openGpio(BoardDefaults.getGPIOForButton5())
            this.gpioalarmstateb = gpioalarmstateb
            gpioalarmstateb.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            gpioalarmstateb.setActiveType(Gpio.ACTIVE_HIGH)

            gpalarmstate.addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.w(LOG.toString(), "Listen failed.", e)
                    return@addSnapshotListener
                }

                var vlrgpalarm=snapshot?.toObject(FireFran::class.java)?.gpioalarmstate

                Log.w(TAG, "Listen 94333 94333 94333." + vlrgpalarm)

                if (vlrgpalarm!!.equals(1)) {

                    gpioalarmstateb?.setValue(true)
                    Log.w(TAG, "Listen 94334 94334 94334." + vlrgpalarm)

                } else {
                    gpioalarmstateb?.setValue(false)
                    Log.w(TAG, "Listen 94335 94335 94335" + vlrgpalarm)
                }

            }

        } catch (e: IOException) {
            Timber.d(e.toString(),"Error on PeripheralIO API")
        }


        var gpiogaragestateb: Gpio? = null

        try {

            gpiogaragestateb = manager.openGpio(BoardDefaults.getGPIOForButton13())
            this.gpiogaragestateb = gpiogaragestateb
            gpiogaragestateb.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            gpiogaragestateb.setActiveType(Gpio.ACTIVE_HIGH)

            gpgaragestate.addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.w(LOG.toString(), "Listen failed.", e)
                    return@addSnapshotListener
                }

                var vlrgaragesta=snapshot?.toObject(FireFran::class.java)?.gpiogaragestate

                Log.w(TAG, "Listen 94336 94336 94336." + vlrgaragesta)

                if (vlrgaragesta!!.equals(1)) {

                    gpioalarmstateb?.setValue(true)
                    Log.w(TAG, "Listen 94337 94337 94337." + vlrgaragesta)

                } else {
                    gpioalarmstateb?.setValue(false)
                    Log.w(TAG, "Listen 94338 94338 94338" + vlrgaragesta)
                }

            }

        } catch (e: IOException) {
            Timber.d(e.toString(),"Error on PeripheralIO API")
        }

        var alarmstate: Gpio? = null

        try {

            alarmstate = manager.openGpio(BoardDefaults.getGPIOForButton20())

            Timber.d( "99 99 99 ")

            alarmstate.setDirection(Gpio.DIRECTION_IN)
            // Step 3. Enable edge trigger events.
            alarmstate.setEdgeTriggerType(Gpio.EDGE_BOTH)

            alarmstate.registerGpioCallback(alarmStateFun)

            if (alarmstate.value) {

                db.collection("products").document("tutorial")
                    .set(data2, SetOptions.merge())

            } else if (!alarmstate.value) {

                db.collection("products").document("tutorial")
                    .set(data1, SetOptions.merge())
            }

        } catch (e: IOException) {
            Timber.d(e.toString(), "Error on PeripheralIO API")
        }

        var garagestate: Gpio? = null

        try {

            garagestate = manager.openGpio(BoardDefaults.getGPIOForButton21())

            Timber.d("99 99 99 ")

            garagestate.setDirection(Gpio.DIRECTION_IN)

            garagestate.setEdgeTriggerType(Gpio.EDGE_BOTH)

            garagestate.registerGpioCallback(garageStateFun)

            if (garagestate.value) {

                db.collection("products").document("tutorial")
                    .set(data4, SetOptions.merge())

            } else if (!garagestate.value) {

                db.collection("products").document("tutorial")
                    .set(data3, SetOptions.merge())
            }

        } catch (e: IOException) {
            Timber.d(e.toString(), "Error on PeripheralIO API")
        }
    }

    var alarmStateFun = object : GpioCallback {

        override fun onGpioEdge(buttonAlarm: Gpio): Boolean {

            Thread(Runnable {

                if (buttonAlarm.value) {

                    db.collection("products").document("tutorial")
                            .set(data2, SetOptions.merge())

                    Timber.d("passei 3 passei 3 passei 3")

                } else if (!buttonAlarm.value) {

                    db.collection("products").document("tutorial").set(
                            data1, SetOptions.merge() )

                    Timber.d("passei 911 passei 911 passei 911" + buttonAlarm.value)
                }

            }).start()

            return true
        }

        override fun onGpioError(gpio: Gpio?, error: Int) = Timber.d("$gpio Error event $error")
    }

    var garageStateFun = object : GpioCallback {

        override fun onGpioEdge(buttonGarage: Gpio): Boolean {

            Thread(Runnable {

            Timber.d("GPIO changed, button 94201 94201 94201" + buttonGarage.value)

                if (buttonGarage.value) {

                    db.collection("products").document("tutorial")
                            .set(data4, SetOptions.merge())

                    Timber.d("passei 23 passei 23 passei 23")

                } else if (!buttonGarage.value) {

                    db.collection("products").document("tutorial").set(
                            data3, SetOptions.merge())

                    Timber.d("passei 921 passei 921 passei 921" + buttonGarage.value)
                }
            }).start()

            return true
        }

        override fun onGpioError(gpio: Gpio?, error: Int) = Timber.d("$gpio Error event $error")
    }

    override fun onStop() {
        super.onStop()
        gpioalarmstateb?.close()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (buttonAlarm != null) {
            buttonAlarm.unregisterGpioCallback(alarmStateFun)
            try {
                buttonAlarm.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error on PeripheralIO API", e)
            }
        }

        if (buttonGarage != null) {
            buttonGarage.unregisterGpioCallback(garageStateFun)
            try {
                buttonGarage.close()
            } catch (e: IOException) {
                Timber.d("Error on PeripheralIO API")
            }
        }

        
     }
}
