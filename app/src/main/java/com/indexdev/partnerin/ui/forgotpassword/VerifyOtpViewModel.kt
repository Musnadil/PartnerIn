package com.indexdev.partnerin.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import com.indexdev.partnerin.data.model.request.RequestVerifyOtp
import com.indexdev.partnerin.data.model.response.ResponseForgotPassword
import com.indexdev.partnerin.data.model.response.ResponseVerifyOtp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseVerifyOtp = MutableLiveData<Resource<ResponseVerifyOtp>>()
    val responseVerifyOtp: LiveData<Resource<ResponseVerifyOtp>> get() = _responseVerifyOtp

    fun verifyOtp(requestVerifyOtp: RequestVerifyOtp) {
        viewModelScope.launch {
            _responseVerifyOtp.postValue(Resource.loading())
            try {
                _responseVerifyOtp.postValue(Resource.success(repository.verifyOtp(requestVerifyOtp)))
            } catch (e: Exception) {
                _responseVerifyOtp.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }

    // email check
    private val _responseResendCode = MutableLiveData<Resource<ResponseForgotPassword>>()
    val responseResendCode: LiveData<Resource<ResponseForgotPassword>> get() = _responseResendCode

    fun resendCode(email: RequestEmailCheck) {
        viewModelScope.launch {
            _responseResendCode.postValue(Resource.loading())
            try {
                _responseResendCode.postValue(Resource.success(repository.forgotPassword(email)))
            } catch (e: Exception) {
                _responseResendCode.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }

}