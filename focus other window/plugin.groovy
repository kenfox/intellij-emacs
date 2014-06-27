import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx

import java.awt.Point

import static liveplugin.PluginUtil.*

// I highly recommend binding IntelliJ split window horizontally to ^X 2.
// The other-window command then gives you basic emacs window navigation.

registerAction("other-window", "ctrl X, O") { AnActionEvent event ->
  def editorManager = FileEditorManagerEx.getInstanceEx(event.project)
  if (editorManager.currentWindow && editorManager.windows.size() > 1) {
    def windowIndex = editorManager.windows.findIndexOf {
      it == editorManager.currentWindow
    }

    def otherWindowIndex = windowIndex + 1
    if (otherWindowIndex >= editorManager.windows.size()) {
      otherWindowIndex = 0
    }

    def otherWindow = editorManager.windows[otherWindowIndex]
    otherWindow.setAsCurrentWindow(true)
  }
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
    }
    else {
      def distToScroll = distFromCenter + (distFromCenter < -0.2 ? -0.5 : 0.3)
      def newCenterY = distToScroll * windowHeight + centerY
      def newCenter = xyToLogicalPosition(new Point(0, (int)newCenterY))

      scrollingModel.scrollTo(newCenter, ScrollType.CENTER)
    }
  }
}
