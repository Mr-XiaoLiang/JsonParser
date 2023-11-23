import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.json.JsonParser

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

//fun main() = application {
//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "JsonParser"
//    ) {
//        App()
//    }
//}

fun main() {

    /*
            """
        {
            "name": "AAA",
            "age": 1,
            "man": true,
            "body": {
                "length": 1.8,
                "like": [
                    "AAA",
                    12,
                    {
                        "AA": ""
                    }
                ]
            }
        }
    """
     */

    val infos = JsonParser.parse(
        """
        {
            "name": "AAA",
            "age": 1,
            "man": true,
            "body": {
                "length": 1.8,
                "like": [
                    "AAA",
                    12,
                    {
                        "AA": ""
                    }
                ]
            }
        }
    """
    )
    infos.forEach {
        println(it.toString())
    }
}
