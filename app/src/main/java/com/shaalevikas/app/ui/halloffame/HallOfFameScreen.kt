package com.shaalevikas.app.ui.halloffame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shaalevikas.app.data.model.BadgeTier
import com.shaalevikas.app.data.model.User
import com.shaalevikas.app.ui.theme.Amber700
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HallOfFameScreen(onBack: () -> Unit) {
    val viewModel: UserViewModel = viewModel()
    val leaderboard by viewModel.leaderboard.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hall of Fame", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Amber700.copy(alpha = 0.12f))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Amber700, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Alumni Heroes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Amber700)
                            Text("Recognizing those who made a difference", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
            itemsIndexed(leaderboard) { index, user ->
                LeaderboardCard(rank = index + 1, user = user)
            }
            if (leaderboard.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No donors yet. Be the first!", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardCard(rank: Int, user: User) {
    val rankColor = when (rank) { 1 -> Color(0xFFFFD700); 2 -> Color(0xFFC0C0C0); 3 -> Color(0xFFCD7F32); else -> Color.Gray }
    val badgeColor = when (user.badgeTier) { BadgeTier.PLATINUM -> Color(0xFF9C27B0); BadgeTier.GOLD -> Color(0xFFFFD700); BadgeTier.SILVER -> Color(0xFFC0C0C0); BadgeTier.BRONZE -> Color(0xFFCD7F32) }
    val badgeEmoji = when (user.badgeTier) { BadgeTier.PLATINUM -> "💎"; BadgeTier.GOLD -> "🥇"; BadgeTier.SILVER -> "🥈"; BadgeTier.BRONZE -> "🥉" }

    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(if (rank <= 3) 4.dp else 1.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).background(rankColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("#$rank", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = rankColor)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name.ifEmpty { "Anonymous" }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(badgeEmoji, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(user.badgeTier.name, fontSize = 12.sp, color = badgeColor, fontWeight = FontWeight.Medium)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${user.totalPledged.toLong()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Green700)
                Text("pledged", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
