package com.indexdev.partnerin.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestLogin
import com.indexdev.partnerin.data.model.response.ResponseLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    //login
    private val _responseLogin = MutableLiveData<Resource<ResponseLogin>>()
    val responseLogin: LiveData<Resource<ResponseLogin>> get() = _responseLogin

    fun login(requestLogin: RequestLogin) {
        viewModelScope.launch {
            _responseLogin.postValue(Resource.loading())
            try {
                _responseLogin.postValue(Resource.success(repository.login(requestLogin)))
            } catch (e: Exception) {
                _responseLogin.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }
}