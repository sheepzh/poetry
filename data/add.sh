#!/bin/sh
# default is 1.pt
title=${1-1}

filePath=$title.pt
content=title:$title\r\ndate:$2

if [ ! -f "$filePath" ];then
  echo title:$1\\r\\ndate:$2 >> $filePath
  code $filePath
else 
  echo File exists: \'$title.pt\'.
  echo Please delete it manually, not overwrite it.
fi
