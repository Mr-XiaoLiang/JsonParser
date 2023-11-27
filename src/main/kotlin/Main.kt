import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

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
        Row {

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
