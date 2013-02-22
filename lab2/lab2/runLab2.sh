#!/bin/sh
if [ $# -eq 3 ]
then
  labdir=`pwd`
  datafile=$labdir/$1
  rdnfile=$labdir/$2
  algor=$3

  cd ..
  java lab2/SchedulerRunner $datafile $rdnfile $algor
else
  echo "Usage: bash runLab2.sh process-file random-numbe-file algorithm-name"
fi

