#!/bin/sh
if [ $# -eq 1 ]
then
  labdir=`pwd`
  datafile=$labdir/$1
  cd ..
  java lab1/LinkerRunner $datafile
else
  echo "Usage: bash runLab1.sh filename"
fi

