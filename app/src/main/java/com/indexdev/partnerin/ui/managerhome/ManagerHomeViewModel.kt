package com.indexdev.partnerin.ui.managerhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.response.ResponseGetMarker
import com.indexdev.partnerin.data.model.response.ResponseGetMarkerItem
import com.indexdev.partnerin.data.model.response.ResponseUserMitraById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ManagerHomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseGetMarker: MutableLiveData<Resource<Response<List<ResponseGetMarkerItem>>>> =
        MutableLiveData()
    val responseGetMarker: LiveData<Resource<Response<List<ResponseGetMarkerItem>>>> get() = _responseGetMarker

    fun getMarkerByIdTour(id: Int) {
        viewModelScope.launch {
            _responseGetMarker.postValue(Resource.loading())
            try {
                _responseGetMarker.postValue(Resource.success(repository.getMarkerByIdTour(id)))
            } catch (e: Exception) {
                _responseGetMarker.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }

    private val _responseUserById: MutableLiveData<Resource<ResponseUserMitraById>> =
        MutableLiveData()
    val responseUserById: LiveData<Resource<ResponseUserMitraById>> get() = _responseUserById

    fun getUserById(id: Int) {
        viewModelScope.launch {
            _responseUserById.postValue(Resource.loading())
            try {
                _responseUserById.postValue(Resource.success(repository.getUserById(id)))
            } catch (e: Exception) {
                _responseUserById.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }
}