package com.crypto.cryptoaddresschecker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter


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
    val canShare = remember { mutableStateOf(false) }

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
                        verifyAddress(code, scannedCoin, canShare, isValid)
                    }
                    .padding(horizontal = 50.dp, vertical = 10.dp)
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
                        verifyAddress(code, scannedCoin, canShare, isValid)
                        val bitmap = encodeAsBitmap(code)
                        if (bitmap != null && canShare.value) {
                            sharePalette(bitmap, context)
                        }
                    }
                    .padding(horizontal = 50.dp, vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isValid.value.isNotEmpty()){
            Text(
                text = isValid.value,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
                    .padding(horizontal = 50.dp, vertical = 10.dp)
            )
        }
    }
}

private fun verifyAddress(
    code: String,
    scannedCoin: String,
    canShare: MutableState<Boolean>,
    isValid: MutableState<String>
){
    if (scannedCoin == "BTC" &&
        code.length <= 34 &&
        code.length >= 25 &&
        code[0] == '1' &&
        !code.contentEquals("0") &&
        !code.contentEquals("O") &&
        !code.contentEquals("l") &&
        !code.contentEquals("I")
    ) {
        canShare.value = true
        isValid.value = "✔️\nBTC address is valid"
    } else if (
        scannedCoin == "ETH" &&
        code[0] == '0' &&
        code[1] == 'x' &&
        code.contains(Regex("[0-9]+")) &&
        code.contains(Regex("[a-f]"))
    ) {
        canShare.value = true
        isValid.value = "✔️\nETH address is valid"
    } else {
        isValid.value = "❌ \ncrypto address is not valid"
    }
}

@Throws(WriterException::class)
private fun encodeAsBitmap(str: String?): Bitmap? {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 250, 250)
    val w = bitMatrix.width
    val h = bitMatrix.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        for (x in 0 until w) {
            pixels[y * w + x] = if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    return bitmap
}


private fun sharePalette(bitmap: Bitmap, context: Context) {
    val bitmapPath = MediaStore.Images.Media.insertImage(
        context.contentResolver,
        bitmap,
        "Juno",
        "QR Code"
    )
    val bitmapUri: Uri = Uri.parse(bitmapPath)
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/png"
    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
    context.startActivity(Intent.createChooser(intent, "QR Code"))
}