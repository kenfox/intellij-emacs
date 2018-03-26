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
other apps). My primary emacs is currently Emacs 24 built and distributed
from `http://emacsformacosx.com/`.

These are not packaged and polished by any stretch of the imagination, but
they make switching back and forth between IntelliJ and emacs much more
pleasant for me.


Setup
-----

`LivePlugin` had trouble running the macros directly out of a git
repo, so copy the macros into your `live-plugin` directory:

### MacOSX

```
tar cf - */*.groovy | (cd ~/'Library/Application Support/IntelliJIdea13/live-plugins'; tar xf -)
```

### GNU/Linux


```
tar cf - */*.groovy | (cd ~/".IdeaIC2017.3/config/plugins/live-plugins"; tar xf -)
```

### Windows

I'm not sure where that is on Windows--if someone wants to send
me a patch that'd be great.


Notes
-----

If you are having trouble on Mac OS with certain option key combinations
not recognized (they will be just dead keys) in IntelliJ, you can try using
the Coding.keylayout I made with http://scripts.sil.org/ukelele. It simply
maps all option key combinations to the plain key. You'll lose the ability
to enter cool symbols with the option key, but gain the ability to bind all
option key combinations to IntelliJ commands.

The bin directory contains a small shell script wrapper around emacsclient.
I find it useful for both command line editing and IntelliJ integration.
