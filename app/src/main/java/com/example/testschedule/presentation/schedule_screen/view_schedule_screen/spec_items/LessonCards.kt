@file:OptIn(ExperimentalLayoutApi::class)

package com.example.testschedule.presentation.schedule_screen.view_schedule_screen.spec_items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.testschedule.R
import com.example.testschedule.domain.model.schedule.ListOfEmployeesModel
import com.example.testschedule.domain.model.schedule.ListOfGroupsModel
import com.example.testschedule.domain.model.schedule.ScheduleModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun StickySchedule(lesson: LessonDay): Unit = Row(
    Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background)
        .padding(8.dp),
    Arrangement.SpaceBetween,
    Alignment.CenterVertically
) {
    val date = lesson.day.toString() +
            " " +
            stringArrayResource(id = R.array.months)[lesson.month] +
            ", " +
            stringArrayResource(id = R.array.days_of_week)[lesson.dayOfWeek - 1]
    val curWeek = stringResource(id = R.string.schedule_week, lesson.week)
    Text(
        text = date,
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = curWeek,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun StickySchedule(exam: ExamDay): Unit = Row(
    Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background)
        .padding(8.dp),
    Arrangement.SpaceBetween,
    Alignment.CenterVertically
) {
    val date = exam.day.toString() +
            " " +
            stringArrayResource(id = R.array.months)[exam.month]
    Text(
        text = date,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun LessonCard(
    lesson: ScheduleModel.WeeksSchedule.Lesson, isGroup: Boolean,
    click: () -> Unit
) {
    val color = when (lesson.lessonTypeAbbrev) {
        "ЛК" -> colorResource(id = R.color.lecture)
        "УЛк" -> colorResource(id = R.color.lecture)
        "ЛР" -> colorResource(id = R.color.labs)
        "УЛр" -> colorResource(id = R.color.labs)
        "ПЗ" -> colorResource(id = R.color.practice)
        "УПз" -> colorResource(id = R.color.practice)
        "Консультация" -> colorResource(id = R.color.consultation)
        "Экзамен" -> colorResource(id = R.color.exams)
        "Зачёт" -> colorResource(id = R.color.exams)
        "Зачет" -> colorResource(id = R.color.exams)
        else -> colorResource(id = R.color.other)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier,
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                getTimeInString(lesson.startLessonTime),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                getTimeInString(lesson.endLessonTime),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Card(
            onClick = { click() },
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(start = 8.dp),
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(
                            text = lesson.subject + if (lesson.lessonTypeAbbrev.isNotEmpty()) " (${lesson.lessonTypeAbbrev})" else "",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (lesson.auditories.isNotEmpty())
                            Text(
                                text = lesson.auditories[0],
                                style = MaterialTheme.typography.titleMedium
                            )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        var fio = ""
                        if (isGroup) {
                            if (lesson.employees?.isNotEmpty() == true) {
                                lesson.employees.sortedBy { it.lastName }.forEach {
                                    it.getFio()
                                    fio += it.getFio() + ", "
                                }
                                fio = fio.removeSuffix(", ")
                            }
                        } else {
                            if (lesson.studentGroups.isNotEmpty()) {
                                lesson.studentGroups.sortedBy { it.name }.forEach {
                                    fio += "${it.name}, "
                                }
                                fio = fio.removeSuffix(", ")
                            }
                        }
                        Text(
                            text = fio,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (lesson.numSubgroup != 0) {
                            Text(
                                text = stringResource(
                                    id = R.string.schedule_subgroup_text,
                                    lesson.numSubgroup
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (lesson.note?.isNotBlank() == true)
                        Text(
                            text = lesson.note,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(color)
            )
        }
    }
}

@Composable
fun GroupItemCard(
    selectScheduleClicked: (id: String, title: String) -> Unit,
    item: ListOfGroupsModel
) {
    Card(
        onClick = {
            selectScheduleClicked(item.name, item.name)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)

                ) {
                    Text(
                        item.course.toString(),
                        Modifier
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Column(Modifier.padding(start = 16.dp)) {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        item.facultyAbbrev + " " + item.specialityAbbrev,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeItemCard(
    selectScheduleClicked: (id: String, title: String) -> Unit,
    item: ListOfEmployeesModel
) {
    Card(
        onClick = {
            selectScheduleClicked(item.urlId, item.fio)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)

                ) {
                    Icon(
                        Icons.Outlined.Person,
                        null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )

                    if (item.photoLink != null) {
                        SubcomposeAsyncImage(
                            model = item.photoLink,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                Column(Modifier.padding(start = 16.dp)) {
                    Text(
                        item.lastName + " " + item.firstName + item.middleName.let { " $it" },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    var kaf = ""
                    item.academicDepartment.forEach {
                        kaf += "$it, "
                    }
                    kaf = kaf.removeSuffix(", ")
                    if (kaf.isNotEmpty()) {
                        Text(
                            kaf,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoreDetailCard(
    lesson: ScheduleModel.WeeksSchedule.Lesson,
    onDismissRequest: () -> Unit,
    selectScheduleClicked: (id: String, title: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(stringResource(id = R.string.close))
            }
        },
        title = {
            Text(lesson.subjectFullName)
        },
        text = {
            LazyColumn {
                item {
                    val dateStart = Date(lesson.startLessonDate ?: lesson.dateLesson ?: 0)
                    val dateEnd = Date(lesson.endLessonDate ?: lesson.dateLesson ?: 0)
                    val format = SimpleDateFormat(
                        stringResource(id = R.string.schedule_dialog_date_pattern),
                        Locale.getDefault()
                    )
                    if (lesson.lessonTypeAbbrev.isNotEmpty()) {
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_type_of_lesson,
                                lesson.lessonTypeAbbrev
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    if ((lesson.startLessonDate ?: lesson.dateLesson ?: 0) != (lesson.endLessonDate
                            ?: lesson.dateLesson ?: 0)
                    )
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_dates,
                                format.format(dateStart),
                                format.format(dateEnd)
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    else if ((lesson.startLessonDate ?: lesson.dateLesson ?: 0) != 0L) {
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_date,
                                format.format(dateStart)
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Text(
                        stringResource(
                            id = R.string.schedule_dialog_times,
                            getTimeInString(lesson.startLessonTime),
                            getTimeInString(lesson.endLessonTime),
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (lesson.weekNumber.isNotEmpty()) {
                        val weeksString =
                            lesson.weekNumber.toString().removeSuffix("]").removePrefix("[")
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_weeks,
                                weeksString,
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (lesson.auditories.isNotEmpty()) {
                        val weeksString =
                            lesson.auditories.toString().removeSuffix("]").removePrefix("[")
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_auditoriums,
                                weeksString,
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (lesson.note?.isNotEmpty() == true) {
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_note,
                                lesson.note,
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    if (lesson.numSubgroup != 0) {
                        Text(
                            stringResource(
                                id = R.string.schedule_dialog_sub_group,
                                lesson.numSubgroup,
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    if (lesson.employees?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            stringResource(
                                id = R.string.schedule_add_employees_title,
                                lesson.numSubgroup,
                            ),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lesson.employees.sortedBy { it.lastName }.forEach { employee ->
                                val fio = employee.lastName + " " +
                                        employee.firstName[0] + "." +
                                        employee.middleName?.let { " ${it[0]}." } +
                                        (employee.rank?.let { " (${it})" } ?: "")
                                AssistChip(
                                    onClick = {
                                        selectScheduleClicked(employee.urlId, fio)
                                        onDismissRequest()
                                    },
                                    label = {
                                        Text(
                                            text = employee.lastName + " " +
                                                    employee.firstName +
                                                    (employee.middleName?.let { " $it" } ?: "")
                                        )
                                    }
                                )
                            }
                        }
                    }
                    if (lesson.studentGroups.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            stringResource(
                                id = R.string.schedule_add_groups_title,
                                lesson.numSubgroup,
                            ),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lesson.studentGroups.sortedBy { it.name }.forEach { group ->
                                AssistChip(
                                    onClick = {
                                        selectScheduleClicked(group.name, group.name)
                                        onDismissRequest()
                                    },
                                    label = {
                                        Text(
                                            text = group.name
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
