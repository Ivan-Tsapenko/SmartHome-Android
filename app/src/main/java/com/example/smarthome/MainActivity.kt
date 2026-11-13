package com.example.smarthome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF101827)
private val Panel = Color(0xFF1B2738)
private val Mint = Color(0xFF55D6BE)
private val Muted = Color(0xFF9AA8B8)

data class SmartDevice(
    val name: String,
    val room: String,
    val icon: ImageVector,
    var enabled: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SmartHomeApp() }
    }
}

@Composable
fun SmartHomeApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var temperature by remember { mutableFloatStateOf(22f) }
    var devices by remember {
        mutableStateOf(listOf(
            SmartDevice("Потолочный свет", "Гостиная", Icons.Default.Lightbulb, true),
            SmartDevice("Умная розетка", "Гостиная", Icons.Default.Power, false),
            SmartDevice("Кондиционер", "Спальня", Icons.Default.AcUnit, true),
            SmartDevice("Увлажнитель", "Детская", Icons.Default.WaterDrop, false)
        ))
    }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Navy, surface = Panel, primary = Mint,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Navy,
            bottomBar = {
                NavigationBar(containerColor = Panel) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, null) }, label = { Text("Главная") })
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Devices, null) }, label = { Text("Устройства") })
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Tune, null) }, label = { Text("Сценарии") })
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("SMART HOME", color = Mint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(if (selectedTab == 0) "Мой дом" else listOf("", "Устройства", "Сценарии")[selectedTab],
                            fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.NotificationsNone, null, tint = Color.White)
                }
                Spacer(Modifier.height(18.dp))
                when (selectedTab) {
                    0 -> HomeScreen(devices, temperature, { temperature = it },
                        { index, value -> devices = devices.toMutableList().also { it[index] = it[index].copy(enabled = value) } })
                    1 -> DeviceScreen(devices, { index, value ->
                        devices = devices.toMutableList().also { it[index] = it[index].copy(enabled = value) }
                    })
                    else -> SceneScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(devices: List<SmartDevice>, temperature: Float, onTemperature: (Float) -> Unit,
               onToggle: (Int, Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoCard("Температура", "22°", Icons.Default.Thermostat, Modifier.weight(1f))
        InfoCard("Влажность", "45%", Icons.Default.WaterDrop, Modifier.weight(1f))
    }
    Spacer(Modifier.height(18.dp))
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text("Климат в доме", color = Muted)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${temperature.toInt()}°", fontSize = 42.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))
                Icon(Icons.Default.AcUnit, null, tint = Mint, modifier = Modifier.size(34.dp))
            }
            Slider(value = temperature, onValueChange = onTemperature, valueRange = 16f..30f)
            Text("Целевая температура", color = Muted, fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(18.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Быстрое управление", fontSize = 19.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f))
        Text("${devices.count { it.enabled }} активно", color = Mint, fontSize = 12.sp)
    }
    Spacer(Modifier.height(10.dp))
    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().heightIn(min = 260.dp, max = 420.dp)) {
        items(devices) { device ->
            val index = devices.indexOf(device)
            DeviceCard(device, { onToggle(index, it) })
        }
    }
}

@Composable
fun DeviceScreen(devices: List<SmartDevice>, onToggle: (Int, Boolean) -> Unit) {
    Text("Все подключённые устройства", color = Muted)
    Spacer(Modifier.height(12.dp))
    devices.forEachIndexed { index, device ->
        Card(Modifier.fillMaxWidth().padding(bottom = 10.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(device.icon, null, tint = Mint, modifier = Modifier.size(28.dp))
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(device.name, fontWeight = FontWeight.SemiBold)
                    Text(device.room, color = Muted, fontSize = 12.sp)
                }
                Switch(checked = device.enabled, onCheckedChange = { onToggle(index, it) })
            }
        }
    }
}

@Composable
fun SceneScreen() {
    Text("Управляйте домом одним нажатием", color = Muted)
    Spacer(Modifier.height(16.dp))
    listOf(
        Triple("Я дома", "Комфортное освещение и климат", Icons.Default.Home),
        Triple("Ночной режим", "Выключить свет и снизить температуру", Icons.Default.DarkMode),
        Triple("Я ушёл", "Отключить выбранные приборы", Icons.Default.DirectionsWalk)
    ).forEach { (title, subtitle, icon) ->
        Card(Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Mint, modifier = Modifier.size(30.dp))
                Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Muted, fontSize = 12.sp)
                }
                FilledTonalButton(onClick = { }) { Text("Запустить") }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Mint)
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun DeviceCard(device: SmartDevice, onToggle: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = if (device.enabled) Color(0xFF233A43) else Panel),
        shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(device.icon, null, tint = Mint)
                Spacer(Modifier.weight(1f))
                Switch(checked = device.enabled, onCheckedChange = onToggle)
            }
            Spacer(Modifier.height(14.dp))
            Text(device.name, fontWeight = FontWeight.SemiBold)
            Text(if (device.enabled) "Включено" else "Выключено", color = Muted, fontSize = 12.sp)
        }
    }
}
