package com.indexdev.partnerin.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import com.indexdev.partnerin.data.model.response.ResponseForgotPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {
    // email check
    private val _responseForgotPassword = MutableLiveData<Resource<ResponseForgotPassword>>()
    val responseForgotPassword: LiveData<Resource<ResponseForgotPassword>> get() = _responseForgotPassword

    fun forgotPassword(email: RequestEmailCheck) {
        viewModelScope.launch {
            _responseForgotPassword.postValue(Resource.loading())
            try {
                _responseForgotPassword.postValue(Resource.success(repository.forgotPassword(email)))
            } catch (e: Exception) {
                _responseForgotPassword.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }
}