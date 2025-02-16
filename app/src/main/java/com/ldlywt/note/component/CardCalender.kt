package com.ldlywt.note.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ldlywt.note.bean.NoteShowBean
import com.ldlywt.note.ui.page.router.Screen
import com.ldlywt.note.utils.toMinute
import com.moriafly.salt.ui.SaltTheme
import dev.jeziellago.compose.markdowntext.MarkdownText


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CardCalender(
    noteShowBean: NoteShowBean, navHostController: NavHostController, modifier: Modifier = Modifier
) {

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val note = noteShowBean.note
    val tags = noteShowBean.tagList


    Card(
        colors = CardDefaults.cardColors(containerColor = SaltTheme.colors.subBackground),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    navHostController.navigate(route = Screen.InputDetail(noteShowBean.note.noteId))
                },
                onLongClick = {
                    openBottomSheet = true
                },
            ),
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = note.createTime.toMinute(),
                style = SaltTheme.textStyles.paragraph.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (!note.noteTitle.isNullOrEmpty()) {
                Text(
                    text = note.noteTitle ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            MarkdownText(markdown = note.content, style = SaltTheme.textStyles.paragraph.copy(fontSize = 15.sp, lineHeight = 24.sp), onTagClick = {
                navHostController.navigate(Screen.TagDetail(it))
            })
            if (note.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                ImageCard(note, navHostController)
            }
            Spacer(modifier = Modifier.height(8.dp))
            showLocationInfoContent(note)
//            val filterTagList = tags.filterNot { it.tag.isBlank() || it.isCityTag }
//            if (filterTagList.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(4.dp))
//                LazyRow {
//                    filterTagList.forEachIndexed { index, tag ->
//                        item(tag.tag) {
//                            val startPadding = if (index == 0) 0.dp else 6.dp
//                            Text(tag.tag,
//                                style = MaterialTheme.typography.labelMedium,
//                                color = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier
//                                    .padding(horizontal = startPadding)
//                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
//                                    .clickable {
//                                        navHostController.navigate("${RouteName.TAG_DETAIL}/${tag.tag}")
//                                    }
//                            )
//                        }
//                    }
//                }
//            }
        }
    }

    ActionBottomSheet(navHostController, noteShowBean = noteShowBean, show = openBottomSheet) {
        openBottomSheet = false
    }

}