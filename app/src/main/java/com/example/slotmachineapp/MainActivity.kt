package com.example.slotmachineapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.slotmachineapp.ui.theme.SlotMachineAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items





// MainActivity class, the entry point of the Android application
class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var coins: MutableState<Int>
    private lateinit var equippedBackgroundId: MutableState<Int>
    private lateinit var purchasedBackgrounds: MutableState<Set<String>>

    // Callback function to update the equipped background ID
    private val onBackgroundEquipped: (Int) -> Unit = { newBackgroundId ->
        equippedBackgroundId.value = newBackgroundId
    }


    // onCreate function called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences for storing data
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        // Initialize mutable state for coins count
        coins = mutableIntStateOf(sharedPreferences.getInt("coins", 100))
        // Initialize mutable state for equipped background ID
        equippedBackgroundId = mutableIntStateOf(sharedPreferences.getInt("equipped_background_id", R.drawable.man))
        // Initialize mutable state for purchased backgrounds
        purchasedBackgrounds = mutableStateOf(
            sharedPreferences.getStringSet("purchased_backgrounds", setOf()) ?: setOf()
        )

        // Set the content of the activity using Compose
        setContent {
            // Initialize NavController for navigating between screens
            val navController = rememberNavController()
            // Set the theme for the app
            SlotMachineAppTheme {
                // Navigation host for managing navigation between screens
                NavHost(navController = navController, startDestination = "starter") {
                    // Define composable for the starter screen
                    composable("starter") {
                        // Call the StarterScreen composable
                        StarterScreen(
                            onPlayClicked = { navController.navigate("game") },
                            onShopClicked = { navController.navigate("Shop") }
                        )
                    }
                    // Define composable for the game screen
                    composable("game") {
                        // Call the SlotMachineApp composable
                        SlotMachineApp(navController = navController, coins = coins, equippedBackgroundId = equippedBackgroundId)
                    }
                    // Define composable for the shop screen
                    composable("Shop") {
                        // Call the ShopScreen composable
                        ShopScreen(
                            coins = coins,
                            onBackClicked = { navController.popBackStack() },
                            equippedBackgroundId = equippedBackgroundId,
                            purchasedBackgrounds = purchasedBackgrounds,
                            onBackgroundEquipped = onBackgroundEquipped,
                            sharedPreferences = sharedPreferences // Pass sharedPreferences here

                        )
                    }

                }
            }
        }
    }

    // onStop function called when the activity is stopped
    override fun onStop() {
        super.onStop()
        // Save the current coins count and equipped background ID to SharedPreferences
        sharedPreferences.edit().apply {
            putInt("coins", coins.value)
            putInt("equipped_background_id", equippedBackgroundId.value)
            putStringSet("purchased_backgrounds", purchasedBackgrounds.value)
            apply()
        }
    }

}


// Composable function for the starter screen
@Composable
fun StarterScreen(
    onPlayClicked: () -> Unit,
    onShopClicked: () -> Unit
) {
    // Box composable to hold the background image and content
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.startscreen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom button for playing the slot machine
            CustomButton(
                text = "Play Slot Machine",
                onClick = onPlayClicked,
                modifier = Modifier.padding(bottom = 16.dp),
                backgroundColor = Color(0xFF4CAF50) // Custom background color
            )

            // Custom button for accessing the shop
            CustomButton(
                text = "Shop",
                onClick = onShopClicked,
                backgroundColor = Color(0xFF2196F3) // Custom background color
            )
        }
    }
}

// Custom button composable
@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color = Color.White
) {
    // Button composable with custom styling
    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .background(backgroundColor)
            .border(BorderStroke(1.dp, Color.Black)),
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 18.sp)
        )
    }
}


