import androidx.compose.ui.window.Window
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.movisens.bluetooth.kmm.App
import com.movisens.bluetooth.kmm.di.initKoin
import platform.AppKit.NSApp
import platform.AppKit.NSApplication
import platform.Foundation.NSLog

fun main() {
    Logger.addLogWriter(logWriter = arrayOf(object : LogWriter() {
        override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
            NSLog(message)
        }
    }))
    initKoin()
    NSApplication.sharedApplication()
    Window {
        App()
    }
    NSApp?.run()
}