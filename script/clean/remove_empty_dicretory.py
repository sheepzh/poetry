"""
 Copyright (c) 2022 Hengyang Zhang
 
 This software is released under the MIT License.
 https://opensource.org/licenses/MIT
"""
import os
import sys

argv = sys.argv
path = None
if len(argv) > 1:
    path = argv[1]

if not path or not os.path.exists(path):
    print('no directory')
    quit()

for dir in os.listdir(path):
    dir_path = os.path.abspath(os.path.join(path, dir))
    if not os.path.isdir(dir_path):
        continue
    if '_' not in dir:
        print("Invalid poet dir name: " + os.path.abspath(os.path.join(path, dir)))
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
