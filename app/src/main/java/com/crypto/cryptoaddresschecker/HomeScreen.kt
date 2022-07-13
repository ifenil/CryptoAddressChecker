package com.crypto.cryptoaddresschecker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NavController.HomeUI(){
    val scanCoin = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BTC",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .clickable {
                    scanCoin.value = "BTC"
                    navigate("Scanner/${scanCoin.value}")
                }
                .padding(horizontal = 50.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ETH",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .clickable {
                    scanCoin.value = "ETH"
                    navigate("Scanner/${scanCoin.value}")
                }
                .padding(horizontal = 50.dp, vertical = 10.dp)
        )
    }
}