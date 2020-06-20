package neuberfran.com.jfran

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.*
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import neuberfran.com.jfran.model.FireFran
import neuberfran.com.jfran.repository.FireRepository
import neuberfran.com.jfran.viewmodel.FireViewModel
import timber.log.Timber
import java.io.IOException
import java.lang.Runnable
import java.util.logging.Logger
import androidx.lifecycle.ViewModelProvider

var db = FirebaseFirestore.getInstance()
var alarmState = db.collection("products").document("tutorial")
var garageState  = db.collection("products").document("tutorial")

var data1=hashMapOf("alarmstate" to true)
var data2=hashMapOf("alarmstate" to false)

var data3=hashMapOf("garagestate" to true)
var data4=hashMapOf("garagestate" to false)

class MainActivity : Activity() {

    private lateinit var iotestadoViewModel: FireViewModel

    private var gpioalarmstate: Gpio? = null

    private val handler = Handler()

    private lateinit var gbuttonAlarm: Gpio

    private lateinit var buttonAlarm: Gpio

    private lateinit var buttonGarage: Gpio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())
        val manager = PeripheralManager.getInstance()

        var gpioalarmstate: Gpio? = null

        try {

            gpioalarmstate = manager.openGpio(BoardDefaults.getGPIOForButton5())
            this.gpioalarmstate = gpioalarmstate
            gpioalarmstate.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            gpioalarmstate.setActiveType(Gpio.ACTIVE_HIGH)

//            iotestadoViewModel = ViewModelProvider(this, FireViewModel.MainViewModelFactory(FireRepository())
//
//            ).get(FireViewModel::class.java)

            val iotestadoViewModel = ViewModelProvider(this).get(FireViewModel::class.java)

            val hotStockLiveData = iotestadoViewModel.getFireFranById("tutorial").value?.gpioalarmstate

            Log.w(TAG, "Listen 94334 94334 94334." + hotStockLiveData)

            if (hotStockLiveData!!.equals(1)) {
                try {
                    gpioalarmstate?.setValue(false)

                } catch (e: IOException) {
                    Timber.d(e.toString(), "Error on PeripheralIO API")
                }
            } else {
                try {
                    gpioalarmstate?.setValue(true)
                } catch (e: IOException) {
                    Timber.d(e.toString(), "Error on PeripheralIO API")
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

//            db.collection("products").document("tutorial")
//                    .set(data1, SetOptions.merge())
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

//            db.collection("products").document("tutorial")
//                    .set(data3, SetOptions.merge())
            return true
        }
//        override fun onGpioError(gpio: Gpio?, error: Int) = LOG.severe("$gpio Error event $error")

        override fun onGpioError(gpio: Gpio?, error: Int) = Timber.d("$gpio Error event $error")
    }

    override fun onStop() {
        super.onStop()
        gpioalarmstate?.close()
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
