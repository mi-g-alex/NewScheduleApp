package com.example.testschedule.presentation.account.headman

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testschedule.R
import com.example.testschedule.presentation.account.additional_elements.BasicTopBar
import com.example.testschedule.presentation.account.headman.components.create_omissions.SaveConfirmDialog
import com.example.testschedule.presentation.account.headman.components.create_omissions.SetHoursItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


@Composable
fun HeadmanScreen(
    onBackPressed: () -> Unit,
    onLogOut: () -> Unit,
    viewModel: HeadmanViewModel = hiltViewModel()
) {
    val cnt = LocalContext.current
    val errorText = stringResource(id = R.string.error_to_login)
    var enabled by remember { mutableStateOf(true) }

    val cal = Calendar.getInstance().apply {
        GregorianCalendar(
            get(Calendar.YEAR),
            get(Calendar.MONTH),
            get(Calendar.DAY_OF_MONTH),
            3, 0, 0
        )
    }

    LaunchedEffect(viewModel.errorText.value) {
        if (viewModel.errorText.value == "WrongPassword") {
            Toast.makeText(cnt, errorText, Toast.LENGTH_LONG).show()
            onLogOut()
        }
    }

    LaunchedEffect(viewModel.savedLessons.intValue) {
        if (viewModel.savedLessons.intValue == viewModel.selectedOmissions.size) {
            viewModel.getOmissionsByDate(null)
            viewModel.savedLessons.intValue = -1
        }
    }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val showSnack: (str: String) -> Unit = { str ->
        scope.launch {
            snackBarHostState.showSnackbar(
                str,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }

    val snackTextSortLesson =
        stringResource(id = R.string.account_headman_create_snack_sorted_by_lesson)
    val snackTextSortStudent =
        stringResource(id = R.string.account_headman_create_snack_sorted_by_student)
    val snackTextSaveSuccess =
        stringResource(id = R.string.account_headman_create_snack_saved_success)
    val snackTextSaveError =
        stringResource(id = R.string.account_headman_create_snack_saved_error)


    var sortByLesson by remember { mutableStateOf(true) }

    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BasicTopBar(
                onBackPressed = { onBackPressed(); enabled = false },
                title = stringResource(
                    id = R.string.account_headman_title,
                    SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(cal.timeInMillis))
                ),
                enabled = enabled,
                isOfflineResult = viewModel.isLoading.value || viewModel.errorText.value.isNotEmpty(),
            ) {
                IconButton(
                    onClick = {
                        showConfirmDialog = true
                    },
                    enabled = !viewModel.isSaving.value && viewModel.selectedOmissions.isNotEmpty()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = stringResource(id = R.string.account_headman_create_save_desc)
                    )
                }
                IconButton(
                    onClick = {
                        sortByLesson = !sortByLesson
                        showSnack(if (sortByLesson) snackTextSortLesson else snackTextSortStudent)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort),
                        contentDescription = stringResource(
                            id = if (sortByLesson) R.string.account_headman_create_sort_by_student_desc
                            else R.string.account_headman_create_sort_by_lessson_desc
                        )
                    )
                }
            }
            if (viewModel.isLoading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        SetHoursItem(it, viewModel, sortByLesson)

        if(showConfirmDialog) {
            SaveConfirmDialog(
                onConfirm = {
                    viewModel.saveOmissions(
                        onSuccess = { id ->
                            scope.launch {
                                showSnack(
                                    snackTextSaveSuccess.format(
                                        viewModel.lessonsList.first { it.id == id }.nameAbbrev,
                                        viewModel.lessonsList.first { it.id == id }.lessonTypeAbbrev
                                    )
                                )
                            }
                        },
                        onError = { id ->
                            scope.launch {
                                showSnack(
                                    snackTextSaveError.format(
                                        viewModel.lessonsList.first { it.id == id }.nameAbbrev,
                                        viewModel.lessonsList.first { it.id == id }.lessonTypeAbbrev
                                    )
                                )
                            }
                        }
                    )
                },
                onDismiss = { showConfirmDialog = false},
                lessonList = viewModel.lessonsList,
                selectedOmissions = viewModel.selectedOmissions
            )
        }
    }

}