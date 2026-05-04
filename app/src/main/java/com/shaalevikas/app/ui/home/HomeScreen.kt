package com.shaalevikas.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shaalevikas.app.data.model.Need
import com.shaalevikas.app.ui.theme.Amber700
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.NeedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNeedClick: (String) -> Unit,
    onHallOfFameClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSchoolClick: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: NeedsViewModel = viewModel()
    val activeNeeds by viewModel.activeNeeds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) { Text("Sign Out", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Shaale-Vikas", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("School Needs Dashboard", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700),
                actions = {
                    IconButton(onClick = onSchoolClick) { Icon(Icons.Default.School, null, tint = Color.White) }
                    IconButton(onClick = onProfileClick) { Icon(Icons.Default.Person, null, tint = Color.White) }
                    IconButton(onClick = { showLogoutDialog = true }) { Icon(Icons.Default.Logout, null, tint = Color.White) }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Needs") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; onHallOfFameClick() },
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    label = { Text("Hall of Fame") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; onGalleryClick() },
                    icon = { Icon(Icons.Default.PhotoLibrary, null) },
                    label = { Text("Gallery") }
                )
            }
        }
    ) { padding ->
        if (isLoading && activeNeeds.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Green700)
            }
        } else if (activeNeeds.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.School, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No active needs right now", color = Color.Gray)
                    Text("Check back soon!", color = Color.LightGray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${activeNeeds.size} Active Need${if (activeNeeds.size != 1) "s" else ""}",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
                items(activeNeeds, key = { it.id }) { need ->
                    NeedCard(need = need, onClick = { onNeedClick(need.id) })
                }
            }
        }
    }
}

@Composable
fun NeedCard(need: Need, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            if (need.photoUrl.isNotEmpty()) {
                AsyncImage(
                    model = need.photoUrl,
                    contentDescription = need.title,
                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏫", fontSize = 48.sp)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Green700.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            need.category, fontSize = 11.sp, color = Green700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (need.urgency >= 3) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                            Text("URGENT", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(need.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(need.description, color = Color.Gray, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { need.progressPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Green700,
                    trackColor = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${need.progressPercent.toInt()}% funded", fontSize = 12.sp, color = Green700, fontWeight = FontWeight.Medium)
                    Text("₹${need.costEstimate.toLong()}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
