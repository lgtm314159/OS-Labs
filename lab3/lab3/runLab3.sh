#!/bin/sh
if [ $# -eq 1 ]
then
  labdir=`pwd`
  datafile=$labdir/$1

  cd ..
  java lab3/ResManagerRunner $datafile
else
  echo "Usage: bash runLab3.sh input-file"
fi

