package com.indexdev.partnerin.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.response.ResponseProductByIdMitra
import com.indexdev.partnerin.data.model.response.ResponseUserMitraById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
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

    private val _responseProductByIdMitra: MutableLiveData<Resource<Response<List<ResponseProductByIdMitra>>>> =
        MutableLiveData()
    val responseProductByIdMitra: LiveData<Resource<Response<List<ResponseProductByIdMitra>>>> get() = _responseProductByIdMitra

    fun getProductByIdMitra(id: Int) {
        viewModelScope.launch {
            _responseProductByIdMitra.postValue(Resource.loading())
            try {
                _responseProductByIdMitra.postValue(
                    Resource.success(
                        repository.getProductByIdMitra(
                            id
                        )
                    )
                )
            } catch (e: Exception) {
                _responseProductByIdMitra.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }
}