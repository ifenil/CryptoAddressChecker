package com.crypto.cryptoaddresschecker

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crypto.cryptoaddresschecker.ui.theme.CryptoAddressCheckerTheme


interface MainActions {
    fun onScanClicked()
    fun onBackClicked()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var code by remember { mutableStateOf("") }
            var hasReadCode by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraProviderFeature = remember { ProcessCameraProvider.getInstance(context) }

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
                            override fun onScanClicked() {

                            }

                            override fun onBackClicked() {
                                restartApp()
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

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        ActivityCompat.finishAffinity(this)
    }
}

@Composable
fun HomeUI(
    actions: MainActions
){
    var code by remember { mutableStateOf("") }
    var hasReadCode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFeature = remember { ProcessCameraProvider.getInstance(context) }
    val resultText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        if (checkPermissions(context)) {
            if (hasReadCode) {
                resultText.value = code
                BackHandler {
                    actions.onBackClicked()
                }
            } else {
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(
                                Size(
                                    previewView.width,
                                    previewView.height
                                )
                            )
                            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QRCode { result ->
                                code = result
                                hasReadCode = true
                            }
                        )
                        try {
                            cameraProviderFeature.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        previewView
                    },
                    modifier = Modifier.border(10.dp, color = Color(0xFFD6A2E8))
                )
            }
        }

        Text(
            text = "BTC",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .clickable { actions.onScanClicked() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ETH",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .clickable { actions.onScanClicked() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = resultText.value,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .background(Color.DarkGray)
                .padding(horizontal = 10.dp, vertical = 5.dp)
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