import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.JsonParser
import com.lollipop.json.builder.KotlinDataClassBuilder
import com.lollipop.json.panel.InputPanel
import com.lollipop.json.panel.OutLog
import com.lollipop.json.panel.OutPanel

@Composable
@Preview
fun App() {

    var codeBuilder by remember { mutableStateOf<CodeBuilder>(KotlinDataClassBuilder) }

    val logcatList = remember { mutableStateListOf<OutLog>() }

    val inputWeight = 0.35F

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(inputWeight),
                backgroundColor = Color(0xFFEEEEEE),
                elevation = 5.dp,
                shape = RoundedCornerShape(0.dp),
            ) {
                InputPanel(codeBuilder) { cmd, value ->
                    when (cmd) {
                        Command.Json -> {
                            logcatList.add(OutLog(value))
                            val bean = codeBuilder.build(JsonParser.parse(value))
                            logcatList.add(OutLog(bean))
                        }

                        Command.Curl -> {
                            logcatList.add(OutLog(value))
                            logcatList.add(OutLog("暂不支持CURL"))
                        }

                        Command.Profile -> {
                            // 这个情况直接忽略
                        }

                        else -> {
                            logcatList.add(OutLog("${cmd::class.simpleName} : ${cmd.describe} -> $value"))
                            codeBuilder.onCommand(cmd, value)
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(1 - inputWeight),
                backgroundColor = Color(0xFFEEEEEE),
                elevation = 10.dp,
                shape = RoundedCornerShape(0.dp),
            ) {
                OutPanel(logcatList)
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
