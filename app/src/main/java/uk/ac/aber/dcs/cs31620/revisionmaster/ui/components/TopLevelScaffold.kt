package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.MainPageTopAppBar

/**
 * Composable function for creating a top-level scaffold layout for the app.
 * @param navController: NavController to handle navigation within the app.
 * @param floatingActionButton: Composable function to render the floating action button.
 * @param snackbarContent: Composable function to render the content of the snackbar.
 * @param snackbarHostState: SnackbarHostState to manage the state of the snackbar host.
 * @param pageContent: Composable function to render the main content of the page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLevelScaffold(
    navController: NavHostController,
    floatingActionButton: @Composable () -> Unit = { },
    snackbarContent: @Composable (SnackbarData) -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    // Scaffold composable used to create a basic layout structure for the app
    Scaffold(
        // Setting up top bar with custom scroll behavior
        topBar = {
            // Custom top app bar component
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            MainPageTopAppBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        // Setting up bottom navigation bar
        bottomBar = {
            MainPageNavigationBar(navController)
        },
        // Adding floating action button to the scaffold
        floatingActionButton = floatingActionButton,
        // Setting up snackbar host for displaying snackbars
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    snackbarContent(data)
                }
            }
        },
        // Content of the scaffold which can be customized
        content = { innerPadding ->
            pageContent(innerPadding)
        }
    )
}
