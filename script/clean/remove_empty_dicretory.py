"""
 Copyright (c) 2022 Hengyang Zhang
 
 This software is released under the MIT License.
 https://opensource.org/licenses/MIT
"""
import os
import sys

argv = sys.argv
if len(argv) > 1:
    path = argv[1]

if not os.path.exists(path):
    print('no directory')
    quit()

for dir in os.listdir(path):
    if '_' not in dir:
        continue
    dir_path = os.path.join(path, dir)
    if not os.path.isdir(dir_path):
        continue
    has_pt = False
    for file in os.listdir(dir_path):
        file_path = os.path.join(dir_path, file)
        if os.path.isdir(file_path):
            has_pt = True
            break
        elif not has_pt:
            if file.endswith('.pt'):
                has_pt = True
    if not has_pt:
        print("Remove {}".format(dir))
        os.removedirs(dir_path)
