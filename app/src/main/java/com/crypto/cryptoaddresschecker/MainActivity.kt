package com.crypto.cryptoaddresschecker

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crypto.cryptoaddresschecker.ui.theme.CryptoAddressCheckerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            if (checkPermissions(context)) {
                Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission(context)
            }

            CryptoAddressCheckerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navController.NavigationComponent(
                        navController
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            val cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraaccepted) {
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denined \n You cannot use app without providing permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        ActivityCompat.finishAffinity(this)
    }
}

@Composable
fun NavHostController.NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable("Home"){
            HomeUI()
        }

        composable("Scanner/{Scancoin}"){
            val scannedCoin = it.arguments?.getString("Scancoin")

            if (scannedCoin != null) {
                ScannerScreen(scannedCoin = scannedCoin)
            }
        }
    }
}

fun checkPermissions(context: Context): Boolean {
    val camera_permission = ContextCompat.checkSelfPermission(context, CAMERA)
    return camera_permission == PackageManager.PERMISSION_GRANTED
}


fun requestPermission(context: Context) {
    val PERMISSION_REQUEST_CODE = 200
    ActivityCompat.requestPermissions(context as Activity, arrayOf(CAMERA), PERMISSION_REQUEST_CODE)
}