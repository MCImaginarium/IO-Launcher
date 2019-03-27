#!/bin/sh

VERSION=$(echo ./launcher/build/libs/launcher-*-all.jar | cut -d- -f2)
/cygdrive/c/Program\ Files/Java/jdk1.8.0_181/bin/pack200.exe --no-gzip $(echo ./launcher/build/libs/launcher-*-all.jar | cut -d- -f2).pack ./launcher/build/libs/launcher-*-all.jar
