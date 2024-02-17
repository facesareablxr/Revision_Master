package uk.ac.aber.dcs.cs31620.revisionmaster.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs31620.revisionmaster.ui.appbars.MainPageTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLevelScaffold(
    navController: NavHostController,
    floatingActionButton: @Composable () -> Unit = { },
    snackbarContent: @Composable (SnackbarData) -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    Scaffold(
        topBar = {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            MainPageTopAppBar(
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            MainPageNavigationBar(navController)
        },
        floatingActionButton = floatingActionButton,
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    snackbarContent(data)
                }
            }
        },
        content = { innerPadding ->
            pageContent(innerPadding)
        }
    )
}
