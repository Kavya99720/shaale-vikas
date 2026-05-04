package com.shaalevikas.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.shaalevikas.app.data.model.BadgeTier
import com.shaalevikas.app.ui.theme.Amber700
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.PledgeViewModel
import com.shaalevikas.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val userViewModel: UserViewModel = viewModel()
    val pledgeViewModel: PledgeViewModel = viewModel()
    val currentUser by userViewModel.currentUserData.collectAsState()
    val userPledges by pledgeViewModel.userPledges.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        uid?.let {
            userViewModel.loadUser(it)
            pledgeViewModel.loadUserPledges()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = { TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("Sign Out", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700),
                actions = { IconButton(onClick = { showLogoutDialog = true }) { Icon(Icons.Default.Logout, null, tint = Color.White) } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(72.dp).background(Green700.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentUser?.name?.firstOrNull()?.uppercase() ?: "A", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Green700)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(currentUser?.name ?: "Loading...", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(currentUser?.email ?: "", color = Color.Gray, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))
                        val badgeEmoji = when (currentUser?.badgeTier) { BadgeTier.PLATINUM -> "💎"; BadgeTier.GOLD -> "🥇"; BadgeTier.SILVER -> "🥈"; else -> "🥉" }
                        val badgeColor = when (currentUser?.badgeTier) { BadgeTier.PLATINUM -> Color(0xFF9C27B0); BadgeTier.GOLD -> Amber700; BadgeTier.SILVER -> Color.Gray; else -> Color(0xFFCD7F32) }
                        Surface(color = badgeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(badgeEmoji, fontSize = 16.sp)
                                Spacer(Modifier.width(6.dp))
                                Text("${currentUser?.badgeTier?.name ?: "BRONZE"} Donor", fontWeight = FontWeight.Bold, color = badgeColor)
                            }
                        }
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("₹${currentUser?.totalPledged?.toLong() ?: 0}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Green700)
                            Text("Total Pledged", color = Color.Gray, fontSize = 12.sp)
                        }
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("${userPledges.size}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Green700)
                            Text("Needs Supported", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (userPledges.isNotEmpty()) {
                item { Text("My Pledges", fontWeight = FontWeight.Bold, fontSize = 17.sp) }
                items(userPledges) { pledge ->
                    Card(shape = RoundedCornerShape(10.dp)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, null, tint = Green700, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Need ID: ${pledge.needId.take(8)}...", fontSize = 13.sp, color = Color.Gray)
                            }
                            Text("₹${pledge.amount.toLong()}", fontWeight = FontWeight.Bold, color = Green700)
                        }
                    }
                }
            }
        }
    }
}
