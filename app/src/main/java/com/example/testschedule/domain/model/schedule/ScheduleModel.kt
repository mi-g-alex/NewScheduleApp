package com.example.testschedule.domain.model.schedule

import com.example.testschedule.data.remote.dto.schedule_view.ScheduleDto
import com.example.testschedule.domain.model.schedule.ScheduleModel.EmployeeInfo
import com.example.testschedule.domain.model.schedule.ScheduleModel.StudentGroupInfo
import com.example.testschedule.domain.model.schedule.ScheduleModel.WeeksSchedule
import com.example.testschedule.domain.model.schedule.ScheduleModel.WeeksSchedule.Lesson
import com.example.testschedule.domain.model.schedule.ScheduleModel.WeeksSchedule.Lesson.StudentGroupsInfo
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ### ScheduleModel - базовый класс расписания, с основной информацией
 * * __id__ (_i-abramov/210101_) - ID группы (номер) или urlId преподавателя
 * * __title__ (_Абрамов И. И. / 210101_) - какой будет Title отображаться в расписании
 * * __isGroupSchedule__ - True если расписание относится к группе
 * * __startLessonsDate__ - Дата начала занятий в формате Long
 * * __endLessonsDate__ - Дата окончания занятий в формате Long
 * * __startExamsDate__ - Дата начала сессии в формате Long
 * * __endExamsDate__ - Дата конца сессии в формате Long
 * * __employeeInfo__ - [EmployeeInfo] если расписание преподавателя, иначе NULL
 * * __studentGroupInfo__ - [StudentGroupInfo] если расписание группы, иначе NULL
 * * __schedules__  список [WeeksSchedule] из недель, где в каждой неделе есть список дней, а в каждом дне список занятий
 * * __exams__ - список [WeeksSchedule.Lesson] храним экзаменов или NULL если их нет
 */
