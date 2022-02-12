"""
 Copyright (c) 2022 Hengyang Zhang
 
 This software is released under the MIT License.
 https://opensource.org/licenses/MIT
"""
import sys
import os
from langconv import Converter
from shutil import move


argv = sys.argv
path = None
if len(argv) > 1:
    path = argv[1]

conv = Converter('zh-hans')
print('Converter is ready')

if not path or not os.path.exists(path):
    print('no directory')
    quit()

for root, dirs, _ in os.walk(path):
    for dir in dirs:
        dir: 'str' = dir
        if '_' not in dir:
            continue
        idx = dir.rindex('_')
        name = dir[:idx]
        pinyin = dir[idx + 1:]
        simple_name = conv.convert(name)
        if simple_name == name:
            continue
        new_dir = simple_name + "_" + pinyin
        new_path = os.path.join(root, new_dir)
        old_path = os.path.join(root, dir)
        if os.path.exists(new_path):
            for file_name in os.listdir(old_path):
                file_path = os.path.join(old_path, file_name)
                new_file_path = os.path.join(new_path, file_name)
                move(file_path, new_file_path)
        else:
            move(old_path, new_path)
