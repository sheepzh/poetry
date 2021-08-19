"""
    To find suspected ancient poems
"""
from util import read_tmp_file, write_to_tmp_file, iterate_poems, Poem
import sys
import os
import re

TEMP_FILE = 'ancient_whitelist'

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


def find(content, strict=False):
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


whitelist = read_tmp_file(TEMP_FILE)

# Really exist
found_whitelist = []

found = []


def iterator(poem: Poem, poem_path: str, __file_name__):
    if poem_path in whitelist:
        found_whitelist.append(poem_path)
    content = poem.content
    like_ancient = find(content) and find(content, True) if strict else find(content)
    if like_ancient:
        found.append(poem_path)


iterate_poems(path, iterator)


if write_whitelist:
    for path in found:
        if path not in found_whitelist:
            found_whitelist.append(path)
    write_to_tmp_file(TEMP_FILE, found_whitelist)
else:
    for path in found:
        if path not in whitelist:
            print(path)
