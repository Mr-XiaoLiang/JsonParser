import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.json.Command

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("") }
    val tabList = remember { mutableStateListOf<Command>() }
    var currentCommand by remember { mutableStateOf<Command>(Command.Json) }

    tabList.add(Command.Curl)
    tabList.add(Command.Json)

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3F),
                backgroundColor = Color(0xFFEEEEEE),
                elevation = 5.dp,
                shape = RoundedCornerShape(0.dp),
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(54.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(4.dp).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                fontSize = 16.sp,
                                text = currentCommand.describe,
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1F)
                                .fillMaxHeight()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            tabList.forEach { cmd ->
                                Box(
                                    modifier = Modifier.padding(4.dp).fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    OutlinedButton(
                                        onClick = {
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
                    }

                    BasicTextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.Transparent),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7F),
                backgroundColor = Color(0xFFEEEEEE),
                elevation = 10.dp,
                shape = RoundedCornerShape(0.dp),
            ) {

                Column {
                    Button(onClick = {
                        text = "Hello, Desktop!"
                    }) {
                        Text(text)
                    }
                }

            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "JsonParser"
    ) {
        window
        App()
    }
}

//fun main() {
//    val infos = JsonParser.parse(
//        """
//        {
//            "name": "AAA",
//            "age": 1,
//            "man": true,
//            "body": {
//                "length": 1.8,
//                "like": [
//                    "AAA",
//                    12,
//                    {
//                        "AA": ""
//                    }
//                ]
//            }
//        }
//    """
//    )
//    println(KotlinDataClassBuilder.build(infos))
//}
