package com.shaalevikas.app.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shaalevikas.app.data.model.Need
import com.shaalevikas.app.data.model.NeedStatus
import com.shaalevikas.app.ui.theme.Amber700
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.NeedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onAddNeed: () -> Unit,
    onEditNeed: (String) -> Unit,
    onLogout: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val viewModel: NeedsViewModel = viewModel()
    val activeNeeds by viewModel.activeNeeds.collectAsState()
    val fulfilledNeeds by viewModel.fulfilledNeeds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var needToDelete by remember { mutableStateOf<Need?>(null) }
    var needToFulfill by remember { mutableStateOf<Need?>(null) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    needToDelete?.let { need ->
        AlertDialog(
            onDismissRequest = { needToDelete = null },
            title = { Text("Delete Need") },
            text = { Text("Are you sure you want to delete \"${need.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNeed(need.id) {}
                    needToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { needToDelete = null }) { Text("Cancel") } }
        )
    }

    needToFulfill?.let { need ->
        MarkFulfilledDialog(
            need = need,
            isLoading = isLoading,
            onConfirm = { beforeUri, afterUri ->
                viewModel.markFulfilled(
                    needId = need.id,
                    beforeUri = beforeUri,
                    afterUri = afterUri,
                    onSuccess = { needToFulfill = null }
                )
            },
            onDismiss = { needToFulfill = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Panel", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Headmaster Dashboard", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700),
                actions = {
                    IconButton(onClick = onGalleryClick) {
                        Icon(Icons.Default.PhotoLibrary, null, tint = Color.White)
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNeed, containerColor = Amber700) {
                Icon(Icons.Default.Add, "Add Need", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(label = "Active", count = activeNeeds.size, color = Green700)
                StatChip(label = "Fulfilled", count = fulfilledNeeds.size, color = Color.Gray)
                StatChip(label = "Total ₹", count = activeNeeds.sumOf { it.amountPledged }.toInt(), color = Amber700)
            }

            TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(
                        "Active (${activeNeeds.size})",
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(
                        "Fulfilled (${fulfilledNeeds.size})",
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            val displayNeeds = if (selectedTab == 0) activeNeeds else fulfilledNeeds

            if (displayNeeds.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inbox, null, modifier = Modifier.size(56.dp), tint = Color.LightGray)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (selectedTab == 0) "No active needs. Tap + to add one."
                            else "No fulfilled needs yet.",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayNeeds, key = { it.id }) { need ->
                        AdminNeedCard(
                            need = need,
                            onEdit = { onEditNeed(need.id) },
                            onDelete = { needToDelete = need },
                            onMarkFulfilled = { needToFulfill = need }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarkFulfilledDialog(
    need: Need,
    isLoading: Boolean,
    onConfirm: (beforeUri: Uri?, afterUri: Uri?) -> Unit,
    onDismiss: () -> Unit
) {
    var beforeUri by remember { mutableStateOf<Uri?>(null) }
    var afterUri by remember { mutableStateOf<Uri?>(null) }
    var dialogError by remember { mutableStateOf<String?>(null) }

    val beforePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> beforeUri = uri }
    val afterPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> afterUri = uri }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Mark as Fulfilled", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Mark \"${need.title}\" as fulfilled?\nPhotos are optional but recommended.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                OutlinedButton(
                    onClick = { beforePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (beforeUri != null) "Before photo selected ✓" else "Before Photo (optional)")
                }

                OutlinedButton(
                    onClick = { afterPicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (afterUri != null) "After photo selected ✓" else "After Photo (optional)")
                }

                dialogError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(beforeUri, afterUri) },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Green700)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Saving...")
                } else {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Mark Fulfilled")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isLoading) onDismiss() }) { Text("Cancel") }
        }
    )
}

@Composable
fun StatChip(label: String, count: Int, color: Color) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
            Text(label, fontSize = 11.sp, color = color)
        }
    }
}

@Composable
fun AdminNeedCard(
    need: Need,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkFulfilled: () -> Unit
) {
    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        need.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(need.category, color = Green700, fontSize = 12.sp)
                }
                Row {
                    if (need.status == NeedStatus.ACTIVE.name) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Edit, "Edit", tint = Green700, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onMarkFulfilled, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.CheckCircle, "Mark Fulfilled", tint = Amber700, modifier = Modifier.size(20.dp))
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { need.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Green700,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${need.progressPercent.toInt()}% funded", fontSize = 12.sp, color = Green700)
                Text("₹${need.amountPledged.toLong()} / ₹${need.costEstimate.toLong()}", fontSize = 12.sp, color = Color.Gray)
            }
            if (need.status == NeedStatus.ACTIVE.name) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onMarkFulfilled,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Green700),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Mark as Fulfilled", fontSize = 13.sp)
                }
            }
        }
    }
}
