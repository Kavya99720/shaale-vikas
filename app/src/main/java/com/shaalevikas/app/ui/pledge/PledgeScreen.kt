package com.shaalevikas.app.ui.pledge

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
import com.shaalevikas.app.ui.theme.Green700
import com.shaalevikas.app.viewmodel.NeedsViewModel
import com.shaalevikas.app.viewmodel.PledgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PledgeScreen(needId: String, onSuccess: () -> Unit, onBack: () -> Unit) {
    val pledgeViewModel: PledgeViewModel = viewModel()
    val needsViewModel: NeedsViewModel = viewModel()
    val need by needsViewModel.selectedNeed.collectAsState()
    val isLoading by pledgeViewModel.isLoading.collectAsState()
    val error by pledgeViewModel.error.collectAsState()
    val success by pledgeViewModel.success.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(needId) { needsViewModel.loadNeed(needId) }

    LaunchedEffect(success) {
        if (success) { showSuccessDialog = true }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Thank You! 🎉") },
            text = { Text("Your pledge of ₹${amount} has been recorded. Your contribution brings the school one step closer to its goal!") },
            confirmButton = {
                Button(onClick = { pledgeViewModel.clearSuccess(); onSuccess() }, colors = ButtonDefaults.buttonColors(containerColor = Green700)) {
                    Text("Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pledge Support", color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            need?.let { n ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("You are pledging for:", color = Color.Gray, fontSize = 13.sp)
                        Text(n.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Green700)
                        Spacer(Modifier.height(4.dp))
                        Text("Target: ₹${n.costEstimate.toLong()}", fontSize = 13.sp, color = Color.Gray)
                        Text("Already Pledged: ₹${n.amountPledged.toLong()}", fontSize = 13.sp, color = Green700)
                    }
                }
            }

            Text("Your Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            OutlinedTextField(
                value = name, onValueChange = { name = it; pledgeViewModel.clearError() },
                label = { Text("Full Name *") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Phone (Optional)") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = amount, onValueChange = { amount = it; pledgeViewModel.clearError() },
                label = { Text("Pledge Amount (₹) *") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("500", "1000", "2000", "5000").forEach { preset ->
                    OutlinedButton(onClick = { amount = preset }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("₹$preset", fontSize = 13.sp)
                    }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }

            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFFFFA000), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("This is a simulated pledge — no real money is transferred. Your commitment helps the school plan and procure resources.", fontSize = 12.sp, color = Color(0xFF6D4C41))
                }
            }

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (name.isBlank()) pledgeViewModel.clearError()
                    else if (amt <= 0) pledgeViewModel.clearError()
                    else pledgeViewModel.submitPledge(needId, name.trim(), phone.trim(), amt)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && name.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Green700),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else { Icon(Icons.Default.Favorite, null); Spacer(Modifier.width(8.dp)); Text("Confirm Pledge", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }
}
