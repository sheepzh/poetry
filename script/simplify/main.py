import os
import sys
from langconv import Converter

path = os.path.join('..', '..', 'data', 'origin')

argv = sys.argv
if len(argv) > 1:
    path = argv[1]

conv = Converter('zh-hans')

if not os.path.exists(path):
    print('no directory')
    quit()
for path, dir_list, file_list in os.walk(path):
    for file_name in file_list:
        if not file_name.endswith('.pt'):
            continue
        file = open(os.path.join(path, file_name), 'r')
        lines = file.readlines()

        simplified = []
        for line in lines:
            simplified.append(conv.convert(line))

        file = open(os.path.join(path, file_name), 'w')
        file.writelines(simplified)
