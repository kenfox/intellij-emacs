#!/bin/bash

emacsdir=/Applications/Emacs.app/Contents/MacOS
socket=$HOME/.emacs.d/server/server

if [ ! -e $socket ]; then
   $emacsdir/Emacs &
   while [ ! -e $socket ]; do
     sleep 1
   done
fi

$emacsdir/bin/emacsclient \
  --no-wait \
  --socket-name=$socket "$@"
