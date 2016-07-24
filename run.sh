#!/bin/bash

# this is our crappy build script.. should really learn makefiles
SRC_FOLDER="/home/joe/workspace/Java-dns-server/src/dns-server"

#FILE1=$SRC_FOLDER/Listener.java

# compile all java classes
javac $SRC_FOLDER/*.java

# if script being called from another script.
# sourceing... ?

# run our main class
sudo -E java Listener
