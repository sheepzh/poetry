@echo off

REM 请将该脚本移动到具体的诗人目录下再使用
REM %1%=诗歌标题
REM %2%=创作日期

if "%1%"=="" (
echo 请输入诗歌名称
goto entrance
) 

set fileName=%1%.pt

if exist %fileName% (
echo 文件已存在。如需覆盖，请先使用下述命令删除旧文件
echo del %fileName%
goto entrance
)

REM 使用UTF-8
chcp 65001

set firstLine=title:%1%
set nextLine=date:%2%
echo %firstLine% > %fileName%
echo %nextLine% >> %fileName%
start %fileName%

:entrance