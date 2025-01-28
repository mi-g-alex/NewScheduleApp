package com.example.testschedule.widget

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.testschedule.di.AppModule
import com.example.testschedule.domain.repository.UserDatabaseRepository
import com.example.testschedule.presentation.schedule_screen.view_schedule_screen.spec_items.getLessonByDate
import java.util.Calendar
import java.util.GregorianCalendar
import javax.inject.Inject
import kotlin.math.max

class FavoriteAppWidgetViewModel
@Inject
constructor(
    private val db: UserDatabaseRepository,
    private val myPreference: AppModule.MyPreference,
) {

    private val _state = mutableStateOf(WidgetState())
    val state: State<WidgetState> = _state
    val id = myPreference.getOpenByDefault()

    val cnt = mutableIntStateOf(0)

    suspend fun getSchedule() {
        val sg = myPreference.getSelectedSubgroup()
        if (id.isNullOrBlank()) {
            _state.value = WidgetState(isError = true)
            return
        }
        var days = 0
        val schedule = db.getSchedule(id)
        val currentWeek = getCurrentWeek()
        val lastUpdate = getLastUpdateCurrentWeek()
        if (lastUpdate == 0L) {
            _state.value = _state.value.copy(isError = true)
            return
        }

        val inst = Calendar.getInstance()
        val day = GregorianCalendar(
            inst.get(Calendar.YEAR),
            inst.get(Calendar.MONTH),
            inst.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )

        schedule?.let { lsn ->
            var lessons = getLessonByDate(day, lastUpdate, currentWeek, lsn.schedules)

            val name =
                if (lsn.isGroupSchedule) lsn.studentGroupInfo?.name else lsn.employeeInfo?.getFio()

            if (lessons.lessons.isEmpty()) {
                repeat(31) {
                    if (lessons.lessons.isEmpty()) {
                        day.add(Calendar.DAY_OF_MONTH, 1)
                        lessons = getLessonByDate(day, lastUpdate, currentWeek, lsn.schedules)
                        days++
                    }
                }
            }

            if (sg != 0)
                lessons = lessons.copy(
                    lessons = lessons.lessons.filter { it.numSubgroup == 0 || it.numSubgroup == sg }
                )

            if (day.timeInMillis > max(lsn.endLessonsDate ?: 0L, lsn.endExamsDate ?: 0L))
                _state.value = WidgetState(isNoMoreLessons = true)
            else
                _state.value = WidgetState(isNoMoreLessons = false)

            _state.value = _state.value.copy(
                scheduleName = name ?: "",
                scheduleList = lessons,
                isGroup = lsn.isGroupSchedule,
                datesAfterToday = days,
                isError = false
            )
        } ?: {
            _state.value = WidgetState(isError = true)
        }

    }

    private fun getCurrentWeek() = myPreference.getCurrentWeek()
    private fun getLastUpdateCurrentWeek() = myPreference.getLastUpdateCurrentWeek()

}