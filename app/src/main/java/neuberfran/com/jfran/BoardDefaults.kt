package neuberfran.com.jfran

import android.content.Context
import android.os.Build

object BoardDefaults {
    private val DEVICE_RPI3 = "rpi3"
    private val DEVICE_IMX7D_PICO = "imx7d_pico"
    private var sBoardVariant = ""

    class BoardDefaults(public val context: Context) {

        public val res = context.resources

    }

    fun getGPIOForLED(): String {
        when (boardVariant) {
            DEVICE_RPI3 -> return "BCM4"
            DEVICE_IMX7D_PICO -> return "GPIO2_IO03"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
    }


    fun getGPIOForButton13(): String {
        when (boardVariant) {
            DEVICE_RPI3 -> return "BCM13"
            DEVICE_IMX7D_PICO -> return "GPIO2_IO03"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
    }

    fun getGPIOForButton5(): String {
        when (boardVariant) {
            DEVICE_RPI3 -> return "BCM5"
            DEVICE_IMX7D_PICO -> return "GPIO2_IO03"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
    }

    fun getGPIOForButton20(): String {
        when (boardVariant) {
            DEVICE_RPI3 -> return "BCM20"
            DEVICE_IMX7D_PICO -> return "GPIO2_IO03"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
    }

    fun getGPIOForButton21(): String {
        when (boardVariant) {
            DEVICE_RPI3 -> return "BCM21"
            DEVICE_IMX7D_PICO -> return "GPIO2_IO03"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
    }

    private val boardVariant: String
        get() {
            if (!sBoardVariant.isEmpty()) {
                return sBoardVariant
            }
            sBoardVariant = Build.DEVICE

            return sBoardVariant
        }
}