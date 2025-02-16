package com.ldlywt.note.ui.page.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.ldlywt.note.ui.page.LocalMemosViewModel
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi

@OptIn(UnstableSaltApi::class)
@Composable
fun TagDetailPage(tag: String, navController: NavHostController) {
    val noteViewModel = LocalMemosViewModel.current
    val tagList by noteViewModel.getNoteListByTagFlow(tag).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SaltTheme.colors.background)
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        TitleBar(
            onBack = {
                navController.popBackStack()
            },
            text = tag
        )

        LazyColumn {
            items(count = tagList.size, key = { it }) { index ->
                NoteCard(noteShowBean = tagList[index], navController, from = NoteCardFrom.TAG_DETAIL)
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

}