package com.ldlywt.note.ui.page.input

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.ldlywt.note.R
import com.ldlywt.note.component.showLocationInfoContent
import com.ldlywt.note.utils.lunchMain
import com.ldlywt.note.ui.page.router.LocalRootNavController
import com.ldlywt.note.utils.handlePickFiles
import com.ldlywt.note.utils.str
import com.ldlywt.note.ui.page.LocalMemosState
import com.ldlywt.note.ui.page.LocalMemosViewModel
import com.ldlywt.note.ui.page.LocalTags
import com.moriafly.salt.ui.SaltTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoInputPage(
    memoId: Long,
    viewModel: MemoInputViewModel = hiltViewModel(),
) {
    val noteState = LocalMemosState.current
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val navController = LocalRootNavController.current
    val memosViewModel = LocalMemosViewModel.current
    val memo = remember { noteState.notes.find { it.note.noteId == memoId } }
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(memo?.note?.content ?: "", TextRange(memo?.note?.content?.length ?: 0)))
    }
    val tagList = LocalTags.current.filterNot { it.isCityTag }
    var tagMenuExpanded by remember { mutableStateOf(false) }
    var photoImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val localDate = Instant.ofEpochMilli(memo?.note?.createTime ?: System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
    val focusManager = LocalFocusManager.current

    fun uploadImage(uri: Uri) = coroutineScope.launch {
        lunchMain {
            handlePickFiles(setOf(uri)) {
                viewModel.uploadAttachments.addAll(it)
            }
        }
    }

    val pickImage = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { uploadImage(it) }
    }

    val takePhoto = rememberLauncherForActivityResult(TakePicture()) { success ->
        if (success) {
            photoImageUri?.let { uploadImage(it) }
        }
    }

    fun submit() = coroutineScope.launch {
        focusRequester.freeFocus()
        focusManager.clearFocus()
        memo?.note?.apply {
            this.content = text.text
            this.updateTime = System.currentTimeMillis()
            this.attachments = viewModel.uploadAttachments
            memosViewModel.insertOrUpdate(this)
        }
        navController.popBackStack()
    }

    Scaffold(modifier = Modifier.imePadding(), topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SaltTheme.colors.background),
            title = {
                Text(R.string.edit.str, color = SaltTheme.colors.text)
            },
            navigationIcon = {
                IconButton(onClick = {
                    focusManager.clearFocus()
                    focusRequester.freeFocus()
                    navController.popBackStack()
                }) {
                    Icon(Icons.Filled.Close, contentDescription = R.string.close.str, tint = SaltTheme.colors.text)
                }
            },
        )
    }, bottomBar = {
        BottomAppBar(containerColor = SaltTheme.colors.background) {
            if (tagList.isEmpty()) {
                IconButton(onClick = {
                    text = text.copy(
                        text.text.replaceRange(text.selection.min, text.selection.max, "#"), TextRange(text.selection.min + 1)
                    )
                }) {
                    Icon(Icons.Outlined.Tag, contentDescription = R.string.tag.str, tint = SaltTheme.colors.text)
                }
            } else {
                Box(modifier = Modifier.background(SaltTheme.colors.background)) {
                    DropdownMenu(
                        modifier = Modifier.background(SaltTheme.colors.background),
                        expanded = tagMenuExpanded, onDismissRequest = { tagMenuExpanded = false }, properties = PopupProperties(focusable = false)
                    ) {
                        tagList.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag.tag, color = SaltTheme.colors.text) },
                                onClick = {
                                    val tagText = "${tag} "
                                    text = text.copy(
                                        text.text.replaceRange(
                                            text.selection.min, text.selection.max, tagText
                                        ), TextRange(text.selection.min + tagText.length)
                                    )
                                    tagMenuExpanded = false
                                },
                            )
                        }
                    }
                    IconButton(onClick = { tagMenuExpanded = !tagMenuExpanded }) {
                        Icon(Icons.Outlined.Tag, contentDescription = R.string.tag.str, tint = SaltTheme.colors.text)
                    }
                }
            }

            IconButton(onClick = {
                pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }) {
                Icon(Icons.Outlined.Image, contentDescription = R.string.add_image.str, tint = SaltTheme.colors.text)
            }

            IconButton(onClick = {
                try {
                    val imagesFolder = File(context.cacheDir, "capture_picture")
                    if (!imagesFolder.exists()) {
                        imagesFolder.mkdirs()
                    }
                    val file = File.createTempFile("capture_picture_", ".jpg", imagesFolder)
                    val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                    photoImageUri = uri
                    takePhoto.launch(uri)
                } catch (e: ActivityNotFoundException) {
                    coroutineScope.launch {
                        snackbarState.showSnackbar(
                            e.localizedMessage ?: "Unable to take picture."
                        )
                    }
                }
            }) {
                Icon(
                    Icons.Outlined.PhotoCamera, contentDescription = R.string.take_photo.str, tint = SaltTheme.colors.text
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(enabled = text.text.isNotEmpty(), onClick = { submit() }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = R.string.send.str, tint = SaltTheme.colors.text)
            }
        }
    }, snackbarHost = {
        SnackbarHost(hostState = snackbarState)
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .background(SaltTheme.colors.background)
        ) {
            TextFieldDefaults.colors(

            )
            val customTextFieldColors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent, // 聚焦时的边框颜色
                unfocusedContainerColor = Color.Transparent, // 未聚焦时的边框颜色
                focusedTextColor = SaltTheme.colors.text,
                unfocusedTextColor = SaltTheme.colors.text
                // 其他属性根据需要设置
            )
            Row(modifier = Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = localDate.dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold).copy(color = SaltTheme.colors.text)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = localDate.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        ), style = MaterialTheme.typography.bodySmall.copy(color = SaltTheme.colors.text), fontSize = 11.sp
                    )

                    Text(
                        text = localDate.year.toString() + "/" + localDate.month.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        ), style = MaterialTheme.typography.bodySmall.copy(color = SaltTheme.colors.text), fontSize = 10.sp
                    )

                }
            }

            showLocationInfoContent(modifier = Modifier.padding(start = 14.dp), note = memo?.note)

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = SaltTheme.textStyles.paragraph,
                value = text,
                colors = customTextFieldColors, // 应用自定义颜色
                onValueChange = { it: TextFieldValue ->
                    text = it
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )

            if (viewModel.uploadAttachments.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .height(80.dp)
                        .padding(start = 15.dp, end = 15.dp, bottom = 15.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.uploadAttachments.toList(), { it.path }) { resource ->
                        InputImage(attachment = resource) {
                            viewModel.deleteResource(it)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        when {
            memo != null -> {
                memo.note.attachments.let { resourceList ->
                    viewModel.uploadAttachments.addAll(
                        resourceList
                    )
                }
            }
        }
        delay(300)
        focusRequester.requestFocus()
    }
}