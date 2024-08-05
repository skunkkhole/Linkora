package com.sakethh.linkora.ui.commonComposables


import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme


data class AddNewFolderDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val newFolderData: (String, Long) -> Unit = { folderName, folderID -> },
    val onCreated: () -> Unit = {},
    val inAChildFolderScreen: Boolean,
    val onFolderCreateClick: (folderName: String, folderNote: String) -> Unit
)

@Composable
fun AddNewFolderDialogBox(
    addNewFolderDialogBoxParam: AddNewFolderDialogBoxParam
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val isFolderCreationInProgress = rememberSaveable {
        mutableStateOf(false)
    }
    if (addNewFolderDialogBoxParam.shouldDialogBoxAppear.value) {
        val folderNameTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        val noteTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        LinkoraTheme {
            AlertDialog(dismissButton = {
                if (!isFolderCreationInProgress.value) {
                    androidx.compose.material3.OutlinedButton(modifier = Modifier
                        .fillMaxWidth()
                        .pulsateEffect(),
                        onClick = {
                            addNewFolderDialogBoxParam.shouldDialogBoxAppear.value = false
                        }) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
            },
                confirmButton = {
                    if (!isFolderCreationInProgress.value) {
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(), onClick = {
                            isFolderCreationInProgress.value = true
                            if (folderNameTextFieldValue.value.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.folder_name_cannnot_be_empty),
                                    Toast.LENGTH_SHORT
                                ).show()
                                isFolderCreationInProgress.value = false
                            } else {
                                addNewFolderDialogBoxParam.onFolderCreateClick(
                                    folderNameTextFieldValue.value,
                                    noteTextFieldValue.value
                                )
                                addNewFolderDialogBoxParam.onCreated()
                                addNewFolderDialogBoxParam.shouldDialogBoxAppear.value =
                                    false
                                isFolderCreationInProgress.value = false
                            }
                        }) {
                            Text(
                                text = stringResource(id = R.string.create),
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                modifier = Modifier
                    .animateContentSize()
                    .wrapContentHeight(),
                onDismissRequest = {
                    if (!isFolderCreationInProgress.value) {
                        addNewFolderDialogBoxParam.shouldDialogBoxAppear.value = false
                    }
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        OutlinedTextField(readOnly = isFolderCreationInProgress.value,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            label = {
                                Text(
                                    text = stringResource(id = R.string.folder_name),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            singleLine = true,
                            value = folderNameTextFieldValue.value,
                            onValueChange = {
                                folderNameTextFieldValue.value = it
                            })
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(readOnly = isFolderCreationInProgress.value,
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    text = stringResource(id = R.string.note_for_creating_the_folder),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            value = noteTextFieldValue.value,
                            onValueChange = {
                                noteTextFieldValue.value = it
                            })
                        if (isFolderCreationInProgress.value) {
                            Spacer(modifier = Modifier.height(40.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                },
                title = {
                    Text(
                        text = if (addNewFolderDialogBoxParam.inAChildFolderScreen) stringResource(
                            id = R.string.create_a_new_folder_in
                        ) + " \"${CollectionsScreenVM.currentClickedFolderData.value.folderName}\"" else stringResource(
                            id = R.string.create_a_new_folder
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    )
                })
        }
    }
}