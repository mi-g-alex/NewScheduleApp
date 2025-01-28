package com.example.testschedule.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.updateAll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testschedule.domain.model.schedule.ScheduleModel
import com.example.testschedule.presentation.schedule_screen.view_schedule_screen.exams.ViewExamsScreen
import com.example.testschedule.presentation.schedule_screen.view_schedule_screen.schedule.ViewScheduleScreen
import com.example.testschedule.presentation.ui.theme.TestScheduleTheme
import com.example.testschedule.widget.ScheduleWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestScheduleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationScreen()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch {
            ScheduleWidget().updateAll(applicationContext)
        }
    }
}

@AndroidEntryPoint
class PreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLinkIntent: Intent = intent
        val appLinkData: Uri? = appLinkIntent.data

        val examsData: MutableMap<String, ScheduleModel> = mutableMapOf()

        setContent {
            val navController = rememberNavController()
            fun popNav() {
                navController.popBackStack()
            }
            TestScheduleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        route = Routes.SCHEDULE_ROUTE,
                        startDestination = Routes.SCHEDULE_HOME_ROUTE
                    ) {
                        composable(
                            route = Routes.SCHEDULE_HOME_ROUTE
                        ) { entry ->
                            val id = entry.arguments?.getString("id")
                            val title = entry.arguments?.getString("title") ?: id
                            ViewScheduleScreen(
                                goBackSet = { _, _ ->
                                },
                                scheduleId = id ?: appLinkData?.lastPathSegment ?: "",
                                titleLink = title ?: appLinkData?.lastPathSegment
                                ?: "Плохая ссылка",
                                navToExams = { exams ->
                                    examsData[exams.id] = exams
                                    navController.navigate("SCHEDULE_EXAMS_VIEW/" + exams.id)
                                },
                                goToAddSchedule = {},
                                navToLogin = {},
                                navToProfile = {},
                                isPrev = true,
                                goToPreview = { urlId, uTitle ->
                                    navController.navigate("SCHEDULE_HOME_ROUTE/${urlId}/${uTitle}/${true}")
                                }
                            )
                        }
                        composable(
                            route = Routes.SCHEDULE_EXAMS_VIEW_ROUTE,
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { 450 },
                                    animationSpec = tween(
                                        durationMillis = 250,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeIn(animationSpec = tween(250))
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { 450 },
                                    animationSpec = tween(
                                        durationMillis = 250,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeOut(animationSpec = tween(250))
                            }
                        ) { entry ->
                            val id = entry.arguments?.getString("id") ?: ""
                            val exams = examsData[id]
                            ViewExamsScreen(
                                scheduleExams = exams!!,
                                navBack = {
                                    popNav()
                                },
                                selectScheduleClicked = { urlId, title ->
                                    navController.navigate("SCHEDULE_HOME_ROUTE/${urlId}/${title}/${true}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
