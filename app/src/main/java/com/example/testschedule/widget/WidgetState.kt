package com.example.testschedule.widget

import com.example.testschedule.presentation.schedule_screen.view_schedule_screen.spec_items.LessonDay

data class WidgetState(
    val scheduleName: String = "",
    val scheduleList: LessonDay? = null,
    val isGroup: Boolean = true,
    val isNoMoreLessons: Boolean = true,
    val datesAfterToday: Int = 0,
    val isError : Boolean = false
)