data class ScheduleModel(
    val id: String, // Тут ID как номер группы или urlId
    val title: String, // Что будет в шапке
    val isGroupSchedule: Boolean, // Группа да или нет
    val startLessonsDate: Long?, // Дата начала занятий в этой группе в формате dd.MM.yyyy
    val endLessonsDate: Long?, // Дата окончания занятий в этой группе
    val startExamsDate: Long?, // Дата начала экзаменов в этой группе
    val endExamsDate: Long?, // Дата окончания экзаменов в этой группе
    val employeeInfo: EmployeeInfo?, // Информация о преподавателе, если это его расписание
    val studentGroupInfo: StudentGroupInfo?, // Информация о группе, если это её расписание
    val schedules: List<WeeksSchedule>, // Тут храним список из недель, где в каждой неделе есть список дней, а в каждом дне список занятий
    val exams: List<Lesson>? // Тут список храним экзаменов
) {
    /**
     * ### EmployeeInfo - информация о преподавтеле в расписании
     * * __firstName__ (_Игорь_) - имя
     * * __lastName__ (_Абрамов_) - фамилия
     * * __middleName__ (_ Иванович_) - отчество (может быть NULL потому что да)
     * * __photoLink__ (_https://iis.bsuir.by/api/v1/employees/photo/500434_) - ссылка на фотку. Вообще генерится как `ссылка + id`, поэтому __по ссылке фотки может и не быть)__
     * * __degree__ (_кандидат технических наук и т.п._) - степень
     * * __degreeAbbrev__ (__)
     * * __rank__ (_профессор_) - ранг преподавателя
     * * __urlId__ (_i-abramov_) - string-id
     */
    data class EmployeeInfo(
        val id: Int, // ID преподавателя
        val firstName: String, // Сергей
        val middleName: String?, // Николаевич
        val lastName: String, // Нестеренков
        val photoLink: String?, // https://iis.bsuir.by/api/v1/employees/photo/501822
        val degree: String?, // кандидат технических наук и т.п.
        val degreeAbbrev: String?, // кратко? к.т.н.
        val rank: String?, // доцент и т.п.h
        val urlId: String, // s-nesterenkov
    ) {
        fun getFio(): String =
            "$lastName ${firstName[0]}." + if (middleName?.isNotBlank() == true) " ${middleName[0]}." else ""
    }

    /**
     * ### ListOfGroupsModel - информация о группе в расписании
     * * __name__ (_210101_) - номер гурппы
     * * __facultyAbbrev__ (_ФКП_) - факультет, к которому относится группа
     * * __specialityName__ (_Информационные системы..._) - полное название специальности
     * * __specialityAbbrev__ (_ИСиТ(в ОПБ)_) - аббревиатура специальности
     * * __course__ (_2_) - номер курса (может быть 0, тогда группу не отображаем
     */
    data class StudentGroupInfo(
        val name: String, // 253501
        val facultyAbbrev: String, // ФКСиС
        val specialityName: String, // Информатика и технологии программирования
        val specialityAbbrev: String, // ИиТП
        val course: Int // 1
    )

    /**
     * ### WeeksSchedule - хранятся 6 дней недели (пн-сб), внутри которых список предометов [Lesson]
     */
    data class WeeksSchedule( // Список предметов на этой неделе в определенный день
        val monday: List<Lesson>,
        val tuesday: List<Lesson>,
        val wednesday: List<Lesson>,
        val thursday: List<Lesson>,
        val friday: List<Lesson>,
        val saturday: List<Lesson>
    ) {

        /**
         * ### Lesson - вся информация о конкретной паре
         * * __auditories__ (_["4-4к."]_) - список кабинетов, в котором проходит занятие (зачем список вопрос хороший)
         * * __startLessonTime__ (_635_) - Время начала пары в формате `HH * 60 + MM`
         * * __endLessonTime__ (_715_) -  Время конца пары в формате `HH * 60 + MM`
         * * __startLessonDate__ - Дата, когда пройдет первая пара в формате LONG
         * * __endLessonDate__ - Дата, когда планируется последняя пара в формете LONG
         * * __dateLesson__ - Дата события или экзамена (2 поля выше тогда NULL)
         * * __lessonTypeAbbrev__ (_ЛК, ЛР, ПЗ, Консультация, Экзамен, Зачет, УЛк, УПз, УЛр_) - какого типа пара из перечесленно (мб еще что-то есть)
         * * __note__ (_"21.12 пары не будет"_) - примечание к паре
         * * __numSubgroup__ (_0, 1, 2_) - 0 - общая пара, 1 и 2 по подгруппам соответственно
         * * __studentGroups__ (_[[StudentGroupsInfo], ]_) - краткая информация о группе, у преподавателя в расписании может быть больше чем 1 группа в списке. В расписании группы только сама группа.
         * * __subject__ (_МА_) - краткое название предмета
         * * __subjectFullName__ (_Математический анализ_) - полное название предмета
         * * __weekNumber__ (_[1, 2, 3, 4]_) - список недель, по которых приходит пара
         * * __employees__ (_[[EmployeeInfo], ]_) - список преподавателей, которые ведут предмет
         * * __announcement__ (_True/False_) - событие выпало
         * * __split__ - _А ВОТ НЕ ЗНАЮ ЧТО ЭТО ТАКОЕ но пусть будет, вдруг найду_
         */
        data class Lesson(
            val auditories: List<String>, // Список кабинетов
            val startLessonTime: Int, // Время начала урока / экзамена
            val endLessonTime: Int,  // Время конеца урока / экзамена
            val startLessonDate: Long?, // Дата начада занятий по предмета
            val endLessonDate: Long?, // Дата окончания занятий по предменту
            val dateLesson: Long?, // Дата экзамена
            val lessonTypeAbbrev: String, // ЛК, ЛР, ПЗ, Консультация, Экзамен
            val note: String?, // Пометка
            val numSubgroup: Int, // Номер подгруппы: 0, 1, 2
            val studentGroups: List<StudentGroupsInfo>, // Список групп
            val subject: String, // Название предмета: МА
            val subjectFullName: String, // Полное название: Математический анализ
            val weekNumber: List<Int>, // номера недель
            val employees: List<EmployeeInfo>?, // Список преподавателей
            val announcement: Boolean, // событие или нет
            val split: Boolean // хз что это
        ) {
            data class StudentGroupsInfo(
                val specialityName: String, // Информатика и Технологии Программирования
                val numberOfStudents: Int, // 28
                val name: String, // 253501
                val educationDegree: Int, // 1
            )
        }
    }

}

/**
 * Конвертирует из DTO расписание в мою [ScheduleModel]
 */
