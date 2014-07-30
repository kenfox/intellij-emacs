import com.intellij.openapi.actionSystem.AnActionEvent
import static liveplugin.PluginUtil.*
import java.util.regex.*

def formatList(def str) {
  "\t" + str.split(/,/).collect {
    it.trim().replaceAll(/['"]/, "'")
  }.join(",\n\t")
}

registerAction("format-requirejs-imports", "alt EQUALS") { AnActionEvent event ->
  runDocumentWriteAction(event.project) {
    currentEditorIn(event.project).with {
      Matcher match = document.text =~ /^(?s)(\s*define\s*\(\s*\[(.*?)\]\s*,\s*function\s*\((.*?)\)\s*\{\s*)/

      if (match.find()) {
        def files = formatList(match.group(2))
        def symbols = formatList(match.group(3))
        def header = """define([
${files}
], function(
${symbols}
) {
\t"""
        document.replaceString(0, match.end(), header)
      }
    }
  }
}
