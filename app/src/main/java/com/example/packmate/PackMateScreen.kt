package com.example.packmate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackMateScreen(viewModel: PackViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("PackMate", style = MaterialTheme.typography.headlineSmall)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.tripType.value,
                    onValueChange = { viewModel.tripType.value = it },
                    label = { Text("Where are you going? (e.g. Beach vacation)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { viewModel.generatePackingList() },
                    enabled = !viewModel.isLoading.value,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Generate")
                }

                if (viewModel.isLoading.value) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (viewModel.errorMessage.value.isNotBlank()) {
                    Text(
                        "Error: ${viewModel.errorMessage.value}",
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (viewModel.itemList.isNotEmpty()) {
                    Text(
                        "Here's what you should pack:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.itemList) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "• $item",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
