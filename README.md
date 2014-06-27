intellij-emacs
==============

Live-plugin macros for making IntelliJ more friendly to emacs users.

You must install `LivePlugin` before using these macros! It is available
from the IntelliJ plugin manager. See `https://github.com/dkandalov/live-plugin`
for more information.

I've used these macros on IntelliJ 13 ultimate and community editions
and on Android Studio. My main laptop is a Mac so I use the Mac key
bindings in IntelliJ, not the emacs ones (Mac default bindings are very
emacs friendly and I hate losing basic command key compatibility with
other apps). My primary emacs is currently Emacs 24 build and distributed
from `http://emacsformacosx.com/`.

These are not packaged and polished by any stretch of the imagination, but
they make switching back and forth between IntelliJ and emacs much more
pleasant for me.

`LivePlugin` had trouble running the macros directly out of a git
repo, so copy the macros into your `live-plugin` directory:

```
cp -r [a-z]* ~/'Library/Application Support/IntelliJIdea13/live-plugins'
```
