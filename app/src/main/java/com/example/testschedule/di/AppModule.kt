package com.example.testschedule.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.testschedule.common.Constants
import com.example.testschedule.data.local.Converters
import com.example.testschedule.data.local.UserDataBase
import com.example.testschedule.data.remote.IisAPI
import com.example.testschedule.data.repository.IisAPIRepositoryImpl
import com.example.testschedule.data.repository.UserDatabaseRepositoryImpl
import com.example.testschedule.data.util.GsonParser
import com.example.testschedule.domain.repository.IisAPIRepository
import com.example.testschedule.domain.repository.UserDatabaseRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideIisAPI(): IisAPI {

        val inspector = HttpLoggingInterceptor()
        inspector.level = HttpLoggingInterceptor.Level.BODY

        val clint = OkHttpClient.Builder()
            .addInterceptor(inspector)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_IIS_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clint)
            .build()
            .create(IisAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideIisAPIRepository(api: IisAPI): IisAPIRepository = IisAPIRepositoryImpl(api)


    @Provides
    @Singleton
    fun provideUserDatabase(app: Application): UserDataBase =
        Room.databaseBuilder(
            app,
            UserDataBase::class.java,
            UserDataBase.DATABASE_NAME
        )
            .addTypeConverter(Converters(GsonParser(Gson())))
            .build()


    @Provides
    @Singleton
    fun provideUserDataBaseRepository(db: UserDataBase): UserDatabaseRepository =
        UserDatabaseRepositoryImpl(db.userDao)

    @Singleton
    class MyPreference @Inject constructor(@ApplicationContext context: Context) {
        private val prefs = context.getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE)

        fun getLastUpdateCurrentWeek(): Long = prefs.getLong(Constants.LAST_UPDATE_CURRENT_WEEK, 0)

        fun getSelectedSubgroup(): Int = prefs.getInt(Constants.SELECTED_SUBGROUP, 0)

        fun getCurrentWeek(): Int = prefs.getInt(Constants.CURRENT_WEEK, 0)

        fun setCurrentWeek(date: Long, week: Int) {
            prefs.edit()
                .putLong(Constants.LAST_UPDATE_CURRENT_WEEK, date)
                .putInt(Constants.CURRENT_WEEK, week)
                .apply()
        }

        fun setOpenByDefault(id: String, title: String) {
            prefs.edit()
                .putString(Constants.PREF_OPEN_BY_DEFAULT_TITLE, title)
                .putString(Constants.PREF_OPEN_BY_DEFAULT_ID, id)
                .apply()
        }


        fun getOpenByDefault() = prefs.getString(Constants.PREF_OPEN_BY_DEFAULT_ID, "")
    }

}