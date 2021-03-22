#!/bin/sh

filePath=$1.pt
content=title:$1\r\ndate:$2

if [ ! -f "$filePath" ];then
  echo title:$1\\r\\ndate:$2 >> $filePath
else 
  echo 'File exists. Please delete it manually, not overwrite it.'
fi