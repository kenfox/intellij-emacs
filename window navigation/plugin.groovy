import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx

import java.awt.Point

import static liveplugin.PluginUtil.*

// Basic commands to manage split windows and navigate between them.
// IntelliJ can do most of this out of the box, but since it has a
// completely different visual display model, it's useful to see how
// these commands can be implemented.
//
// There seems to be a bug in IntelliJ when deleting the 2nd from
// the top window--only the top window is left and all others are
// deleted.

registerAction("split-window-below", "ctrl X, 2") { AnActionEvent event ->
  def editorManager = FileEditorManagerEx.getInstanceEx(event.project)
  editorManager.createSplitter(0, editorManager.currentWindow)
}

private void withPreviousWindow(event, closure) {
  def editorManager = FileEditorManagerEx.getInstanceEx(event.project)
  if (editorManager.currentWindow && editorManager.windows.size() > 1) {
    def windows = editorManager.splitters.orderedWindows
    def windowIndex = windows.findIndexOf {
      it == editorManager.currentWindow
    }

    def otherWindowIndex = (windowIndex > 0) ? windowIndex - 1 : windows.size() - 1

    closure.call(editorManager, windows[otherWindowIndex])
  }
}

registerAction("other-window", "ctrl X, O") { AnActionEvent event ->
  withPreviousWindow(event) { editorManager, window ->
    window.setAsCurrentWindow(true)
  }
}

registerAction("delete-window", "ctrl X, 0") { AnActionEvent event ->
  withPreviousWindow(event) { editorManager, window ->
    window.unsplit(false)
  }
}

registerAction("delete-other-windows", "ctrl X, 1") { AnActionEvent event ->
  FileEditorManagerEx.getInstanceEx(event.project).unsplitAllWindow()
}

// IntelliJ has a center caret command, but the emacs feature is better.
// This is an approximation of emacs because there's no low-level scroll
// API exposed. There are global editor settings to limit scrolling, so
// this code may not work for everyone. It works well with my settings
// and line heights.

registerAction("recenter-top-bottom", "ctrl L") { AnActionEvent event ->
  currentEditorIn(event.project).with {
    def windowHeight = scrollingModel.visibleArea.height
    def centerY = scrollingModel.verticalScrollOffset + windowHeight / 2
    def caretPosition = visualPositionToXY(caretModel.visualPosition)
    def distFromCenter = (caretPosition.y - centerY) / windowHeight

    if (distFromCenter > 0.2 || distFromCenter < -0.5) {
      scrollingModel.scrollToCaret(ScrollType.CENTER)
    } else {
      def distToScroll = distFromCenter + (distFromCenter < -0.2 ? -0.5 : 0.3)
      def newCenterY = distToScroll * windowHeight + centerY
      def newCenter = xyToLogicalPosition(new Point(0, (int) newCenterY))

      scrollingModel.scrollTo(newCenter, ScrollType.CENTER)
    }
  }
}
