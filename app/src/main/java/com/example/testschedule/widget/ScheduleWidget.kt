package com.example.testschedule.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString
import androidx.core.content.ContextCompat.startActivity
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.testschedule.R
import com.example.testschedule.alarm_manager.DefaultAlarmScheduler
import com.example.testschedule.domain.model.schedule.ScheduleModel
import com.example.testschedule.domain.repository.UserDatabaseRepository
import com.example.testschedule.presentation.MainActivity
import com.example.testschedule.presentation.schedule_screen.view_schedule_screen.spec_items.getTimeInString
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import javax.inject.Inject

class ScheduleWidget : GlanceAppWidget() {

    @Inject
    lateinit var db: UserDatabaseRepository

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("AlarmManager", "WidgetUpdated")
        DefaultAlarmScheduler(context).schedule()
        val viewModel =
            EntryPoints
                .get(
                    context,
                    FavoriteAppWidgetEntryPoint::class.java,
                ).getViewModel()

        viewModel.getSchedule()


        provideContent {
            GlanceTheme {
                var cnt by remember { viewModel.cnt }

                LaunchedEffect(cnt) {
                    viewModel.getSchedule()
                }

                val state by remember { viewModel.state }
                MyContent(state, viewModel.id, context) {
                    cnt++
                }
            }
        }
    }


    @Composable
    fun MyContent(data: WidgetState, id: String?, context: Context, onUpdate: () -> Unit) {

        val cal = GregorianCalendar()
        cal.timeInMillis = data.scheduleList?.date ?: 0L

        Scaffold(
            titleBar = {
                TitleBar(
                    ImageProvider(R.drawable.ic_for_widget),
                    (data.scheduleName + (data.scheduleList?.date?.let {
                        val date = when (data.datesAfterToday) {
                            0 -> context.resources.getString(R.string.widget_today)
                            1 -> context.resources.getString(R.string.widget_tomorrow)
                            else -> SimpleDateFormat(
                                "dd.MM",
                                Locale.getDefault()
                            ).format(Date(data.scheduleList.date))
                        }
                        " | $date (${
                            context.resources.getString(
                                R.string.schedule_week,
                                data.scheduleList.week
                            )
                        })"
                    } ?: "")),
                    actions = {
                        Row(GlanceModifier.padding(12.dp)) {
                            Image(
                                ImageProvider(R.drawable.baseline_sync_24),
                                null,
                                modifier = GlanceModifier.size(24.dp).clickable(
                                    rippleOverride = R.drawable.widget_button,
                                ) { onUpdate() }
                                    .background(
                                        ImageProvider(R.drawable.widget_card_background),
                                        colorFilter = ColorFilter.tint(GlanceTheme.colors.widgetBackground)
                                    ),
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                            )
                        }
                    })
            },
            modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
        ) {
            if (data.isError) {
                Text(
                    context.resources.getString(R.string.widget_error),
                    style = TextStyle(
                        color = GlanceTheme.colors.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.fillMaxSize()
                )
            } else if (data.isNoMoreLessons) {
                Text(
                    context.resources.getString(R.string.schedule_no_lessons),
                    style = TextStyle(
                        color = GlanceTheme.colors.onBackground,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.fillMaxSize()
                )
            } else {
                LazyColumn {
                    if (data.scheduleList?.lessons?.isEmpty() == true) {
                        item {
                            Text(
                                ":(",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onBackground,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = GlanceModifier.fillMaxSize()
                            )
                        }
                    }
                    data.scheduleList?.lessons?.forEach {
                        item {
                            if (!it.announcement)
                                LessonCard(it, data.isGroup, context)
                            else
                                LessonCard(
                                    it.copy(
                                        subject = context.resources.getString(R.string.schedule_announcement)
                                    ),
                                    data.isGroup,
                                    context
                                )
                        }
                    }
                    item {
                        Text(
                            context.resources.getString(
                                R.string.widget_updated_at,
                                SimpleDateFormat(
                                    "dd.MM hh:mm:ss",
                                    Locale.getDefault()
                                ).format(Calendar.getInstance().time)
                            ),
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = GlanceTheme.colors.secondaryContainer,
                                textAlign = TextAlign.End
                            ),
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    fun interface FavoriteAppWidgetEntryPoint {
        fun getViewModel(): FavoriteAppWidgetViewModel
    }

    @Composable
    fun LessonCard(
        lesson: ScheduleModel.WeeksSchedule.Lesson,
        isGroup: Boolean,
        context: Context
    ) {

        val color = when (lesson.lessonTypeAbbrev) {
            "ЛК", "УЛк" -> ColorProvider(R.color.lecture)
            "ЛР", "УЛр" -> ColorProvider(R.color.labs)
            "ПЗ", "УПз" -> ColorProvider(R.color.practice)
            "Консультация" -> ColorProvider(R.color.consultation)
            "Экзамен", "Зачёт", "Зачет" -> ColorProvider(R.color.exams)
            else -> ColorProvider(R.color.other)
        }

        val monoTimeStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            color = GlanceTheme.colors.onBackground
        )

        val topTextStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = GlanceTheme.colors.onBackground
        )

        val bottomTextStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = GlanceTheme.colors.secondary
        )

        val noteTextStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = GlanceTheme.colors.secondary
        )

        val howName =
            if (!isGroup) lesson.studentGroups.firstOrNull()?.name ?: ""
            else lesson.employees?.firstOrNull()?.getFio() ?: ""

        val where = (lesson.auditories.fastJoinToString(", "))

        val startTime = getTimeInString(lesson.startLessonTime)
        val endTime = getTimeInString(lesson.endLessonTime)

        val dateElement = @Composable {
            Column(horizontalAlignment = Alignment.Horizontal.End) {
                Text(startTime, style = monoTimeStyle.copy(fontSize = 16.sp))
                Text(
                    endTime,
                    style = monoTimeStyle.copy(
                        fontSize = 14.sp,
                        color = GlanceTheme.colors.secondary
                    )
                )
            }
        }

        val subjectNameBox = @Composable {
            Box(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    lesson.subject + if (lesson.lessonTypeAbbrev.isNotEmpty()) " (${lesson.lessonTypeAbbrev})" else "",
                    style = topTextStyle
                )
            }
        }

        val auditoryBox = @Composable {
            Box(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(where, style = topTextStyle)
            }
        }

        val whoBox = @Composable {
            Box(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(howName, style = bottomTextStyle)
            }
        }

        val subgroupBox = @Composable {
            Box(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (lesson.numSubgroup != 0) Text(
                    context.resources.getString(
                        R.string.schedule_subgroup_text,
                        lesson.numSubgroup
                    ),
                    style = bottomTextStyle
                )
            }
        }

        val noteText = @Composable {
            Box(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (lesson.note?.isNotBlank() == true) {
                    Text(lesson.note, maxLines = 1, style = noteTextStyle)
                }
            }
        }

        Box(GlanceModifier.wrapContentHeight().padding(vertical = 4.dp)) {
            Row {
                Box(
                    GlanceModifier.wrapContentWidth().fillMaxHeight().padding(end = 10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    dateElement()
                }
                Column(
                    GlanceModifier.fillMaxWidth().wrapContentHeight()
                        .background(
                            ImageProvider(R.drawable.widget_card_background),
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.secondaryContainer)
                        ),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Column(GlanceModifier.padding(6.dp)) {
                        Box(GlanceModifier.fillMaxWidth().wrapContentHeight()) {
                            subjectNameBox()
                            auditoryBox()
                        }
                        Box(GlanceModifier.fillMaxWidth().wrapContentHeight()) {
                            whoBox()
                            subgroupBox()
                        }
                        noteText()
                    }
                    Box(
                        GlanceModifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(
                                ImageProvider(R.drawable.widget_indentificator_background),
                                colorFilter = ColorFilter.tint(color)
                            )
                    ) {}
                }
            }
        }
    }
}