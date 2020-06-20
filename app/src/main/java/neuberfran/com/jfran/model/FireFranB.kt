package neuberfran.com.jfran.model

data class FireFranB(

var id: String? = null,
var userId: String? = null,

var alarmstate: Boolean  = false,
var garagestate: Boolean = false,

var gpioalarmstate: Int?  = 0,
var gpiogaragestate: Int? = 0

)