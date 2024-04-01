package com.engineerfred.kotlin.next.presentation.screens.addFeeling

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.theme.TangerineOrange
import com.engineerfred.kotlin.next.utils.feelings

@Composable
fun AddFeelingScreen(
    onBackClicked: () -> Unit,
    onFeelingSelected: (String) -> Unit,
    viewModel: AddFeelingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    isDarkTheme: Boolean
) {

    val uiState = viewModel.uiState.collectAsState().value
    SetSystemBarColor( color = MaterialTheme.colorScheme.primary)

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                onBackClicked.invoke()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "How are you feeling?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            feelings.forEach {
                Row(
                    modifier = Modifier
                        .clickable {
                            viewModel.onEvent( AddFeelingUiEvent.FeelingSelected(it.desc) )
                            onFeelingSelected.invoke(it.desc)
                        }
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEmotions,
                        contentDescription = null,
                        tint = TangerineOrange
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = it.desc)
                }
                Spacer(modifier = Modifier.size(5.dp))
                Divider()
                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }

}