// Composable function for the shop screen

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ShopScreen(
    coins: MutableState<Int>,
    onBackClicked: () -> Unit,
    equippedBackgroundId: MutableState<Int>,
    purchasedBackgrounds: MutableState<Set<String>>,
    onBackgroundEquipped: (Int) -> Unit,
    sharedPreferences: SharedPreferences
) {
    val purchasedFromPrefs = sharedPreferences.getStringSet("purchased_backgrounds", emptySet()) ?: emptySet()
    val equippedFromPrefs = sharedPreferences.getInt("equipped_background_id", 0)

    // Ensure equippedBackgroundId is initialized correctly
    equippedBackgroundId.value = equippedFromPrefs

    // Initialize backgrounds with purchase and equipped state
    val backgrounds = listOf(
        BackgroundItemModel("Background 1", 10, R.drawable.man),
        BackgroundItemModel("Background 2", 100, R.drawable.background1),
        BackgroundItemModel("Background 3", 100, R.drawable.ship),
        BackgroundItemModel("Background 4", 100, R.drawable._k_pc_wallpapers_160_bc317),
    ).map { bg ->
        bg.copy(
            purchased = purchasedFromPrefs.contains(bg.name),
            equipped = bg.imageResource == equippedFromPrefs
        )
    }

    // Convert purchasedBackgrounds to MutableSet<String>
    val mutablePurchasedBackgrounds = remember { mutableStateOf(purchasedFromPrefs.toMutableSet()) }

    // Ensure equippedBackgroundId is initialized correctly
    equippedBackgroundId.value = equippedFromPrefs

    // LazyColumn composable to hold the content of the shop screen
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Display available coins count
        item {
            Text(
                text = "Available Coins: ${coins.value}",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Display each background item available in the shop
        // Items loop to display backgrounds
        items(backgrounds) { background ->
            // Change the click logic to check and update purchases properly
            ShopBackgroundItem(
                background = background,
                coins = coins,
                equippedBackgroundId = equippedBackgroundId,
                purchasedBackgrounds = mutablePurchasedBackgrounds,
                onBackgroundEquipped = onBackgroundEquipped,
                sharedPreferences = sharedPreferences
            )
        }

        // Button to navigate back to the game screen
        item {
            Button(onClick = onBackClicked, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Game")
            }
        }
    }

    // Update the list of purchased backgrounds when recomposed
    LaunchedEffect(Unit) {
        purchasedBackgrounds.value = purchasedFromPrefs
    }
}






// Data class representing a background item in the shop
data class BackgroundItemModel(
    val name: String,
    val price: Int,
    val imageResource: Int,
    var purchased: Boolean = false,
    var equipped: Boolean = false
)


// Composable function for rendering a background item in the shop
@Composable
fun ShopBackgroundItem(
    background: BackgroundItemModel,
    coins: MutableState<Int>,
    equippedBackgroundId: MutableState<Int>,
    purchasedBackgrounds: MutableState<MutableSet<String>>,
    onBackgroundEquipped: (Int) -> Unit,
    sharedPreferences: SharedPreferences
) {
    val isPurchased = remember(purchasedBackgrounds.value) {
        mutableStateOf(purchasedBackgrounds.value.contains(background.name))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            // Update purchasing logic
            if (!isPurchased.value) {
                if (coins.value >= background.price) {
                    coins.value -= background.price
                    purchasedBackgrounds.value.add(background.name)
                    isPurchased.value = true  // Ensure UI updates to reflect the purchase
                    savePurchasesToPreferences(purchasedBackgrounds.value, sharedPreferences)
                }
            }
            // Equip background without extra charges
            equipBackground(background, equippedBackgroundId, onBackgroundEquipped, sharedPreferences)
        }
    ) {
        // Display background image
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray)
        ) {
            Image(
                painter = painterResource(id = background.imageResource),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Display background item name and price
        Column {
            Text(
                text = "${background.name} - Price: ${background.price} coins",
                style = TextStyle(fontSize = 18.sp)
            )
            // Display "Purchased" tag for purchased backgrounds
            if (isPurchased.value) {
                Text(
                    text = "Purchased",
                    style = TextStyle(fontSize = 14.sp, color = Color.Green),
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
        }
    }
}



// Function to equip a background
private fun equipBackground(
    background: BackgroundItemModel,
    equippedBackgroundId: MutableState<Int>,
    onBackgroundEquipped: (Int) -> Unit,
    sharedPreferences: SharedPreferences
) {
    // Only change equipped background if it's different from the current one
    if (equippedBackgroundId.value != background.imageResource) {
        background.equipped = true
        // Update the equipped background ID
        equippedBackgroundId.value = background.imageResource
        onBackgroundEquipped(background.imageResource)

        // Save the equipped background ID to SharedPreferences
        sharedPreferences.edit().putInt("equipped_background_id", background.imageResource).apply()
    }
}

private fun savePurchasesToPreferences(purchased: Set<String>, preferences: SharedPreferences) {
    Log.d("ShopScreen", "Saving purchases: $purchased")
    preferences.edit()
        .putStringSet("purchased_backgrounds", purchased)
        .apply()
}



// Composable function for the slot machine screen
@Composable
fun SlotMachineApp(
    navController: NavController,
    coins: MutableState<Int>,
    equippedBackgroundId: MutableState<Int>
) {

    // SnackbarHostState for displaying messages
    val snackbarHostState = remember { SnackbarHostState() }
    // Mutable state for displaying messages
    val message = remember { mutableStateOf("") }

    // Load the background image based on the equipped background ID
    val backgroundImage = painterResource(id = equippedBackgroundId.value)



    // Center the content vertically and horizontally
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Scale the image to fill the bounds
        )

        // Foreground content
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Slot machine content
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                SlotMachine(message, snackbarHostState, coins) // Remove sharedPreferences parameter
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // "Add Coins" button
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { coins.value += 10 }) {
                Text("Add Coins (+10)")
            }

            // Back to starter screen button
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Starter Screen")
            }
        }
    }
}




