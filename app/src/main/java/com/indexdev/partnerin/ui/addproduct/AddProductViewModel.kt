package com.indexdev.partnerin.ui.addproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.response.ResponseAddProduct
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
class AddProductViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    // add product
    private val _responseAddProduct = MutableLiveData<Resource<ResponseAddProduct>>()
    val responseAddProduct: LiveData<Resource<ResponseAddProduct>> get() = _responseAddProduct

    fun addProduct(
        idMitra: Int,
        namaProduk: String,
        harga: String,
        satuan: String,
        deskripsi: String,
        gambar: File
    ) {
        val fileImage = reduceFileImage(gambar).asRequestBody("image/jpg".toMediaTypeOrNull())
        val productImage = MultipartBody.Part.createFormData("gambar", gambar.name, fileImage)
        val productName = namaProduk.toRequestBody("text/plain".toMediaType())
        val price = harga.toRequestBody("text/plain".toMediaType())
        val unit = satuan.toRequestBody("text/plain".toMediaType())
        val description = deskripsi.toRequestBody("text/plain".toMediaType())

        viewModelScope.launch {
            _responseAddProduct.postValue(Resource.loading())
            try {
                _responseAddProduct.postValue(
                    Resource.success(
                        repository.addProduct(
                            idMitra,
                            productName,
                            price,
                            unit,
                            description,
                            productImage
                        )
                    )
                )
            } catch (e: Exception) {
                _responseAddProduct.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }
}