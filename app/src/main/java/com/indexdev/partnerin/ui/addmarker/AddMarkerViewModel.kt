package com.indexdev.partnerin.ui.addmarker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestAddMarker
import com.indexdev.partnerin.data.model.response.ResponseAddMarker
import com.indexdev.partnerin.data.model.response.ResponseUserMitraById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMarkerViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

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

    private val _responseAddMarker: MutableLiveData<Resource<ResponseAddMarker>> = MutableLiveData()
    val responseAddMarker: LiveData<Resource<ResponseAddMarker>> get() = _responseAddMarker

    fun addMarker(requestAddMarker: RequestAddMarker) {
        viewModelScope.launch {
            _responseAddMarker.postValue(Resource.loading())
            try {
                _responseAddMarker.postValue(Resource.success(repository.addMarker(requestAddMarker)))
            } catch (e: Exception) {
                _responseAddMarker.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }
}