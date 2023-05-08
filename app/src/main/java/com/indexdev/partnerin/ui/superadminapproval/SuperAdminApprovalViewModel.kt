package com.indexdev.partnerin.ui.superadminapproval

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.request.RequestEditAccount
import com.indexdev.partnerin.data.model.response.ResponseEditAccount
import com.indexdev.partnerin.data.model.response.ResponseUserMitraById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuperAdminApprovalViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {
    private val _responseApproval: MutableLiveData<Resource<ResponseEditAccount>> =
        MutableLiveData()
    val responseApproval: LiveData<Resource<ResponseEditAccount>> get() = _responseApproval

    fun approvePartner(id: Int, requestEditAccount: RequestEditAccount) {
        viewModelScope.launch {
            _responseApproval.postValue(Resource.loading())
            try {
                _responseApproval.postValue(
                    Resource.success(
                        repository.editAccount(
                            id = id,
                            requestEditAccount
                        )
                    )
                )
            } catch (e: Exception) {
                _responseApproval.postValue(Resource.error(e.localizedMessage ?: "Error Occurred"))
            }
        }
    }

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
}