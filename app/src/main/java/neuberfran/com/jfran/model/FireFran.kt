package neuberfran.com.jfran.model

class FireFran {

    companion object Factory {
        //  fun create() :FireViewModel = FireViewModel()
        var COLLECTION = "products"
        var DOCUMENT = "tutorial"
        var FIELD_userId = "userId"
        var FIELD_alarmstate  = "alarmstate"
        var FIELD_garagestate = "garagestate"
        var FIELD_gpioalarmstate  = "gpioalarmstate"
        var FIELD_gpiogaragestate = "gpiogaragestate"
        var FIELD_gcu = "gcu"
    }

    var id: String? = null
    var userId: String? = null

    var alarmstate: Boolean  = false
    var garagestate: Boolean = false
    var gcu: Boolean = false

    var gpioalarmstate: Int  = 0
    var gpiogaragestate: Int = 0

}