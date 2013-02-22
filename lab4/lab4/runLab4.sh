#!/bin/sh
if [ $# -eq 7 ]
then
  machineSize=$1
  pageSize=$2
  processSize=$3
  jobMix=$4
  numOfRefsPerProcess=$5
  algorithm=$6
  mode=$7  

  cd ..
  java lab4/Paging $machineSize $pageSize $processSize $jobMix $numOfRefsPerProcess $algorithm $mode
else
  echo "Usage: bash runLab4.sh machineSize pageSize processSize jobMix numOfRefsPerProcess algorithm mode"
fi

