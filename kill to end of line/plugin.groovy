import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.Key

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

import static liveplugin.PluginUtil.*

// IntelliJ has a reasonable implementation of kill-line, but it's
// interesting to see how it would look as a plug-in. All of the
// complexity comes from having to listen for caret movement so we
// know whether to append to the cut buffer.

// kill-line and yank are compatible with other clipboard commands.

Key<Boolean> DISABLE_KILL_APPENDING_ADDED = Key.create("disableKillAppendingAdded");
Boolean appendNextKill = false

def disableKillAppending = new CaretListener() {
  void caretPositionChanged(CaretEvent caretEvent) { appendNextKill = false }
  void caretAdded(CaretEvent caretEvent) { appendNextKill = false }
  void caretRemoved(CaretEvent caretEvent) { appendNextKill = false }
}

def getPreviousKill() {
  return CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor)
}

registerAction("kill-line", "ctrl K") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      def curOffset = caretModel.offset
      def endOffset = document.getLineEndOffset(caretModel.logicalPosition.line)
      if (curOffset == endOffset) {
        endOffset += 1
        if (endOffset > document.textLength) {
          endOffset = document.textLength
        }
      }

      if (!getUserData(DISABLE_KILL_APPENDING_ADDED)) {
        caretModel.addCaretListener(disableKillAppending)
        putUserData(DISABLE_KILL_APPENDING_ADDED, true)
      }

      def killedText = document.getText(new TextRange(curOffset, endOffset))
      if (appendNextKill) {
        killedText = getPreviousKill() + killedText
      }

      CopyPasteManager.instance.contents = new StringSelection(killedText)
      appendNextKill = true

      document.deleteString(curOffset, endOffset)
    }
  }
}

registerAction("yank", "ctrl Y") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      def curOffset = caretModel.offset
      def killString = getPreviousKill()
      document.insertString(curOffset, killString)
      caretModel.moveToOffset(curOffset + killString.length())
    }
  }
  appendNextKill = false
}
