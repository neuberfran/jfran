package neuberfran.com.jfran.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import neuberfran.com.jfran.model.FireFran
import neuberfran.com.jfran.repository.FireRepository

class FireViewModel(private val repository: FireRepository) : ViewModel() {

    private var firefran : MutableLiveData<FireFran>? = null
    private var firefrans: MutableLiveData<List<FireFran>>? = null

    fun getFireFranById(firefranId : String) :MutableLiveData<FireFran> {

        if (firefran == null) {
            firefran = FireRepository.getInstance().getFireFranById(firefranId)
        }

        return firefran as MutableLiveData<FireFran>
    }

    val changeGpio:LiveData<Boolean>
        get() = FireRepository.getInstance().changeGpio

    val allFireFrans: MutableLiveData<List<FireFran>>
        get() {
            if (firefrans == null) {
                firefrans = FireRepository.getInstance().firefrans
            }
            return firefrans as MutableLiveData<List<FireFran>>
        }

    class MainViewModelFactory(private val repository: FireRepository
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FireViewModel(repository) as T
        }
    }

    fun onChangeGpioAlarm() =
        FireRepository.getInstance().changeValueGpioAlarm()

    fun onChangeGpioGarage() =
        FireRepository.getInstance().changeValueGpioGarage()

}