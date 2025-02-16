package com.ldlywt.note.ui.page.data

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ldlywt.note.R
import com.ldlywt.note.component.RYOutlineTextField
import com.ldlywt.note.component.RYScaffold
import com.ldlywt.note.preferences
import com.ldlywt.note.utils.str

@Composable
fun DataCloudConfigPage(
    navController: NavHostController
) {
    var serverUrl by rememberSaveable { mutableStateOf(preferences.davServerUrl) }
    var username by rememberSaveable { mutableStateOf(preferences.davUserName) }
    var password by rememberSaveable { mutableStateOf(preferences.davPassword) }
    val snackbarState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val dataManagerViewMode: DataManagerViewModel = hiltViewModel()

    RYScaffold(
        title = R.string.cloud_data_manager.str,
        navController = navController,
        snackBarHostState = snackbarState,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp),
        ) {
            RYOutlineTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = stringResource(R.string.server_url),
                placeholder = "https://dav.jianguoyun.com/dav/",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Computer,
                        contentDescription = R.string.server_url.str
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            RYOutlineTextField(
                value = username,
                onValueChange = { username = it },
                label = stringResource(R.string.username),
                placeholder = stringResource(R.string.username),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = R.string.username.str
                    )
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            RYOutlineTextField(
                value = password,
                onValueChange = { password = it },
                isPassword = true,
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.password),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Password,
                        contentDescription = R.string.password.str
                    )
                },
            )
            Spacer(modifier = Modifier.height(60.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
                enabled = !serverUrl.isNullOrBlank() && !username.isNullOrBlank() && !password.isNullOrBlank(),
                onClick = {
                    focusManager.clearFocus()


                }
            ) {
                Text(stringResource(R.string.submit))
            }
        }

    }
}