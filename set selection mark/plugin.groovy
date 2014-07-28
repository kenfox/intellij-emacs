import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.Key

import java.awt.datatransfer.StringSelection

import static liveplugin.PluginUtil.*

// Using a RangeMarker for the mark will dynamically update the mark
// position when affected by edits. For example, inserting text before
// a mark will move the mark forward. Nice!

Key<RangeMarker> MARK = Key.create("mark")

// This does not show the active region like on emacs because IntelliJ
// clears the selection after every command. I tried using a caret listener
// to dynamically update the region during simple navigation, but the
// selection was cleared after the listener ran.

registerAction("set-mark-command", "ctrl SPACE") { AnActionEvent event ->
  currentEditorIn(event.project).with {
    def point = caretModel.offset

    putUserData(MARK, document.createRangeMarker(point, point, true))

    selectionModel.removeSelection()
    show("mark set")
  }
}

registerAction("exchange-point-and-mark", "ctrl X, ctrl X") { AnActionEvent event ->
  currentEditorIn(event.project).with {
    def mark = getUserData(MARK)
    def point = caretModel.offset

    putUserData(MARK, document.createRangeMarker(point, point, true))

    if (mark) {
      caretModel.moveToOffset(mark.startOffset)
      selectionModel.setSelection(mark.startOffset, point)
      scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
    }
    else {
      selectionModel.removeSelection()
    }
  }
}

// Killing the region works even when the region is not selected.
// The killed text goes onto the clipboard so it's compatible with
// other IntelliJ clipboard functions. If there is an active
// selection, the selection will be killed instead of the region.
//
// TODO: use a selection listener to keep the mark in sync

registerAction("kill-region", "ctrl W") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      def mark = getUserData(MARK)
      if (selectionModel.hasSelection()) {
        killRegion(document, selectionModel.selectionStart, selectionModel.selectionEnd)
      }
      else if (mark) {
        killRegion(document, mark.startOffset, caretModel.offset)
      }
    }
  }
}

def killRegion(document, begin, end) {
  if (begin > end) {
    (begin, end) = [end, begin]
  }

  def killedText = document.getText(new TextRange(begin, end))
  CopyPasteManager.instance.contents = new StringSelection(killedText)

  document.deleteString(begin, end)
}

// It's not possible to rebind the general IntelliJ escape. If that
// becomes possible in a future release, more comfortable and complete
// emacs bindings will be possible. Compounding this limitation is the
// fact that the Mac option key doesn't register several key combos due
// to the default Keymap having "dead" keys. Ukelele can be used to
// create a new Keymap without any dead keys--I copied the default and
// then redefined all option key combos to just return the normal key.
// That also makes it easy to see when a key is mapped in IntelliJ
// since it will just insert the key when not mapped.

registerAction("keyboard-quit", "ctrl G") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      selectionModel.removeSelection()
    }
  }
}
