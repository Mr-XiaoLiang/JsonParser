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
import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.JsonParser
import com.lollipop.json.ShellCommandHelper
import com.lollipop.json.builder.JavaBeanBuilder
import com.lollipop.json.builder.KotlinDataClassBuilder
import com.lollipop.json.panel.InputPanel
import com.lollipop.json.panel.LogInfo
import com.lollipop.json.panel.OutPanel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App() {

    var codeBuilder by remember { mutableStateOf<CodeBuilder>(KotlinDataClassBuilder) }

    val logcatList = remember { mutableStateListOf<LogInfo>() }

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
                            logcatList.add(LogInfo.input(value))
                            GlobalScope.launch {
                                val bean = codeBuilder.build(JsonParser.parse(value))
                                logcatList.add(LogInfo.output(bean))
                            }
                        }

                        Command.Curl -> {
                            logcatList.add(LogInfo.input(value))
                            val errorLog = LogInfo.output("")
                            val resultLog = LogInfo.output("")
                            GlobalScope.launch {
                                ShellCommandHelper.exec(value) { value, error ->
                                    if (error) {
                                        errorLog.log.value += value

                                    } else {
                                        resultLog.log.value += value
                                    }
                                }
                                val bean = codeBuilder.build(JsonParser.parse(resultLog.log.value))
                                logcatList.add(LogInfo.output(bean))
                            }
                            logcatList.add(errorLog)
                            logcatList.add(resultLog)
                        }

                        Command.Profile -> {
                            // 这个情况直接忽略
                        }

                        else -> {
                            logcatList.add(LogInfo.output("${cmd::class.simpleName} : ${cmd.describe} -> $value"))
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


//fun main() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "JsonParser"
//    ) {
//        window
//        App()
//    }
//}

fun main() {
    GlobalScope.launch {
        val json = "{\"AA\":\"aa\",\"BB\":12,\"CC\":1.0,\"DD\":true,\"EE\":{\"FF\":34,\"GG\":false}}"
        val result = JavaBeanBuilder.build(JsonParser.parse(json))
        println(result)
    }
    while (true) {
    }
}
