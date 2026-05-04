package com.shaalevikas.app.ui.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shaalevikas.app.data.model.Need
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.NeedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactGalleryScreen(onBack: () -> Unit) {
    val viewModel: NeedsViewModel = viewModel()
    val fulfilledNeeds by viewModel.fulfilledNeeds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impact Gallery", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700)
            )
        }
    ) { padding ->
        if (fulfilledNeeds.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No completed projects yet", color = Color.Gray)
                    Text("Check back after needs are fulfilled!", color = Color.LightGray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text("${fulfilledNeeds.size} Project${if (fulfilledNeeds.size != 1) "s" else ""} Completed", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                items(fulfilledNeeds) { need ->
                    GalleryCard(need = need)
                }
            }
        }
    }
}

@Composable
fun GalleryCard(need: Need) {
    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF4CAF50).copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Text("✅ COMPLETED", fontSize = 11.sp, color = Green700, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontWeight = FontWeight.Bold)
                }
            }
            Text(need.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)

            if (need.beforePhotoUrl.isNotEmpty() || need.afterPhotoUrl.isNotEmpty()) {
                Text("Before & After", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (need.beforePhotoUrl.isNotEmpty()) {
                        Column(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = need.beforePhotoUrl,
                                contentDescription = "Before",
                                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Text("Before", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    if (need.afterPhotoUrl.isNotEmpty()) {
                        Column(modifier = Modifier.weight(1f)) {
                            AsyncImage(
                                model = need.afterPhotoUrl,
                                contentDescription = "After",
                                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Text("After", fontSize = 11.sp, color = Green700, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Funded:", color = Color.Gray, fontSize = 13.sp)
                Text("₹${need.amountPledged.toLong()}", fontWeight = FontWeight.Bold, color = Green700, fontSize = 14.sp)
            }
        }
    }
}
