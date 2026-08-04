package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.RadioAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RadioViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val radioViewModel: RadioViewModel = viewModel()
        RadioAppScreen(
            viewModel = radioViewModel,
            modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
