"""
    To find suspected ancient poems
"""
import sys
import os
import re

argv = sys.argv
if len(argv) > 1:
    path = argv[1]

if not os.path.exists(path):
    print('no directory')
    quit()

strict = '-s' in argv
write_whitelist = '-w' in argv


def strict_line(line):
    return re.sub(r'[，。、？！；：“”—-【】《》…]', '', line)


def find(path, strict=False):
    file = open(path, 'r', encoding='utf-8')
    content = file.readlines()[3:]
    word_count_per_line = 0
    diff = False
    if len(content) < 2:
        return
    for line in content:
        if strict:
            line = strict_line(line)
        wc = len(line.strip())
        if not wc:
            continue
        if not word_count_per_line:
            word_count_per_line = wc
        if word_count_per_line != wc:
            diff = True
            return
    return not diff and word_count_per_line >= 4


whitelist = []
whitelist_dir_path = sys.path[0]
whitelist_path = os.path.join(whitelist_dir_path, 'ancient_whitelist')
if os.path.exists(whitelist_path):
    file = open(whitelist_path, 'r', encoding='utf-8')
    for line in file.readlines():
        line = line.strip()
        if line:
            whitelist.append(line)


# Really exist
found_whitelist = []

found = []


for path, dir_list, file_list in os.walk(path):
    for file_name in file_list:
        if not file_name.endswith('.pt'):
            continue
        file_path = os.path.join(path, file_name)
        if file_path in whitelist:
            found_whitelist.append(file_path)
        like_ancient = find(file_path) and find(file_path, True) if strict else find(file_path)
        if like_ancient:
            found.append(file_path)

if write_whitelist:
    for path in found:
        if path not in found_whitelist:
            found_whitelist.append(path)
    file = open(whitelist_path, 'w', encoding='utf-8')
    file.write('\n'.join(sorted(found_whitelist)))
else:
    for path in found:
        if path not in whitelist:
            print(path)
