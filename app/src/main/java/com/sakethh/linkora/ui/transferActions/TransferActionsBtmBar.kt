package com.sakethh.linkora.ui.transferActions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM

@Composable
fun TransferActionsBtmBar() {
    val transferActionsBtmBarVM: TransferActionsBtmBarVM = hiltViewModel()
    BottomAppBar {
        IconButton(onClick = {
            TransferActionsBtmBarVM.reset()
        }) {
            Icon(Icons.Default.Cancel, null)
        }
        Column {
            Text(
                text = TransferActionsBtmBarValues.currentTransferActionType.value.name.replace(
                    "_",
                    " "
                )
                    .split(" ").map { it[0] + it.substring(1).lowercase() }.joinToString { it }
                    .replace(",", ""),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 12.sp
            )
            Text(
                text = TransferActionsBtmBarValues.sourceFolders.toList()
                    .joinToString { it.toString() },
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(end = 100.dp)
                    .horizontalScroll(rememberScrollState()),
                maxLines = 1
            )
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Row {
                IconButton(onClick = {
                    when (TransferActionsBtmBarValues.currentTransferActionType.value) {
                        TransferActionType.COPYING_OF_FOLDERS -> TODO()
                        TransferActionType.MOVING_OF_FOLDERS -> {
                            transferActionsBtmBarVM.changeTheParentIdOfASpecificFolder(
                                sourceFolderIds = TransferActionsBtmBarValues.sourceFolders.map { it.id },
                                targetParentId = CollectionsScreenVM.currentClickedFolderData.value.id
                            )
                        }

                        TransferActionType.COPYING_OF_LINKS -> TODO()
                        TransferActionType.MOVING_OF_LINKS -> TODO()
                        TransferActionType.NOTHING -> TODO()
                    }
                    TransferActionsBtmBarVM.reset()
                }) {
                    Icon(Icons.Default.ContentPaste, null)
                }
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}