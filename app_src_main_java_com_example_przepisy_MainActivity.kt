package com.example.przepisy

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.przepisy.ui.theme.PrzepisyTheme

data class Przepis(
    val tytul: String,
    val skladniki: String,
    val opis: String,
    val zdjecieUri: String?
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrzepisyTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PrzepisyScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrzepisyScreen() {
    var przepisy by remember { mutableStateOf(listOf<Przepis>()) }
    var tytul by remember { mutableStateOf("") }
    var skladniki by remember { mutableStateOf("") }
    var opis by remember { mutableStateOf("") }
    var zdjecieUri by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        zdjecieUri = uri?.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Twoje przepisy") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)) {

            Text("Nowy przepis", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tytul,
                onValueChange = { tytul = it },
                label = { Text("Tytuł") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = skladniki,
                onValueChange = { skladniki = it },
                label = { Text("Składniki") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = opis,
                onValueChange = { opis = it },
                label = { Text("Opis przygotowania") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                Text("Dodaj zdjęcie")
            }
            if (zdjecieUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(zdjecieUri),
                    contentDescription = "Zdjęcie",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    przepisy = przepisy + Przepis(tytul, skladniki, opis, zdjecieUri)
                    tytul = ""
                    skladniki = ""
                    opis = ""
                    zdjecieUri = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj przepis")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Lista przepisów", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(przepisy.size) { index ->
                    val przepis = przepisy[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(przepis.tytul, style = MaterialTheme.typography.titleMedium)
                            if (przepis.zdjecieUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(przepis.zdjecieUri),
                                    contentDescription = "Przepis zdjęcie",
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .size(80.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                )
                            }
                            Text("Składniki: ${przepis.skladniki}")
                            Text("Opis: ${przepis.opis}")
                        }
                    }
                }
            }
        }
    }
}