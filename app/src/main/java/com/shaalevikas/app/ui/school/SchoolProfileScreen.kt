package com.shaalevikas.app.ui.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolProfileScreen(onBack: () -> Unit) {
    val viewModel: UserViewModel = viewModel()
    val schoolProfile by viewModel.schoolProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Profile", color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Green700), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏫", fontSize = 64.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(schoolProfile?.name ?: "Government School", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                schoolProfile?.let { school ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoRow(icon = Icons.Default.LocationOn, label = "Location", value = school.location)
                            InfoRow(icon = Icons.Default.CalendarToday, label = "Established", value = school.established)
                            InfoRow(icon = Icons.Default.People, label = "Students", value = "${school.studentCount}")
                        }
                    }

                    if (school.about.isNotEmpty()) {
                        Text("About", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                            Text(school.about, modifier = Modifier.padding(16.dp), lineHeight = 22.sp, color = Color(0xFF2E7D32))
                        }
                    }
                } ?: run {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Green700)
                            Spacer(Modifier.height(12.dp))
                            Text("Loading school profile...", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Green700, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}
