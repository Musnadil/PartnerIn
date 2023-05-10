package com.indexdev.partnerin.ui.editmarker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestMarker
import com.indexdev.partnerin.data.model.response.ResponseEditMarker
import com.indexdev.partnerin.data.model.response.ResponseMarkerById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMarkerViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseMarkerById: MutableLiveData<Resource<ResponseMarkerById>> =
        MutableLiveData()
    val responseMarkerById: LiveData<Resource<ResponseMarkerById>> get() = _responseMarkerById

    fun getMarkerById(id: Int) {
        viewModelScope.launch {
            _responseMarkerById.postValue(Resource.loading())
            try {
                _responseMarkerById.postValue(Resource.success(repository.getMarkerById(id)))

            } catch (e: Exception) {
                _responseMarkerById.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )

            }
        }
    }


    private val _responseEditMarker: MutableLiveData<Resource<ResponseEditMarker>> =
        MutableLiveData()
    val responseEditMarker: LiveData<Resource<ResponseEditMarker>> get() = _responseEditMarker

    fun editMarker(id: Int, requestMarker: RequestMarker) {
        viewModelScope.launch {
            _responseEditMarker.postValue(Resource.loading())
            try {
                _responseEditMarker.postValue(
                    Resource.success(
                        repository.editMarker(id, requestMarker)
                    )
                )

            } catch (e: Exception) {
                _responseEditMarker.postValue(
                    Resource.error(e.localizedMessage ?: "Error Occurred")
                )
            }
        }
    }

    private val _responseDeleteMarker: MutableLiveData<Resource<ResponseEditMarker>> =
        MutableLiveData()
    val responseDeleteMarker: LiveData<Resource<ResponseEditMarker>> get() = _responseDeleteMarker

    fun deleteMarker(id: Int) {
        viewModelScope.launch {
            _responseDeleteMarker.postValue(Resource.loading())
            try {
                _responseDeleteMarker.postValue(Resource.success(repository.deleteMarker(id)))

            } catch (e: Exception) {
                _responseDeleteMarker.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )

            }
        }
    }
}