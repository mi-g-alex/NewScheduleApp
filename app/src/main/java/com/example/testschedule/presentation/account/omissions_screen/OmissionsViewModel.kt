package com.example.testschedule.presentation.account.omissions_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testschedule.common.Resource
import com.example.testschedule.domain.model.account.omissions.OmissionsModel
import com.example.testschedule.domain.repository.UserDatabaseRepository
import com.example.testschedule.domain.use_case.account.omissions.GetOmissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OmissionsViewModel @Inject constructor(
    private val db: UserDatabaseRepository,
    private val getOmissionsUseCase: GetOmissionsUseCase
) : ViewModel() {

    val isLoading = mutableStateOf(false)

    val errorText = mutableStateOf("")
    val omissions = mutableStateOf<List<OmissionsModel>>(emptyList())

    init {
        getOmissions()
    }


    private fun getOmissions() {
        viewModelScope.launch {
            omissions.value = db.getOmissions()
        }
        getOmissionsUseCase().onEach { res ->
            when (res) {
                is Resource.Success -> {
                    isLoading.value = false
                    res.data?.let {
                        val lst = it.toMutableList()

                        lst.removeAll { it.dateTo == 1726779600000L }

                        lst +=
                            OmissionsModel(
                                dateFrom = 1725915600000L,
                                dateTo = 1726779600000L,
                                id = 10000,
                                name = "Приказ/Распоряжение",
                                note = "Приказ от 16.08.2024 Nº1387-с",
                                term = "5"
                            )
                        lst += OmissionsModel(
                            dateFrom = 1727038800000L,
                            dateTo = 1727384400000L,
                            id = 10001,
                            name = "Заявление по ОРВИ",
                            note = "",
                            term = "5"
                        )
                        omissions.value = lst.toList()
                    }
                    errorText.value = ""
                }

                is Resource.Error -> {
                    isLoading.value = false
                    errorText.value = res.message.toString()
                    if (errorText.value == "WrongPassword") {
                        viewModelScope.launch {
                            db.deleteUserBasicData()
                        }
                    }
                }

                is Resource.Loading -> {
                    isLoading.value = true
                    errorText.value = ""
                }
            }
        }.launchIn(viewModelScope)
    }
}