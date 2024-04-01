package com.engineerfred.kotlin.next.presentation.screens.addLocation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.SearchTextField
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.SteelBlue
import com.engineerfred.kotlin.next.utils.places

@Composable
fun AddLocationScreen(
    onBackClicked: () -> Unit,
    onLocationSelected: (String) -> Unit,
    viewModel: AddLocationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    isDarkTheme: Boolean
) {
    val uiState = viewModel.uiState.collectAsState().value

    val bgColor = if (isDarkTheme) Charcoal else CrimsonRed.copy(alpha = .6f)
    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

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
                text = "Add your location",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.size(8.dp))
        SearchTextField(
            text = { uiState.locationText },
            onTextChanged = {
                viewModel.onEvent( AddLocationUiEvent.LocationChanged(it) )
            },
            onDoneClicked = {
                if ( uiState.locationText.isNotEmpty() ) {
                    onLocationSelected.invoke( uiState.locationText )
                }
            }, onCloseClicked = {
                viewModel.onEvent(  AddLocationUiEvent.LocationChanged("") )
            }, modifier = Modifier.padding(horizontal = 10.dp),
            isDarkTheme = isDarkTheme
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = "Suggestions",
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            maxLines = 1
        )
        Spacer(modifier = Modifier.size(9.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            places.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent( AddLocationUiEvent.LocationChanged(it))
                            onLocationSelected.invoke(it)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (!isDarkTheme) CrimsonRed.copy(alpha = .5f) else Charcoal
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_add_location),
                            contentDescription = null,
                            tint = if ( isDarkTheme ) Color.White else SteelBlue
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = it)
                }
                Spacer(modifier = Modifier.size(15.dp))
            }
        }
    }
}