fun scheduleFromDtoToModel(schedule: ScheduleDto): ScheduleModel {
    val isGroupSchedule = schedule.studentGroupDto != null && schedule.employeeDto == null

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    var startLessonsDate =
        if (schedule.startDate.isNullOrBlank()) null else dateFormat.parse(schedule.startDate)?.time
    var endLessonsDate =
        if (schedule.endDate.isNullOrBlank()) null else dateFormat.parse(schedule.endDate)?.time
    val startExamsDate =
        if (schedule.startExamsDate.isNullOrBlank()) null else dateFormat.parse(schedule.startExamsDate)?.time
    val endExamsDate =
        if (schedule.endExamsDate.isNullOrBlank()) null else dateFormat.parse(schedule.endExamsDate)?.time

    val employeeTempInfo = schedule.employeeDto
    val employeeInfo = if (employeeTempInfo != null) {
        EmployeeInfo(
            id = employeeTempInfo.id,
            firstName = employeeTempInfo.firstName,
            middleName = employeeTempInfo.middleName,
            lastName = employeeTempInfo.lastName,
            photoLink = employeeTempInfo.photoLink,
            degree = employeeTempInfo.degree,
            degreeAbbrev = employeeTempInfo.degreeAbbrev,
            rank = employeeTempInfo.rank,
            urlId = employeeTempInfo.urlId
        )
    } else null

    val groupTempInfo = schedule.studentGroupDto
    val groupInfo = if (groupTempInfo != null) {
        StudentGroupInfo(
            name = groupTempInfo.name,
            facultyAbbrev = groupTempInfo.facultyAbbrev,
            specialityAbbrev = groupTempInfo.specialityAbbrev,
            specialityName = groupTempInfo.specialityName,
            course = groupTempInfo.course
        )
    } else null

    data class Week(
        val monday: MutableList<Lesson>,
        val tuesday: MutableList<Lesson>,
        val wednesday: MutableList<Lesson>,
        val thursday: MutableList<Lesson>,
        val friday: MutableList<Lesson>,
        val saturday: MutableList<Lesson>
    ) {
        fun toWeekList() = WeeksSchedule(
            this.monday.toList(),
            this.tuesday.toList(),
            this.wednesday.toList(),
            this.thursday.toList(),
            this.friday.toList(),
            this.saturday.toList()
        )
    }

    val week1 = Week(
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf()
    )
    val week2 = Week(
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf()
    )
    val week3 = Week(
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf()
    )
    val week4 = Week(
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf()
    )

    val listOfWeeks = listOf(week1, week2, week3, week4)

    var curSchedule = schedule.schedules
    val prevSchedule = schedule.previousSchedules

    if (curSchedule != null) {
        var m = curSchedule.monday?.toMutableList() ?: mutableListOf()
        prevSchedule?.monday?.let { m += it }
        curSchedule = curSchedule.copy(monday = m)

        m = curSchedule.tuesday?.toMutableList() ?: mutableListOf()
        prevSchedule?.tuesday?.let { m += it }
        curSchedule = curSchedule.copy(tuesday = m)

        m = curSchedule.wednesday?.toMutableList() ?: mutableListOf()
        prevSchedule?.wednesday?.let { m += it }
        curSchedule = curSchedule.copy(wednesday = m)

        m = curSchedule.thursday?.toMutableList() ?: mutableListOf()
        prevSchedule?.thursday?.let { m += it }
        curSchedule = curSchedule.copy(thursday = m)

        m = curSchedule.friday?.toMutableList() ?: mutableListOf()
        prevSchedule?.friday?.let { m += it }
        curSchedule = curSchedule.copy(friday = m)

        m = curSchedule.saturday?.toMutableList() ?: mutableListOf()
        prevSchedule?.saturday?.let { m += it }
        curSchedule = curSchedule.copy(saturday = m)
    } else {
        curSchedule = prevSchedule
    }

    curSchedule?.monday?.forEach { day ->
        val tmp = convertDay(day)
        if (day.weekNumber != null) {
            day.weekNumber.forEach { week ->
                listOfWeeks[week - 1].monday.add(tmp)
            }
        } else {
            listOfWeeks[0].monday.add(tmp)
            listOfWeeks[1].monday.add(tmp)
            listOfWeeks[2].monday.add(tmp)
            listOfWeeks[3].monday.add(tmp)
        }
    }

    curSchedule?.tuesday?.forEach { day ->
        val tmp = convertDay(day)
        if (day.weekNumber != null) {
            day.weekNumber.forEach { week ->
                listOfWeeks[week - 1].tuesday.add(tmp)
            }
        } else {
            listOfWeeks[0].tuesday.add(tmp)
            listOfWeeks[1].tuesday.add(tmp)
            listOfWeeks[2].tuesday.add(tmp)
            listOfWeeks[3].tuesday.add(tmp)
        }
    }

    curSchedule?.wednesday?.forEach { day ->
        val tmp = convertDay(day)
        if (day.weekNumber != null) {
            day.weekNumber.forEach { week ->
                listOfWeeks[week - 1].wednesday.add(tmp)
            }
        } else {
            listOfWeeks[0].wednesday.add(tmp)
            listOfWeeks[1].wednesday.add(tmp)
            listOfWeeks[2].wednesday.add(tmp)
            listOfWeeks[3].wednesday.add(tmp)
        }
    }

    curSchedule?.thursday?.forEach { day ->
        val tmp = convertDay(day)
        if (day.weekNumber != null) {
            day.weekNumber.forEach { week ->
                listOfWeeks[week - 1].thursday.add(tmp)
            }
        } else {
            listOfWeeks[0].thursday.add(tmp)
            listOfWeeks[1].thursday.add(tmp)
            listOfWeeks[2].thursday.add(tmp)
            listOfWeeks[3].thursday.add(tmp)
        }
    }

    curSchedule?.friday?.forEach { day ->
        val tmp = convertDay(day)
        if (day.weekNumber != null) {
            day.weekNumber.forEach { week ->
                listOfWeeks[week - 1].friday.add(tmp)
            }
        } else {
            listOfWeeks[0].friday.add(tmp)
            listOfWeeks[1].friday.add(tmp)
            listOfWeeks[2].friday.add(tmp)
            listOfWeeks[3].friday.add(tmp)
        }
    }

    curSchedule?.saturday?.forEach { day ->
        val tmp = convertDay(day)
        if (day.weekNumber != null) {
            day.weekNumber.forEach { week ->
                listOfWeeks[week - 1].saturday.add(tmp)
            }
        } else {
            listOfWeeks[0].saturday.add(tmp)
            listOfWeeks[1].saturday.add(tmp)
            listOfWeeks[2].saturday.add(tmp)
            listOfWeeks[3].saturday.add(tmp)
        }
    }

    val exams = schedule.exams?.map { convertDay(it) }

    exams?.forEach { exam ->
        exam.weekNumber.forEach { week ->

            if (!listOfWeeks[week - 1].monday.contains(exam)) {
                listOfWeeks[week - 1].monday.add(exam)
            }

            if (!listOfWeeks[week - 1].tuesday.contains(exam)) {
                listOfWeeks[week - 1].tuesday.add(exam)
            }

            if (!listOfWeeks[week - 1].wednesday.contains(exam)) {
                listOfWeeks[week - 1].wednesday.add(exam)
            }

            if (!listOfWeeks[week - 1].thursday.contains(exam)) {
                listOfWeeks[week - 1].thursday.add(exam)
            }

            if (!listOfWeeks[week - 1].friday.contains(exam)) {
                listOfWeeks[week - 1].friday.add(exam)
            }

            if (!listOfWeeks[week - 1].saturday.contains(exam)) {
                listOfWeeks[week - 1].saturday.add(exam)
            }
        }
        if (exam.weekNumber.isEmpty()) {
            for (i in 0..3) {
                listOfWeeks[i].monday.add(exam)
                listOfWeeks[i].tuesday.add(exam)
                listOfWeeks[i].wednesday.add(exam)
                listOfWeeks[i].thursday.add(exam)
                listOfWeeks[i].friday.add(exam)
                listOfWeeks[i].saturday.add(exam)
            }
        }
    }

    listOfWeeks.forEach {
        it.monday.sortBy { it1 -> it1.startLessonTime }
        it.tuesday.sortBy { it1 -> it1.startLessonTime }
        it.wednesday.sortBy { it1 -> it1.startLessonTime }
        it.thursday.sortBy { it1 -> it1.startLessonTime }
        it.friday.sortBy { it1 -> it1.startLessonTime }
        it.saturday.sortBy { it1 -> it1.startLessonTime }
    }


    val schedules = listOfWeeks.map { it.toWeekList() }

    val empFio = if (schedule.employeeDto != null) {
        schedule.employeeDto.lastName + " " + schedule.employeeDto.firstName[0] + "." + if (schedule.employeeDto.middleName != null) {
            " " + schedule.employeeDto.middleName[0] + "."
        } else {
            ""
        }
    } else {
        ""
    }

    var lastDayOfLessonsManually: Long? = null
    (schedules.flatMap { it.monday } +
            schedules.flatMap { it.thursday } +
            schedules.flatMap { it.tuesday } +
            schedules.flatMap { it.wednesday } +
            schedules.flatMap { it.friday } +
            schedules.flatMap { it.saturday }).filter { it.endLessonDate != null }.apply {
        lastDayOfLessonsManually = if (isNotEmpty()) {
            maxOf { it.endLessonDate!! }
        } else null
    }

    var firstDayOfLessonsManually: Long? = null
    (schedules.flatMap { it.monday } +
            schedules.flatMap { it.thursday } +
            schedules.flatMap { it.tuesday } +
            schedules.flatMap { it.wednesday } +
            schedules.flatMap { it.friday } +
            schedules.flatMap { it.saturday }).filter { it.startLessonDate != null }.apply {
        firstDayOfLessonsManually = if (isNotEmpty()) {
            minOf { it.startLessonDate!! }
        } else null
    }




    if (endLessonsDate != null && lastDayOfLessonsManually != null) {
        if (lastDayOfLessonsManually!! > endLessonsDate) {
            endLessonsDate = lastDayOfLessonsManually
        }
    } else {
        if (endLessonsDate == null) endLessonsDate = lastDayOfLessonsManually
    }

    if (startLessonsDate != null && firstDayOfLessonsManually != null) {
        if (firstDayOfLessonsManually!! < startLessonsDate) {
            startLessonsDate = firstDayOfLessonsManually
        }
    } else {
        if (startLessonsDate == null) startLessonsDate = firstDayOfLessonsManually
    }

    return ScheduleModel(
        id = if (isGroupSchedule) schedule.studentGroupDto?.name
            ?: "" else schedule.employeeDto?.urlId ?: "",
        title = if (isGroupSchedule) schedule.studentGroupDto?.name ?: "" else empFio,
        isGroupSchedule = isGroupSchedule,
        startLessonsDate = startLessonsDate,
        endLessonsDate = endLessonsDate,
        startExamsDate = startExamsDate,
        endExamsDate = endExamsDate,
        employeeInfo = employeeInfo,
        studentGroupInfo = groupInfo,
        schedules = schedules,
        exams = exams
    )
}

