package com.example.testschedule.data.repository

import android.util.Log
import com.example.testschedule.data.remote.IisAPI
import com.example.testschedule.data.remote.dto.account.headman.create_omissions.HeadmanCreateOmissionsDto
import com.example.testschedule.data.remote.dto.account.notifications.NotificationsDto
import com.example.testschedule.data.remote.dto.account.notifications.ReadNotificationDto
import com.example.testschedule.data.remote.dto.account.settings.email.SendConfirmMessageResponseDto
import com.example.testschedule.data.remote.dto.account.settings.password.ChangePasswordDto
import com.example.testschedule.data.remote.dto.account.study.mark_sheet.additional.MarkSheetTypeModel
import com.example.testschedule.data.remote.dto.auth.LoginAndPasswordDto
import com.example.testschedule.data.remote.dto.auth.UserBasicDataDto
import com.example.testschedule.domain.model.account.announcement.AnnouncementModel
import com.example.testschedule.domain.model.account.dormitory.DormitoryModel
import com.example.testschedule.domain.model.account.dormitory.PrivilegesModel
import com.example.testschedule.domain.model.account.group.GroupModel
import com.example.testschedule.domain.model.account.headman.create_omissions.HeadmanGetOmissionsModel
import com.example.testschedule.domain.model.account.mark_book.MarkBookModel
import com.example.testschedule.domain.model.account.notifications.NotificationModel
import com.example.testschedule.domain.model.account.omissions.OmissionsModel
import com.example.testschedule.domain.model.account.penalty.PenaltyModel
import com.example.testschedule.domain.model.account.profile.AccountProfileModel
import com.example.testschedule.domain.model.account.rating.RatingModel
import com.example.testschedule.domain.model.account.settings.email.ContactsModel
import com.example.testschedule.domain.model.account.settings.email.ContactsUpdateRequestModel
import com.example.testschedule.domain.model.account.settings.email.SendConfirmCodeRequestModel
import com.example.testschedule.domain.model.account.study.certificate.CertificateModel
import com.example.testschedule.domain.model.account.study.certificate.CreateCertificateModel
import com.example.testschedule.domain.model.account.study.certificate.NewCertificatePlacesModel
import com.example.testschedule.domain.model.account.study.mark_sheet.MarkSheetModel
import com.example.testschedule.domain.model.account.study.mark_sheet.create.CreateMarkSheetModel
import com.example.testschedule.domain.model.account.study.mark_sheet.create.MarkSheetSubjectsModel
import com.example.testschedule.domain.model.account.study.mark_sheet.create.SearchEmployeeMarkSheetModel
import com.example.testschedule.domain.model.schedule.ListOfEmployeesModel
import com.example.testschedule.domain.model.schedule.ListOfGroupsModel
import com.example.testschedule.domain.model.schedule.ScheduleModel
import com.example.testschedule.domain.model.schedule.scheduleFromDtoToModel
import com.example.testschedule.domain.repository.IisAPIRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject

