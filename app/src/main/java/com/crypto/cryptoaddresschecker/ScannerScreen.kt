package com.crypto.cryptoaddresschecker

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun NavController.ScannerScreen(
    scannedCoin: String
){
    var code by remember { mutableStateOf("") }
    var hasReadCode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFeature = remember { ProcessCameraProvider.getInstance(context) }
    val resultText = remember { mutableStateOf("") }
    val isValid = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasReadCode) {
            resultText.value = code
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
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
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
                modifier = Modifier.fillMaxSize()
            )
        }

        if (resultText.value.isNotEmpty()) {
            Text(
                text = "Validate",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.DarkGray)
                    .clickable {
                        if (scannedCoin == "BTC" &&
                            scannedCoin.length <= 34 &&
                            scannedCoin.length >= 25 &&
                            scannedCoin[0] == '1' &&
                            !scannedCoin.contentEquals("0") &&
                            !scannedCoin.contentEquals("O") &&
                            !scannedCoin.contentEquals("l") &&
                            !scannedCoin.contentEquals("I")
                        ) {
                            isValid.value = "this BTC address is valid"
                        } else if (
                            scannedCoin == "ETH" &&
                            scannedCoin[0] == '0' &&
                            scannedCoin[1] == 'x' &&
                            scannedCoin.contains("[0-9]+") &&
                            scannedCoin.contains("[a-f]+")
                        ) {
                            isValid.value = "this ETH address is valid"
                        } else {
                            isValid.value = "$code this address is not valid"
                        }
                    }
                    .padding(horizontal = 50.dp, vertical =10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Share",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.DarkGray)
                    .clickable {

                    }
                    .padding(horizontal = 50.dp, vertical =10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isValid.value.isNotEmpty()){
            Text(
                text = isValid.value,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.DarkGray)
                    .padding(horizontal = 50.dp, vertical =10.dp)
            )
        }
    }
}