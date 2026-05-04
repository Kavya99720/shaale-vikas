package com.shaalevikas.app.ui.needs

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.NeedsViewModel
import com.shaalevikas.app.viewmodel.PledgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedDetailScreen(
    needId: String,
    onPledgeClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val needsViewModel: NeedsViewModel = viewModel()
    val pledgeViewModel: PledgeViewModel = viewModel()
    val need by needsViewModel.selectedNeed.collectAsState()
    val pledges by pledgeViewModel.pledges.collectAsState()
    val isLoading by needsViewModel.isLoading.collectAsState()

    LaunchedEffect(needId) {
        needsViewModel.loadNeed(needId)
        pledgeViewModel.loadPledgesForNeed(needId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(need?.title ?: "Need Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700)
            )
        },
        bottomBar = {
            need?.let { n ->
                if (n.progressPercent < 100f) {
                    Surface(shadowElevation = 8.dp) {
                        Button(
                            onClick = { onPledgeClick(needId) },
                            modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Green700),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Favorite, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Pledge Support", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Green700)
            }
        } else {
            need?.let { n ->
                Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                    if (n.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = n.photoUrl,
                            contentDescription = n.title,
                            modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(0.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = Green700.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                                Text(n.category, fontSize = 12.sp, color = Green700, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Medium)
                            }
                        }
                        Text(n.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(n.description, color = Color.Gray, fontSize = 15.sp, lineHeight = 22.sp)

                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Funding Progress", fontWeight = FontWeight.Bold, color = Green700)
                                    Text("${n.progressPercent.toInt()}%", fontWeight = FontWeight.Bold, color = Green700)
                                }
                                LinearProgressIndicator(
                                    progress = { n.progressPercent / 100f },
                                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                    color = Green700,
                                    trackColor = Color.White
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Pledged", color = Color.Gray, fontSize = 12.sp)
                                        Text("₹${n.amountPledged.toLong()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Green700)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Target", color = Color.Gray, fontSize = 12.sp)
                                        Text("₹${n.costEstimate.toLong()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                }
                            }
                        }

                        if (pledges.isNotEmpty()) {
                            Text("Supporters (${pledges.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            pledges.forEach { pledge ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, null, tint = Green700, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(pledge.name, fontSize = 14.sp)
                                    }
                                    Text("₹${pledge.amount.toLong()}", fontWeight = FontWeight.Medium, color = Green700, fontSize = 14.sp)
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
