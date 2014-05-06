#!/bin/sh

#this assumes that you have already run the command
#   cp -r ~cs61b/hw/pj2 .
#in the home dir of your lab computer.

FILES="../GRADER
../player/Board.java
../player/MachinePlayer.java
../player/Move.java
../player/Player.java
../player/Makefile
../player/Piece.java"


USER=`cat username.txt`
PROJDIR=pj2
for file in $FILES
do
    scp $file $USER@star.cs.berkeley.edu:~/$PROJDIR/
done
    