// Composable function for the slot machine content
@Composable
fun SlotMachine(
    message: MutableState<String>,
    snackbarHostState: SnackbarHostState,
    coins: MutableState<Int>
) {
    // Mutable state for the reels
    val reels = remember { List(5) { mutableStateListOf(Random.nextInt(0, 10)) } }

    // Mutable state for spinning state
    var spinning by remember { mutableStateOf(false) }
    // Coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    // Effect for spinning the reels
    LaunchedEffect(spinning) {
        if (spinning && coins.value >= 10) {
            coins.value -= 10 // Deduct 10 coins from the user's balance

            // Launch coroutines to spin each reel and collect results
            val results = mutableListOf<List<Int>>()
            repeat(5) { index ->
                coroutineScope.launch {
                    val reelResult = spinReel(reels[index])
                    results.add(reelResult)

                    if (results.size == 5) { // Check if all reels have stopped
                        val coinsAwarded = checkWinningCombination(results)

                        if (coinsAwarded > 0) {
                            val finalMessage = "You win $coinsAwarded coins!"
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(finalMessage)
                            }
                            coins.value += coinsAwarded // Update coins based on the results
                        }

                        spinning = false // Reset spinning to false
                    }
                }
            }
        } else if (spinning) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Not enough coins to spin!")
                spinning = false
            }
        }
    }

    // Column composable for the slot machine UI
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(50.dp))
        ReelsView(reels)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { spinning = true }) {
            Text("Spin (Costs 10 coins)")
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Display current coins count
        Text(
            text = "Coins: ${coins.value}",
            color = Color(0xFFFFD700), // Golden color
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
        // Display the message on the screen
        if (message.value.isNotBlank()) {
            Text(message.value)
        }
    }
}


// Function to check winning combinations
fun checkWinningCombination(reels: List<List<Int>>): Int {
    // Flatten the list of reels to make it easier to analyze
    val flattenedReels = reels.flatten()

    // Check if there are two groups of two identical numbers
    val groupedNumbers = flattenedReels.groupBy { it }
    val twoPairs = groupedNumbers.filterValues { it.size == 2 }
    val triplets = groupedNumbers.filterValues { it.size == 3 }


    // Check if there are exactly two pairs
    if (twoPairs.size == 2 && twoPairs.values.all { it.size == 2 }) {
        return 400 // Reward for the specific pattern
    }

    // Check if there are exactly one triplet and one pair
    if (triplets.size == 1 && twoPairs.size == 1) {
        return 1000 // Reward for the specific pattern
    }


    // Count the number of reels with the same number
    val uniqueNumbers = flattenedReels.distinct()
    val counts = uniqueNumbers.map { number ->
        flattenedReels.count { it == number }
    }

    // Check for winning combinations
    return when {
        counts.contains(5) -> 5000 // All reels have the same number
        counts.contains(4) -> 600 // Four reels have the same number
        counts.contains(3) -> 250 // Three reels have the same number
        else -> 0 // No winning combination
    }
}

// Function to simulate spinning a reel
suspend fun spinReel(reel: MutableList<Int>): List<Int> {
    // Simulate spinning animation
    repeat(20) {
        // Update the state of the reel to a random number
        reel.replaceAll { Random.nextInt(0, 10) }
        delay(60) // Controls the speed of the spinning
    }

    // Collect the result of the reel after spinning

    return reel.toList()
}

// Composable function to render the reels view
@Composable
fun ReelsView(reels: List<MutableList<Int>>) {
    // Row composable to hold the reels
    Row(modifier = Modifier.padding(horizontal = 32.dp)) {
        reels.forEach { reel ->
            Reel(reel)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

// Composable function to render a single reel
@Composable
fun Reel(reel: List<Int>) {
    val number = reel.firstOrNull() ?: 0 // Get the first number from the reel, default to 0 if the reel is empty

    // Replace numbers with appropriate image resources
    val imagePainter: Painter = when (number) {
        0 -> painterResource(id = R.drawable.apple)
        1 -> painterResource(id = R.drawable.banana_jpg)
        2 -> painterResource(id = R.drawable.cherry)
        3 -> painterResource(id = R.drawable.grapess)
        4 -> painterResource(id = R.drawable.clover)
        5 -> painterResource(id = R.drawable.diamond)
        6 -> painterResource(id = R.drawable.lemon)
        7 -> painterResource(id = R.drawable.seven)
        8 -> painterResource(id = R.drawable.orange)
        9 -> painterResource(id = R.drawable.watermelon_jpg)
        else -> painterResource(id = R.drawable.lemon)
    }

    // Box composable to hold the reel item
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.DarkGray)
            .width(50.dp)
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Image composable to display the reel item
        Image(
            painter = imagePainter,
            contentDescription = null
        )
    }
}

// Preview function for the slot machine app
@Preview(showBackground = true)
@Composable
fun SlotMachineAppPreview() {
    // Mock NavController for preview
    val navController = rememberNavController()
    // Mock coins state for preview
    val coins = remember { mutableIntStateOf(100) }
    // Mock equipped background ID state for preview
    val equippedBackgroundId = remember { mutableIntStateOf(R.drawable.man) }
    // Call the SlotMachineApp composable with mock data
    SlotMachineApp(navController = navController, coins = coins, equippedBackgroundId = equippedBackgroundId)
}
