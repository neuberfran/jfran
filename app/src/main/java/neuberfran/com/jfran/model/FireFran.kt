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

    }

    var id: String? = null
    var userId: String? = null

    var alarmstate: Boolean  = false
    var garagestate: Boolean = false

    var gpioalarmstate: Boolean  = false
    var gpiogaragestate: Boolean = false

}