/**
 * Конвертирует время из "12:22" в 12*60+22 = 742
 */
fun String.timeToInt(): Int {
    val parts = this.split(":")
    return if (parts.size == 2) {
        try {
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            hours * 60 + minutes
        } catch (_: Exception) {
            0
        }
    } else {
        0
    }
}


/**
 * Конвертирует из DTO пару в Lesson
 */
fun convertDay(
    day: ScheduleDto.SchedulesDto.SchedulesItemDto
): Lesson {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    val startLessonTime = day.startLessonTime.timeToInt()
    val endLessonTime = day.endLessonTime.timeToInt()

    val startLessonDate =
        if (day.startLessonDate.isNullOrBlank()) null else dateFormat.parse(day.startLessonDate)?.time
    val endLessonDate =
        if (day.endLessonDate.isNullOrBlank()) null else dateFormat.parse(day.endLessonDate)?.time
    val dateLesson =
        if (day.dateLesson.isNullOrBlank()) null else dateFormat.parse(day.dateLesson)?.time

    val studentGroups = day.studentGroups.map {
        StudentGroupsInfo(
            specialityName = it.specialityName,
            numberOfStudents = it.numberOfStudents,
            name = it.name,
            educationDegree = it.educationDegree
        )
    }
    val employees = day.employees?.map {
        EmployeeInfo(
            id = it.id,
            firstName = it.firstName,
            middleName = it.middleName,
            lastName = it.lastName,
            photoLink = it.photoLink,
            degree = it.degree,
            degreeAbbrev = it.degreeAbbrev,
            rank = it.rank,
            urlId = it.urlId
        )
    }

    return Lesson(
        auditories = day.auditories ?: emptyList(),
        startLessonTime = startLessonTime,
        endLessonTime = endLessonTime,
        startLessonDate = startLessonDate,
        endLessonDate = endLessonDate,
        dateLesson = dateLesson,
        lessonTypeAbbrev = day.lessonTypeAbbrev ?: "",
        note = day.note,
        numSubgroup = day.numSubgroup,
        studentGroups = studentGroups,
        subject = day.subject ?: "",
        subjectFullName = day.subjectFullName ?: "",
        weekNumber = day.weekNumber ?: emptyList(),
        employees = employees,
        announcement = day.announcement,
        split = day.split,
    )
}