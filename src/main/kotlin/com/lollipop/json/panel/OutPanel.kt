package com.lollipop.json.panel

import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OutPanel(logList: SnapshotStateList<LogInfo>) {
    LazyColumn {
        items(logList) { log ->
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .border(
                        width = 1.dp,
                        color = when (log) {
                            is LogInfo.Input -> MaterialTheme.colors.secondary
                            is LogInfo.Output -> MaterialTheme.colors.onBackground
                        },
                        shape = RoundedCornerShape(5.dp)
                    ),
            ) {
                ContextMenuDataProvider(
                    items = {
                        listOf(ContextMenuItem("Copy All") {
                            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                            clipboard.setContents(StringSelection(log.log.value), null)
                        })
                    }
                ) {
                    SelectionContainer {
                        Text(
                            text = log.log.value,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

sealed class LogInfo(
    val log: MutableState<String>
) {

    class Input(log: MutableState<String>) : LogInfo(log)
    class Output(log: MutableState<String>) : LogInfo(log)

    companion object {
        fun input(value: String): Input {
            return Input(mutableStateOf(value))
        }

        fun output(value: String): Output {
            return Output(mutableStateOf(value))
        }
    }

}
