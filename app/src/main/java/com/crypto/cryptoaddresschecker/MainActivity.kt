package com.crypto.cryptoaddresschecker

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crypto.cryptoaddresschecker.ui.theme.CryptoAddressCheckerTheme


interface MainActions {
    fun onBTCClicked()
    fun onETHClicked()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                    HomeUI(
                        actions = object : MainActions{
                            override fun onBTCClicked() {

                            }

                            override fun onETHClicked() {
                                TODO("Not yet implemented")
                            }

                        }
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
}

@Composable
fun HomeUI(
    actions: MainActions
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Text(
            text = "BTC",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .clickable { actions.onBTCClicked() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "BTC",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .clickable { actions.onETHClicked() }
        )
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