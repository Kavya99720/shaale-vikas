package com.shaalevikas.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shaalevikas.app.data.model.UserRole
import com.shaalevikas.app.ui.admin.AdminDashboardScreen
import com.shaalevikas.app.ui.admin.AddEditNeedScreen
import com.shaalevikas.app.ui.auth.LoginScreen
import com.shaalevikas.app.ui.auth.RegisterScreen
import com.shaalevikas.app.ui.auth.ForgotPasswordScreen
import com.shaalevikas.app.ui.auth.SplashScreen
import com.shaalevikas.app.ui.gallery.ImpactGalleryScreen
import com.shaalevikas.app.ui.halloffame.HallOfFameScreen
import com.shaalevikas.app.ui.home.HomeScreen
import com.shaalevikas.app.ui.needs.NeedDetailScreen
import com.shaalevikas.app.ui.pledge.PledgeScreen
import com.shaalevikas.app.ui.profile.ProfileScreen
import com.shaalevikas.app.ui.school.SchoolProfileScreen
import com.shaalevikas.app.viewmodel.AuthViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val NEED_DETAIL = "need_detail/{needId}"
    const val PLEDGE = "pledge/{needId}"
    const val HALL_OF_FAME = "hall_of_fame"
    const val GALLERY = "gallery"
    const val PROFILE = "profile"
    const val SCHOOL = "school"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADD_EDIT_NEED = "add_edit_need?needId={needId}"

    fun needDetail(needId: String) = "need_detail/$needId"
    fun pledge(needId: String) = "pledge/$needId"
    fun addEditNeed(needId: String? = null) = if (needId != null) "add_edit_need?needId=$needId" else "add_edit_need?needId="
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val isRoleLoaded by authViewModel.isRoleLoaded.collectAsState()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                isRoleLoaded = isRoleLoaded,
                onNavigate = {
                    if (currentUser != null) {
                        if (userRole == UserRole.ADMIN.name) {
                            navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.SPLASH) { inclusive = true } }
                        } else {
                            navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                        }
                    } else {
                        navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    if (role == UserRole.ADMIN.name) {
                        navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    } else {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNeedClick = { needId -> navController.navigate(Routes.needDetail(needId)) },
                onHallOfFameClick = { navController.navigate(Routes.HALL_OF_FAME) },
                onGalleryClick = { navController.navigate(Routes.GALLERY) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onSchoolClick = { navController.navigate(Routes.SCHOOL) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            route = Routes.NEED_DETAIL,
            arguments = listOf(navArgument("needId") { type = NavType.StringType })
        ) { backStackEntry ->
            NeedDetailScreen(
                needId = backStackEntry.arguments?.getString("needId") ?: "",
                onPledgeClick = { needId -> navController.navigate(Routes.pledge(needId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PLEDGE,
            arguments = listOf(navArgument("needId") { type = NavType.StringType })
        ) { backStackEntry ->
            PledgeScreen(
                needId = backStackEntry.arguments?.getString("needId") ?: "",
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HALL_OF_FAME) {
            HallOfFameScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.GALLERY) {
            ImpactGalleryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.SCHOOL) {
            SchoolProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onAddNeed = { navController.navigate(Routes.addEditNeed()) },
                onEditNeed = { needId -> navController.navigate(Routes.addEditNeed(needId)) },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onGalleryClick = { navController.navigate(Routes.GALLERY) }
            )
        }

        composable(
            route = Routes.ADD_EDIT_NEED,
            arguments = listOf(navArgument("needId") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val needId = backStackEntry.arguments?.getString("needId") ?: ""
            AddEditNeedScreen(
                needId = needId.ifEmpty { null },
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}
