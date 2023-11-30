package com.lollipop.json.panel

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command

@Composable
fun InputPanel(
    builder: CodeBuilder,
    sendCommand: (Command, String) -> Unit
) {
    val booleanTabList = listOf("true", "false")
    var inputText by remember { mutableStateOf("") }
    val tabList = remember { mutableStateListOf<Command>() }
    var currentCommand by remember { mutableStateOf<Command>(Command.Json) }

    bindTabList(tabList, builder)

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp, 0.dp)
        ) {
            tabList.forEach { cmd ->
                Box(
                    modifier = Modifier.padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            inputText = ""
                            currentCommand = cmd
                        }
                    ) {
                        Text(
                            fontSize = 14.sp,
                            text = cmd.describe,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            when (currentCommand) {
                is Command.Bool -> {
                    RadioPanel(
                        currentCommand.describe,
                        booleanTabList,
                        textProvider = { inputText },
                        onTextChanged = { inputText = it }
                    )
                }

                is Command.Enum -> {
                    val cmd = currentCommand as Command.Enum
                    RadioPanel(
                        currentCommand.describe,
                        cmd.itemList,
                        textProvider = { inputText },
                        onTextChanged = { inputText = it }
                    )
                }

                is Command.Custom,
                Command.Curl,
                Command.Json -> {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        label = {
                            Text(currentCommand.describe)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    text = {
                        Text("发送")
                    },
                    onClick = {
                        sendCommand(currentCommand, inputText)
                    }
                )
            }
        }
    }
}

@Composable
private fun RadioPanel(
    label: String,
    tabs: List<String>,
    textProvider: () -> String,
    onTextChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp, 6.dp)
            .border(width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(5.dp)),
    ) {
        Text(
            label,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 6.dp, 6.dp)
        )
        Row(
            Modifier.fillMaxWidth()
                .fillMaxHeight()
                .horizontalScroll(rememberScrollState())
        ) {
            tabs.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = textProvider() == it,
                        onClick = {
                            onTextChanged(it)
                        }
                    )
                    Text(it)
                }
            }
        }
    }
}

private fun bindTabList(list: SnapshotStateList<Command>, builder: CodeBuilder) {
    list.clear()
    list.add(Command.Curl)
    list.add(Command.Json)
    list.addAll(builder.getCommandList())
}
