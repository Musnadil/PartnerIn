package com.indexdev.partnerin.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import com.indexdev.partnerin.data.model.response.ResponseEmailCheck
import com.indexdev.partnerin.data.model.response.ResponseRegister
import com.indexdev.partnerin.ui.reduceFileImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    // email check
    private val _responseEmailCheck = MutableLiveData<Resource<ResponseEmailCheck>>()
    val responseEmailCheck: LiveData<Resource<ResponseEmailCheck>> get() = _responseEmailCheck

    fun emailCheck(email: RequestEmailCheck) {
        viewModelScope.launch {
            _responseEmailCheck.postValue(Resource.loading())
            try {
                _responseEmailCheck.postValue(Resource.success(repository.emailCheck(email)))
            } catch (e: Exception) {
                _responseEmailCheck.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }

    //register
    private val _responseRegister = MutableLiveData<Resource<ResponseRegister>>()
    val responseRegister: LiveData<Resource<ResponseRegister>> get() = _responseRegister

    fun register(
        kodeWisata: Int,
        namaPemilik: String,
        namaUsaha: String,
        emailPemilik: String,
        noPonsel: String,
        jenisUsaha: String,
        gambar: File,
        password: String,
        alamat: String,
        hariBuka: String,
        jamBuka: String,
        jamTutup: String,
        lat: Float,
        longi: Float,
        role: Int,
        status: String,
    ) {
        val fileImage = reduceFileImage(gambar).asRequestBody("image/jpg".toMediaTypeOrNull())
        val productImage = MultipartBody.Part.createFormData("gambar", gambar.name, fileImage)
        val owner = namaPemilik.toRequestBody("text/plain".toMediaType())
        val businessName = namaUsaha.toRequestBody("text/plain".toMediaType())
        val emailOwner = emailPemilik.toRequestBody("text/plain".toMediaType())
        val numberPhone = noPonsel.toRequestBody("text/plain".toMediaType())
        val businessType = jenisUsaha.toRequestBody("text/plain".toMediaType())
        val pass = password.toRequestBody("text/plain".toMediaType())
        val address = alamat.toRequestBody("text/plain".toMediaType())
        val dayOpen = hariBuka.toRequestBody("text/plain".toMediaType())
        val openingHours = jamBuka.toRequestBody("text/plain".toMediaType())
        val closingHours = jamTutup.toRequestBody("text/plain".toMediaType())
        val statuses = status.toRequestBody("text/plain".toMediaType())

        viewModelScope.launch {
            _responseRegister.postValue(Resource.loading())
            try {
                _responseRegister.postValue(
                    Resource.success(
                        repository.register(
                            kodeWisata,
                            owner,
                            businessName,
                            emailOwner,
                            numberPhone,
                            businessType,
                            productImage,
                            pass,
                            address,
                            dayOpen,
                            openingHours,
                            closingHours,
                            lat,
                            longi,
                            role,
                            statuses
                        )
                    )
                )
            } catch (e: Exception) {
                _responseRegister.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }
}