//Fernando Ruiz
//Carné 23065
//Lab 4 Android

package com.example.lab4_android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lab4_android.ui.theme.Lab4_AndroidTheme
import java.net.URL

data class Recipe(val name: String, val imageUrl: String)


// Configuración de la actividad principal usando setcontent
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4_AndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecipeApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Composable RecipeApp que contiene la interfaz de usuario
@Composable
fun RecipeApp(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var imageUrl by remember { mutableStateOf(TextFieldValue("")) }
    var recipes by remember { mutableStateOf(listOf<Recipe>()) }
    var showList by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Columna principal que contiene los campos de entrada y el botón
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre de la receta") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("URL de la imagen") },
            modifier = Modifier.fillMaxWidth()
        )
        // Botón para agregar la receta
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                errorMessage = null
                if (name.text.isNotEmpty() && imageUrl.text.isNotEmpty()) {
                    try {
                        // Verifica si la URL es válida
                        URL(imageUrl.text)
                        recipes = recipes + Recipe(name.text, imageUrl.text)
                        name = TextFieldValue("")
                        imageUrl = TextFieldValue("")
                    } catch (e: Exception) {
                        errorMessage = "URL inválida"
                        Log.e("RecipeApp", "Invalid URL: ${imageUrl.text}", e)
                    }
                } else {
                    errorMessage = "Por favor ingresa un nombre y una URL válida"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar a la lista")
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = {
                showList = true
                Log.d("RecipeApp", "Recipes: $recipes")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mostrar lista")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (showList) {
            RecipeList(recipes)
        }
    }
}

// Composable RecipeList que muestra una lista de recetas
@Composable
fun RecipeList(recipes: List<Recipe>) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeCard(recipe)
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.imageUrl)
                        .crossfade(true)
                        .error(android.R.drawable.ic_menu_report_image) // Imagen de error
                        .build(),
                    onError = { error ->
                        Log.e("RecipeCard", "Error loading image: ${recipe.imageUrl}", error.result.throwable)
                    }
                ),
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = recipe.name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeAppPreview() {
    Lab4_AndroidTheme {
        RecipeApp()
    }
}
