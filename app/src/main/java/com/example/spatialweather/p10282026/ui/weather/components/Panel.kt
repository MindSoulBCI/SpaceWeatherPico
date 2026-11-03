package com.example.spatialweather.p10282026.ui.weather.components

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialweather.p10282026.R
import com.example.spatialweather.p10282026.domain.model.*
import com.example.spatialweather.p10282026.ui.navigation.Windows

@Composable fun Heading(text: String) { Text(text,style=PicoTheme.typography.titleLarge,color=PicoTheme.colorScheme.labelPrimary) }
@Composable fun Copy(text: String) { Text(text,color=PicoTheme.colorScheme.labelPrimary) }
@Composable fun Panel(modifier: Modifier, content: @Composable ColumnScope.()->Unit) {
 Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp),content=content)
}
