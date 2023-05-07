package com.indexdev.partnerin.ui.superadminhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.response.ResponseGetAllUserPartnerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SuperAdminHomeViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {
    private val _getAllUserPartner =
        MutableLiveData<Resource<Response<List<ResponseGetAllUserPartnerItem>>>>()
    val getAllUserPartner: LiveData<Resource<Response<List<ResponseGetAllUserPartnerItem>>>> get() = _getAllUserPartner

    fun getUser() {
        viewModelScope.launch {
            _getAllUserPartner.postValue(Resource.loading())
            try {
                _getAllUserPartner.postValue(Resource.success(repository.getAllUserPartner()))
            } catch (e: Exception) {
                _getAllUserPartner.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }
}