class IisAPIRepositoryImpl @Inject constructor(
    private val api: IisAPI
) : IisAPIRepository {

    // Расписание
    override suspend fun getListOfGroups(): List<ListOfGroupsModel> =
        api.getListOfGroups().map { it.toModel() }


    override suspend fun getListOfEmployees(): List<ListOfEmployeesModel> =
        api.getListOfEmployees().map { it.toModel() }


    override suspend fun getSchedule(id: String): ScheduleModel =
        if (id.isNotEmpty() && id[0] in '0'..'9') {
            scheduleFromDtoToModel(api.getGroupSchedule(id))
            //scheduleFromDtoToModel(Constants.test)
        } else {
            scheduleFromDtoToModel(api.getEmployeeSchedule(id))
        }


    override suspend fun getCurrentWeek(): Int = api.getCurrentWeek()


    // Авторизация
    override suspend fun loginToAccount(
        username: String,
        password: String
    ): Call<UserBasicDataDto?> =
        api.loginToAccount(LoginAndPasswordDto(username, password))


    // Профиль
    override suspend fun getAccountProfile(cookies: String): AccountProfileModel =
        api.getAccountProfile(cookies).toModel()

    // Уведомления
    override suspend fun getNotifications(cookies: String): List<NotificationModel> {
        val pageSize = 300
        var crnPage = 0
        var last: NotificationsDto
        val list: MutableList<NotificationModel> = mutableListOf()
        do {
            last =
                api.getNotifications(cookies = cookies, pagerNumber = crnPage, pagerSize = pageSize)
            last.notifications.forEach {
                list.add(it.toModel())
            }
            crnPage++
        } while (last.hasNext)
        return list.toList()
    }

    override suspend fun readNotifications(cookies: String, data: List<Int>) {
        val list = data.map { ReadNotificationDto(it, true) }
        api.readNotifications(cookies, list)
    }

    // Общага и льготы
    override suspend fun getDormitory(cookies: String): List<DormitoryModel> =
        api.getDormitory(cookies).map { it.toModel() }

    override suspend fun getPrivileges(cookies: String): List<PrivilegesModel> =
        api.getPrivileges(cookies).map { it.toModel() }

    // Группа
    override suspend fun getUserGroup(cookies: String): GroupModel =
        api.getGroupList(cookies).toModel()

    // Зачётка
    override suspend fun getMarkBook(cookies: String): MarkBookModel =
        api.getMarkBook(cookies).toModel()

    // Пропуски
    override suspend fun getOmissions(cookies: String): List<OmissionsModel> =
        api.getOmissions(cookies).omissionDtoList?.map { it.toModel() } ?: emptyList()

    // Взыскания
    override suspend fun getPenalty(cookies: String): List<PenaltyModel> =
        api.getPenalty(cookies).map { it.toModel() }

    // События
    override suspend fun getAnnouncements(cookies: String): List<AnnouncementModel> =
        api.getAnnouncements(cookies).map { it.toModel() }

    // Рейтинг
    override suspend fun getRating(cookies: String): RatingModel =
        api.getRatingOfStudent(cookies)[0].toModel()

    // Учёба

    override suspend fun getCertificates(cookies: String): List<CertificateModel> =
        api.getCertificates(cookies).map { it.toModel() }

    override suspend fun getNewCertificatePlaces(cookies: String): List<NewCertificatePlacesModel> =
        api.getNewCertificatePlaces(cookies).map { it.toModel() }

    override suspend fun createCertificate(
        request: CreateCertificateModel,
        cookies: String
    ): Call<Any> =
        api.createCertificate(request.toDto(), cookies)

    override suspend fun closeCertificate(id: Int, cookies: String) =
        api.closeCertificate(id, cookies)

    override suspend fun getMarkSheets(cookies: String): List<MarkSheetModel> =
        api.getMarkSheets(cookies).map { it.toModel() }

    override suspend fun closeMarkSheet(id: Int, cookies: String) =
        api.closeMarkSheet(id, cookies)

    override suspend fun createMarkSheet(
        request: CreateMarkSheetModel,
        cookies: String
    ): Call<Any> =
        api.createMarkSheet(request.toDto(), cookies)

    override suspend fun getMarkSheetTypes(cookies: String): List<MarkSheetTypeModel> =
        api.getMarkSheetTypes(cookies).map { it.toModel() }

    override suspend fun getMarkSheetSubjects(cookies: String): List<MarkSheetSubjectsModel> =
        api.getMarkSheetSubjects(cookies).map { it.toModel() }

    override suspend fun searchEmployeeById(
        thId: Int?,
        focsId: Int?,
        cookies: String
    ): List<SearchEmployeeMarkSheetModel> {
        if (thId != null) {
            return api.searchEmployeeThIdMarkSheet(thId, cookies)?.map { it.toModel() }
                ?: emptyList()
        }
        if (focsId != null) {
            return api.searchEmployeeFocsIdMarkSheet(focsId, cookies)?.map { it.toModel() }
                ?: emptyList()
        }
        return emptyList()
    }

    override suspend fun searchEmployeeByName(name: String): List<SearchEmployeeMarkSheetModel> =
        api.searchEmployeeNameMarkSheet(name)?.map { it.toModel() } ?: emptyList()

    override suspend fun settingsUpdateBio(profile: AccountProfileModel, cookies: String) =
        api.settingsUpdateBio(profile.toDto(), cookies)

    override suspend fun settingsUpdateSkills(
        skills: List<AccountProfileModel.SkillModel>,
        cookies: String
    ): Call<ResponseBody?> =
        api.settingsUpdateSkills(skills.map { it.toDto() }, cookies)

    override suspend fun settingsUpdateLinks(
        links: List<AccountProfileModel.ReferenceModel>,
        cookies: String
    ) = api.settingsUpdateLinks(links.map { it.toDto() }, cookies)

    override suspend fun settingsUpdateViewProfile(profile: AccountProfileModel, cookies: String) =
        api.settingsUpdateViewProfile(profile.toDto(), cookies)

    override suspend fun settingsUpdateViewRating(profile: AccountProfileModel, cookies: String) =
        api.settingsUpdateViewRating(profile.toDto(), cookies)

    override suspend fun settingsUpdateViewJob(profile: AccountProfileModel, cookies: String) =
        api.settingsUpdateViewJob(profile.toDto(), cookies)

    override suspend fun settingsUpdatePassword(
        password: ChangePasswordDto,
        cookies: String
    ): Call<ResponseBody?> =
        api.settingsChangePassword(password, cookies)

    override suspend fun settingsEmailGetContacts(cookies: String): ContactsModel =
        api.settingsEmailGetContacts(cookies).toModel()

    override suspend fun settingsEmailUpdate(
        mail: ContactsUpdateRequestModel,
        cookies: String
    ): Call<ResponseBody?> =
        api.settingsEmailUpdate(mail.toDto(), cookies)

    override suspend fun settingsEmailGetConfirmCode(
        id: Int,
        cookies: String
    ): Call<SendConfirmMessageResponseDto> =
        api.settingsEmailGetConfirmCode(id, cookies)

    override suspend fun settingsEmailConfirmMessage(
        code: SendConfirmCodeRequestModel,
        cookies: String
    ): Call<ResponseBody?> =
        api.settingsEmailConfirmMessage(code.toDto(), cookies)

    override suspend fun settingsUpdatePhoto(base64: String, cookies: String): Call<String?> {
        val requestBody = (base64).toRequestBody("text/plain".toMediaType())
        return api.settingsUpdatePhoto(requestBody, cookies)
    }

    override suspend fun headmanGetOmissionsByDate(
        date: String,
        cookies: String
    ): HeadmanGetOmissionsModel = api.headmanGetOmissionsByDate(date, cookies).toModel(date)

    override suspend fun headmanSaveOmissions(
        omissions: HeadmanCreateOmissionsDto,
        cookies: String
    ): Call<ResponseBody?> = api.headmanSaveOmissions(omissions, cookies)
}