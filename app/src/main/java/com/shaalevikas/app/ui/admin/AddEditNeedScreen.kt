package com.shaalevikas.app.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shaalevikas.app.data.model.Need
import com.shaalevikas.app.data.model.NeedCategory
import com.shaalevikas.app.ui.theme.Amber700
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.NeedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNeedScreen(needId: String?, onBack: () -> Unit, onSuccess: () -> Unit) {
    val viewModel: NeedsViewModel = viewModel()
    val selectedNeed by viewModel.selectedNeed.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val aiDescription by viewModel.aiGeneratedDescription.collectAsState()
    val aiCost by viewModel.aiEstimatedCost.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(NeedCategory.OTHER.name) }
    var description by remember { mutableStateOf("") }
    var costEstimate by remember { mutableStateOf("") }
    var urgency by remember { mutableIntStateOf(1) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingPhotoUrl by remember { mutableStateOf("") }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var beforeUri by remember { mutableStateOf<Uri?>(null) }
    var afterUri by remember { mutableStateOf<Uri?>(null) }
    var showMarkFulfilledDialog by remember { mutableStateOf(false) }

    val isEditMode = needId != null

    LaunchedEffect(needId) {
        if (needId != null) viewModel.loadNeed(needId)
    }

    LaunchedEffect(selectedNeed) {
        selectedNeed?.let { n ->
            if (isEditMode) {
                title = n.title
                category = n.category
                description = n.description
                costEstimate = n.costEstimate.toString()
                urgency = n.urgency
                existingPhotoUrl = n.photoUrl
            }
        }
    }

    LaunchedEffect(aiDescription) { aiDescription?.let { description = it; viewModel.clearAiDescription() } }
    LaunchedEffect(aiCost) { aiCost?.let { costEstimate = it.toLong().toString(); viewModel.clearAiCost() } }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> imageUri = uri }
    val beforePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> beforeUri = uri }
    val afterPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> afterUri = uri }

    if (showMarkFulfilledDialog) {
        AlertDialog(
            onDismissRequest = { showMarkFulfilledDialog = false },
            title = { Text("Mark as Fulfilled") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Upload Before & After photos to complete this need.")
                    OutlinedButton(onClick = { beforePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(8.dp))
                        Text(if (beforeUri != null) "Before photo selected ✓" else "Select Before Photo")
                    }
                    OutlinedButton(onClick = { afterPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(8.dp))
                        Text(if (afterUri != null) "After photo selected ✓" else "Select After Photo")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bUri = beforeUri; val aUri = afterUri
                        if (bUri != null && aUri != null && needId != null) {
                            viewModel.markFulfilled(needId, bUri, aUri) { showMarkFulfilledDialog = false; onSuccess() }
                        }
                    },
                    enabled = beforeUri != null && afterUri != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Green700)
                ) { Text("Mark Fulfilled") }
            },
            dismissButton = { TextButton(onClick = { showMarkFulfilledDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Need" else "Add New Need", color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700),
                actions = {
                    if (isEditMode) {
                        TextButton(onClick = { showMarkFulfilledDialog = true }) {
                            Text("Mark Fulfilled", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Need Title *") },
                leadingIcon = { Icon(Icons.Default.Title, null) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            ExposedDropdownMenuBox(expanded = showCategoryMenu, onExpandedChange = { showCategoryMenu = !showCategoryMenu }) {
                OutlinedTextField(
                    value = category, onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }) {
                    NeedCategory.entries.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat.name) }, onClick = { category = cat.name; showCategoryMenu = false })
                    }
                }
            }

            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✨ GenAI Assistant", fontWeight = FontWeight.Bold, color = Amber700, fontSize = 14.sp)
                    Text("Use Gemini AI to auto-generate description and estimate cost.", color = Color.Gray, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { if (title.isNotBlank()) viewModel.generateAiDescription(title, category) },
                            modifier = Modifier.weight(1f),
                            enabled = title.isNotBlank() && !isAiLoading
                        ) {
                            if (isAiLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            else Text("Generate Description", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { if (title.isNotBlank()) viewModel.estimateAiCost(title, category) },
                            modifier = Modifier.weight(1f),
                            enabled = title.isNotBlank() && !isAiLoading
                        ) {
                            if (isAiLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            else Text("Estimate Cost", fontSize = 12.sp)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description *") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 6
            )

            OutlinedTextField(
                value = costEstimate, onValueChange = { costEstimate = it },
                label = { Text("Cost Estimate (₹) *") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Text("Urgency Level: $urgency/3", fontWeight = FontWeight.Medium)
            Slider(value = urgency.toFloat(), onValueChange = { urgency = it.toInt() }, valueRange = 1f..3f, steps = 1, colors = SliderDefaults.colors(thumbColor = Green700, activeTrackColor = Green700))

            Text("Need Photo", fontWeight = FontWeight.Medium)
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageUri != null -> AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    existingPhotoUrl.isNotEmpty() -> AsyncImage(model = existingPhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Tap to select photo", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }

            Button(
                onClick = {
                    val need = Need(
                        id = if (isEditMode) needId!! else "",
                        title = title.trim(),
                        category = category,
                        description = description.trim(),
                        costEstimate = costEstimate.toDoubleOrNull() ?: 0.0,
                        urgency = urgency,
                        photoUrl = existingPhotoUrl
                    )
                    viewModel.saveNeed(need, imageUri) { onSuccess() }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && (costEstimate.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Green700),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else Text(if (isEditMode) "Update Need" else "Post Need", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
