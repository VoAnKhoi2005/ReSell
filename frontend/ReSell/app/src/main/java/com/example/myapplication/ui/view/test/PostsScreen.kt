package com.example.myapplication.ui.view.test

import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myapplication.ui.model.Post


@Composable
internal fun PostsScreen() {
    val viewModel: PostViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    PostsContent(state = state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsContent(
    state: PostViewState
) {
    LoadingDialog(isLoading = state.isLoading)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalItemSpacing = 10.dp
        ) {
            items(state.posts) { post ->
                ProductCard(post = post)
            }
        }
    }
}
@Composable
fun ProductCard(modifier: Modifier = Modifier, post: Post) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {

            Spacer(modifier = Modifier.height(5.dp))

        }
    }
}
@Composable
fun LoadingDialog(isLoading: Boolean){
    if(isLoading){
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnClickOutside = false)
        ){
            Box(modifier = Modifier
                .width(200.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(modifier = Modifier.padding(10.dp))
            }
        }
    }
}

