package com.example.testschedule.presentation.account.omissions_screen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testschedule.R
import com.example.testschedule.domain.model.account.omissions.OmissionsModel
import com.example.testschedule.presentation.account.additional_elements.BasicTopBar
import com.example.testschedule.presentation.account.additional_elements.ListDataSection
import com.example.testschedule.presentation.account.additional_elements.SectionItem
import com.example.testschedule.presentation.account.dormitory_screen.timeLongToString

@Composable
fun OmissionsScreen(
    onBackPressed: () -> Unit,
    onLogOut: () -> Unit,
    viewModel: OmissionsViewModel = hiltViewModel()
) {
    val cnt = LocalContext.current
    val errorText = stringResource(id = R.string.error_to_login)
    var enabled by remember { mutableStateOf(true) }
    LaunchedEffect(viewModel.errorText.value) {
        if (viewModel.errorText.value == "WrongPassword") {
            Toast.makeText(cnt, errorText, Toast.LENGTH_LONG).show()
            onLogOut()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BasicTopBar(
                onBackPressed = { onBackPressed(); enabled = false },
                title = stringResource(id = R.string.account_omissions_title),
                enabled = enabled,
                isOfflineResult = viewModel.isLoading.value || viewModel.errorText.value.isNotEmpty(),
            )
            if (viewModel.isLoading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    ) { padVal ->


        val omissions = viewModel.omissions.value.sortedWith(
            compareBy({ i -> i.term }, { i -> i.dateFrom })
        ).reversed()

        val mapOfTerms = mutableMapOf<String, MutableList<OmissionsModel>>()

        omissions.forEach {
            if (mapOfTerms[it.term] == null) {
                mapOfTerms[it.term] = mutableListOf()
            }
            mapOfTerms[it.term]!!.add(it)
        }

        val listOfItem = mutableListOf<SectionItem>()

        mapOfTerms.forEach { item ->
            listOfItem += SectionItem(
                title = stringResource(
                    id = R.string.account_omissions_semester_number,
                    item.key
                ),
                emptyText = "",
                itemList = item.value.map { { OmissionsItem(item = it) } }
            )
        }

        ListDataSection(
            listOfItems = listOfItem,
            paddingValues = padVal
        )
    }
}

@Composable
fun OmissionsItem(
    item: OmissionsModel
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (item.dateFrom == item.dateTo) {
                if (item.dateFrom != 0L) {
                    val text = timeLongToString(
                        item.dateFrom,
                        stringResource(id = R.string.account_omissions_date_format)
                    )
                    Text(
                        stringResource(id = R.string.account_omissions_date, text),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            } else {
                val textStart = timeLongToString(
                    item.dateFrom,
                    stringResource(id = R.string.account_omissions_date_format)
                )
                val textEnd = timeLongToString(
                    item.dateTo,
                    stringResource(id = R.string.account_omissions_date_format)
                )
                Text(
                    stringResource(id = R.string.account_omissions_dates, textStart, textEnd),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            if (item.note.isNotBlank()) {
                Text(
                    stringResource(id = R.string.account_omissions_note, item.note),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
    }
}
