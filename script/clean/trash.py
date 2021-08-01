"""
    Move these file who's title and file name are different, to the trash dir ~/.poem_trash
"""
import os
import sys
import shutil
from pathlib import Path

USER_HOME = str(Path.home())

argv = sys.argv
if len(argv) > 1:
    path = argv[1]

if not os.path.exists(path):
    print('no directory')
    quit()

trash_dir = os.path.join(USER_HOME, '.poem_trash')
if not os.path.exists(trash_dir):
    os.makedirs(trash_dir)

for path, dir_list, file_list in os.walk(path):
    for file_name in file_list:
        if not file_name.endswith('.pt'):
            continue
        file_title = file_name[:-3]
        file_path = os.path.join(path, file_name)
        with open(file_path, 'r', encoding='utf-8') as file:
            first_line = file.readline().strip()
            if first_line == 'title:{}'.format(file_title):
                continue

        print('deleted {}'.format(os.path.join(path, file_name)))
        shutil.move(file_path, os.path.join(trash_dir, file_name))