import com.intellij.openapi.actionSystem.AnActionEvent

import static liveplugin.PluginUtil.*

// The missing ^D to delete character at point was what sent me on
// my emacs plug-in extension binge. It's a very simple command that
// shows how the IntelliJ API works.
//
// The document roughly corresponds to an emacs buffer. The document
// manages a mutable Java CharSequence with a few simple methods for
// inserting, replacing and deleting text. The `runDocumentWriteAction`
// provides thread safety and undo.
//
// The caretModel.offset is equivalent to emacs point. It is an int
// that ranges from 0 to the bufer length (so watch for off-by-one
// when porting elisp!) The caretModel is automatically updated as
// changes are made to the document, but if you save the offset in a
// local var, you have to keep things consistent yourself.

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
