package com.ldlywt.note.ui.page.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ldlywt.note.R
import com.ldlywt.note.bean.Note
import com.ldlywt.note.component.DraggableCard
import com.ldlywt.note.component.EmptyComponent
import com.ldlywt.note.component.ImageCard
import com.ldlywt.note.component.locationAndTimeText
import com.ldlywt.note.component.showLocationInfoContent
import com.ldlywt.note.ui.page.router.Screen
import com.ldlywt.note.utils.orFalse
import com.ldlywt.note.utils.toTime
import com.ldlywt.note.ui.page.LocalMemosState
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.util.Random


@OptIn(ExperimentalMaterial3Api::class, UnstableSaltApi::class)
@Composable
fun ExplorePage(
    navHostController: NavHostController
) {
    val noteState = LocalMemosState.current
    val shuffledList = noteState.notes.shuffled().map { it.note }.map { it.copy() }.take(20).toMutableList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SaltTheme.colors.background)
            .padding(top = 30.dp)
    ) {
        TitleBar(
            onBack = {
                navHostController.popBackStack()
            },
            text = stringResource(R.string.random_walk)
        )

        ExploreList(memos = shuffledList, navHostController, onItemClick = { index ->
            navHostController.navigate(route = Screen.InputDetail(shuffledList[index].noteId))
        })
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreList(
    memos: MutableList<Note>, navHostController: NavHostController, onItemClick: (index: Int) -> Unit
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val cardHeight = screenHeight - 200.dp
    val listEmpty = remember { mutableStateOf(false) }
    if (listEmpty.value) {
        EmptyComponent()
    }
    memos.forEachIndexed { index, note ->
        DraggableCard(
            item = note,
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .padding(
                    top = 16.dp + (index + 2).dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            onSwiped = { _, note ->
                if (memos.isNotEmpty().orFalse()) {
                    memos.remove(note)
                    if (memos.isEmpty().orFalse()) {
                        listEmpty.value = true
                    }
                }
            }
        ) {
            ExploreMemoCard(note, index, navHostController, onItemClick = {

            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreMemoCard(
    note: Note, page: Int, navHostController: NavHostController, onItemClick: (index: Int) -> Unit
) {

    val rnd = Random()
    val color = android.graphics.Color.argb(60, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(color))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // 顶部Spacer
        note.noteTitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        MarkdownText(markdown = note.content, style = SaltTheme.textStyles.paragraph.copy(fontSize = 15.sp, lineHeight = 24.sp), onTagClick = {
            navHostController.navigate(Screen.TagDetail(it))
        })
        Spacer(modifier = Modifier.height(12.dp))
        if (note.attachments.isNotEmpty()) {
            ImageCard(note, navHostController)
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        locationAndTimeText(note.createTime.toTime(), modifier = Modifier.padding(start = 2.dp))
        showLocationInfoContent(note)
    }
}