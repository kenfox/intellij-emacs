import com.intellij.openapi.actionSystem.AnActionEvent

import static liveplugin.PluginUtil.*

// The missing ^D to delete character at point was what sent me on
// my emacs plug-in extension binge. It's a very simple command that
// shows how the IntelliJ API works.

registerAction("delete-char", "ctrl D") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      def offset = caretModel.offset
      if (offset < document.textLength) {
        document.deleteString(offset, offset + 1)
      }
    }
  }
}
