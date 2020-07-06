package neuberfran.com.jfran.repository

import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import neuberfran.com.jfran.model.FireFran
import neuberfran.com.jfran.model.FireFranB


class FireRepository() {

    private val mFirestore: FirebaseFirestore

    private var firefransb: FireFran? = null

    private val _changeGpio = MutableLiveData<Boolean>()
    val changeGpio: LiveData<Boolean>
        get() = _changeGpio
// mudei
    init {
        _changeGpio.value = false
    }

    init {
        mFirestore = FirebaseFirestore.getInstance()
    }

    val firefrans: MutableLiveData<List<FireFran>>
        get() {
            val liveFireFrans = MutableLiveData<List<FireFran>>()

            mFirestore.collection(FireFran.COLLECTION)
                .whereEqualTo(FireFran.FIELD_userId , mFirebaseAuth!!.uid)
                .orderBy("position" , Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot , e ->
                    if (e != null) {
                        Log.w(TAG , "Listen failed." , e)
                        return@addSnapshotListener
                    }

                    val firefrans = ArrayList<FireFran>()
                    if (snapshot != null && !snapshot.isEmpty) {
                        for (documentSnapshot in snapshot.documents) {
                            val firefran = documentSnapshot.toObject(FireFran::class.java)
                            firefran!!.id = documentSnapshot.id
                            firefrans.add(firefran)
                        }
                    }
                    liveFireFrans.postValue(firefrans)
                }

            return liveFireFrans
        }

     fun getFireFranById(firefranId: String): MutableLiveData<FireFran> {
        val liveProject = MutableLiveData<FireFran>()

        val docRef = mFirestore.collection(FireFran.COLLECTION).document(firefranId)
        docRef.addSnapshotListener { snapshot , e ->
            if (e != null) {
                Log.w(TAG , "Listen failed." , e)
                return@addSnapshotListener //docRef.addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val firefran = snapshot.toObject(FireFran::class.java)
                firefran!!.id = snapshot.id
                liveProject.postValue(firefran)
            } else {
                Log.d(TAG , "Current data: null")
            }
        }

        return liveProject
    }

     fun loadBook(bookId: String): Flow<FireFran?> {
        return channelFlow {
            val subscription = mFirestore.collection(FireFran.COLLECTION)
                .document(bookId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        channel.close(e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val book = snapshot.toObject(FireFran::class.java)
                        book?.let {
                            channel.offer(it)
                        }
                    } else {
                        channel.offer(null)
                    }
                }
            awaitClose { subscription.remove() }
        }
    }

    fun changeValueGpioAlarm() {

        val tutorialDocument = mFirestore.collection(FireFran.COLLECTION)
            .document("tutorial")

        val launch = GlobalScope.launch(Dispatchers.IO) {

            val gpioAlarm =
                tutorialDocument.get().await().toObject(FireFran::class.java)?.gpioalarmstate
            withContext(Dispatchers.Main) {

                if (gpioAlarm!!.equals(1)) {
                    val data1 = hashMapOf("gpioalarmstate" to 0)
                    mFirestore.collection(FireFran.COLLECTION).document("tutorial")
                        .set(data1, SetOptions.merge())
                }
                if (gpioAlarm.equals(0)) {
                    val data2 = hashMapOf("gpioalarmstate" to 1)
                    mFirestore.collection(FireFran.COLLECTION).document("tutorial")
                        .set(data2, SetOptions.merge())
                }
            }

        }
    }

    fun changeValueGpioGarage() {

        val tutorialDocument = mFirestore.collection(FireFran.COLLECTION)
            .document("tutorial")

        GlobalScope.launch(Dispatchers.IO) {

            val gpioGarage = tutorialDocument.get().await().toObject(FireFran::class.java)?.gpiogaragestate
            withContext(Dispatchers.Main) {

                if (gpioGarage!!.equals(1)) {
                    val data3 = hashMapOf("gpiogaragestate" to 0)
                    mFirestore.collection(FireFran.COLLECTION).document("tutorial")
                        .set(data3, SetOptions.merge())
                }
                if (gpioGarage.equals(0)) {
                    val data4 = hashMapOf("gpiogaragestate" to 1)
                    mFirestore.collection(FireFran.COLLECTION).document("tutorial")
                        .set(data4, SetOptions.merge())
                }
            }

        }
    }

//    suspend fun runCoroutines(): Unit {
//        return withContext(Dispatchers.Default) {
//            kotlinx.coroutines.delay(3000)
//
//            gpioalarmstate?.let { gpioalarmstate ->
//                gpioalarmstate.value = !gpioalarmstate.value //turn on/off
//                delay(timeMillis = 3000L)
//            }
//        }
//
//    }

    // cada documento vira uma FireFran lista
    fun saveFireFran(firefran :FireFran): String {
        val document: DocumentReference
        if (firefran.id != null) {
            document = mFirestore.collection(FireFran.COLLECTION).document(firefran.id!!)
        } else {
            firefran.userId = mFirebaseAuth!!.uid
            document = mFirestore.collection(FireFran.COLLECTION).document()
        }
        document.set(firefran)

        return document.id
    }

    companion object {
        private val TAG = "FireRepository"
        private var mFirebaseAuth: FirebaseAuth? = null

        private var instance: FireRepository? = null

        @Synchronized
        fun getInstance(): FireRepository {
            if (instance == null) {
                instance = FireRepository()
                mFirebaseAuth = FirebaseAuth.getInstance()
            }
            return instance as FireRepository
        }
    }
}