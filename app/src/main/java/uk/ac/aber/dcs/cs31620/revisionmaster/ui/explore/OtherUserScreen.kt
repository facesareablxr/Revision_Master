package uk.ac.aber.dcs.cs31620.revisionmaster.ui.explore

/*
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OtherUserScreen(
    navController: NavController,
    userId: String,
    userViewModel: UserViewModel = viewModel(),
    flashcardViewModel: FlashcardViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Collect other user's data
    val userState by userViewModel.user.collectAsState()
    val followingListState by userViewModel.followingList.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getUserData(userId)
    }

    Scaffold(
        topBar = {
            NonMainTopAppBar(title = userState!!.username)
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                UserHeader(user = userState)
                Spacer(modifier = Modifier.height(16.dp))
                DecksSection(navController = navController, userId = userId, flashcardViewModel = flashcardViewModel)
            }
        }
    )
}

@Composable
fun UserHeader(user: User?) {
    if (user != null) {
        Text(text = user.username, style = MaterialTheme.typography.headlineMedium)
        Text(text = user.institution ?: "", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun DecksSection(navController: NavController, userId: String, flashcardViewModel: FlashcardViewModel) {
    // Fetch user's decks
    val userDecksState by flashcardViewModel.getUserDecks(userId).collectAsState(initial = emptyList())

    Column {
        Text(text = "Decks", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (userDecksState.isNotEmpty()) {
            LazyColumn {
                items(userDecksState) { deck ->
                    DeckCard(deck = deck, onClick = { */
/* Handle deck click *//*
 })
                }
            }
        } else {
            Text(text = "No decks found.")
        }
    }
}

@Composable
fun DeckCard(deck: Deck, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = deck.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = deck.subject, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FollowButton(isFollowing: Boolean, onFollowToggle: (Boolean) -> Unit) {
    Button(
        onClick = { onFollowToggle(!isFollowing) },
    ) {
        Text(text = if (isFollowing) "Unfollow" else "Follow")
    }
}
*/
