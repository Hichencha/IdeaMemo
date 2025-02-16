package com.ldlywt.note.ui.page.tag

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ldlywt.note.component.NoteCard
import com.ldlywt.note.component.NoteCardFrom
import com.ldlywt.note.component.RYScaffold
import com.ldlywt.note.ui.page.LocalMemosViewModel

@Composable
fun LocationDetailPage(location: String, navController: NavHostController) {
    val noteViewModel = LocalMemosViewModel.current
    val list by noteViewModel.getNotesByLocationInfo(location).collectAsState(initial = emptyList())

    RYScaffold(title = location, navController = navController) {
        LazyColumn {
            items(count = list.size, key = { it }) { index ->
                NoteCard(noteShowBean = list[index], navController, from = NoteCardFrom.TAG_DETAIL)
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

}