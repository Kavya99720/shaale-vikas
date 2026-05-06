package com.shaalevikas.app.ui.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shaalevikas.app.data.model.SchoolProfile
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolProfileScreen(isAdmin: Boolean = false, onBack: () -> Unit) {
    val viewModel: UserViewModel = viewModel()
    val schoolProfile by viewModel.schoolProfile.collectAsState()
    val isSchoolLoading by viewModel.isSchoolLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditSchoolProfileDialog(
            existing = schoolProfile,
            isSaving = isSaving,
            error = saveError,
            onSave = { profile ->
                viewModel.saveSchoolProfile(profile) { showEditDialog = false }
            },
            onDismiss = { showEditDialog = false; viewModel.clearSaveError() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700),
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, "Edit School Profile", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).background(Green700),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏫", fontSize = 64.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        schoolProfile?.name ?: "Government School",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when {
                    isSchoolLoading -> {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Green700)
                                Spacer(Modifier.height(12.dp))
                                Text("Loading school profile...", color = Color.Gray)
                            }
                        }
                    }
                    schoolProfile == null -> {
                        Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.School, null, modifier = Modifier.size(56.dp), tint = Color.LightGray)
                                Text("No school profile set up yet", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                                Text("Contact the headmaster to add school details.", fontSize = 13.sp, color = Color.LightGray)
                                if (isAdmin) {
                                    Spacer(Modifier.height(4.dp))
                                    Button(
                                        onClick = { showEditDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Green700)
                                    ) {
                                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Set Up School Profile")
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        val school = schoolProfile!!
                        Card(shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                InfoRow(icon = Icons.Default.LocationOn, label = "Location", value = school.location.ifEmpty { "—" })
                                InfoRow(icon = Icons.Default.CalendarToday, label = "Established", value = school.established.ifEmpty { "—" })
                                InfoRow(icon = Icons.Default.People, label = "Students", value = if (school.studentCount > 0) "${school.studentCount}" else "—")
                            }
                        }
                        if (school.about.isNotEmpty()) {
                            Text("About", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                                Text(school.about, modifier = Modifier.padding(16.dp), lineHeight = 22.sp, color = Color(0xFF2E7D32))
                            }
                        }
                        if (isAdmin) {
                            Spacer(Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Edit School Profile")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditSchoolProfileDialog(
    existing: SchoolProfile?,
    isSaving: Boolean,
    error: String?,
    onSave: (SchoolProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var location by remember { mutableStateOf(existing?.location ?: "") }
    var established by remember { mutableStateOf(existing?.established ?: "") }
    var studentCount by remember { mutableStateOf(if ((existing?.studentCount ?: 0) > 0) existing!!.studentCount.toString() else "") }
    var about by remember { mutableStateOf(existing?.about ?: "") }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(if (existing == null) "Set Up School Profile" else "Edit School Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("School Name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location / Village") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = established, onValueChange = { established = it }, label = { Text("Year Established") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(
                    value = studentCount, onValueChange = { studentCount = it },
                    label = { Text("Number of Students") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = about, onValueChange = { about = it },
                    label = { Text("About the School") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 5
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(SchoolProfile(
                        id = existing?.id ?: "",
                        name = name.trim(),
                        location = location.trim(),
                        established = established.trim(),
                        studentCount = studentCount.toIntOrNull() ?: 0,
                        about = about.trim()
                    ))
                },
                enabled = !isSaving && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Green700)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("Saving...")
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = { TextButton(onClick = { if (!isSaving) onDismiss() }) { Text("Cancel") } }
    )
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
