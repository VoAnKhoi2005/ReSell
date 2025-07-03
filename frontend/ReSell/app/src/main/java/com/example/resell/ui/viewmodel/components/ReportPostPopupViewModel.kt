package com.example.resell.ui.viewmodel.components


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resell.repository.PostRepository
import com.example.resell.repository.ReportRepository
import com.example.resell.util.Event
import com.example.resell.util.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportPostViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {
    private val _reportResult = MutableStateFlow<Boolean?>(null)
    val reportResult: StateFlow<Boolean?> = _reportResult
    fun report(postId: String, reason: String, detail: String) {
        viewModelScope.launch {
            val result = reportRepository.createReportPost(postId,"$reason: $detail")
            result.fold(
                {
                    Log.e("Report",it.message?:"")
                    EventBus.sendEvent(Event.Toast("Đã có lỗi xảy ra"))
                },
                {
                }
            )
            _reportResult.value = result.isRight()
        }
    }

}
