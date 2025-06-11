package com.example.myapplication.ui.components

import android.R
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.GrayFont
import com.example.myapplication.ui.theme.IconColor
import com.example.myapplication.ui.theme.MainButton
import com.example.myapplication.ui.theme.SoftBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,

){
    //Tạo biến trạng thái cho ô tìm kiếm
    var searchText by remember { mutableStateOf("") }
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SoftBlue
        ),
        title = {
            SearchTopBarContent(

                searchText = searchText,
                onSearchTextChanged = { searchText = it }
            )
        },

        actions = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    .size(30.dp),
                tint = IconColor
            )
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    .size(30.dp),
                tint = IconColor
            )

        }



    )
}