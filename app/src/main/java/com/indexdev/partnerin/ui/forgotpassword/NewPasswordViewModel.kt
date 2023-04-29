package com.indexdev.partnerin.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestNewPassword
import com.indexdev.partnerin.data.model.response.ResponseNewPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPasswordViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseNewPassword = MutableLiveData<Resource<ResponseNewPassword>>()
    val responseNewPassword: LiveData<Resource<ResponseNewPassword>> get() = _responseNewPassword

    fun newPassword(requestNewPassword: RequestNewPassword) {
        viewModelScope.launch {
            _responseNewPassword.postValue(Resource.loading())
            try {
                _responseNewPassword.postValue(
                    Resource.success(
                        repository.newPassword(
                            requestNewPassword
                        )
                    )
                )
            } catch (e: Exception) {
                _responseNewPassword.postValue(
                    (Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    ))
                )
            }
        }
    }
}