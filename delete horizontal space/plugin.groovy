import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.TextRange
import static liveplugin.PluginUtil.*
import java.util.regex.*

// With more complicated editing commands, you can either work
// directly on the IntelliJ document object, or convert to plain Java
// data and use built-in Groovy string handling.
//
// This example uses Java CharSequence.

def skipCharsForward(buf, point, skipChars) {
  for (int i = point; i < buf.length(); ++i) {
    if (0 > skipChars.indexOf((int)buf.charAt(i))) {
      return i
    }
  }
  return buf.length()
}

def skipPatternForward(document, pos, pattern) {
  def buf = document.getText(new TextRange(pos, document.textLength))
  Matcher match = buf =~ /^${pattern}+/
  return match.find() ? pos + match.end() : pos
}

def skipCharsBackward(buf, point, skipChars) {
  for (int i = point - 1; i >= 0; --i) {
    if (0 > skipChars.indexOf((int)buf.charAt(i))) {
      return i + 1
    }
  }
  return 0
}

def skipPatternBackward(document, pos, pattern) {
  def buf = document.getText(new TextRange(0, pos)).reverse()
  Matcher match = buf =~ /^${pattern}+/
  return match.find() ? pos - match.end() : pos
}

// See java.awt.event.KeyEvent for the VK_* key names available.
// The modifiers are shift, control, alt and meta.

registerAction("delete-horizontal-space", "alt BACK_SLASH") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      def point = caretModel.offset
      def buf = document.charsSequence
      document.deleteString(skipCharsBackward(buf, point, " \t"),
                            skipCharsForward(buf, point, " \t"))
    }
  }
}
