package com.indexdev.partnerin.ui.editaccount

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
class EditAccountViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseEditAccount: MutableLiveData<Resource<ResponseEditAccount>> =
        MutableLiveData()
    val responseEditAccount: LiveData<Resource<ResponseEditAccount>> get() = _responseEditAccount

    fun editAccount(
        id: Int,
        requestEditAccount: RequestEditAccount
    ) {
        viewModelScope.launch {
            _responseEditAccount.postValue(Resource.loading())
            try {
                _responseEditAccount.postValue(
                    Resource.success(
                        repository.editAccount(
                            id = id,
                            requestEditAccount
                        )
                    )
                )
            } catch (e: Exception) {
                _responseEditAccount.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
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