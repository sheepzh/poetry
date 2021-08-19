"""
    Find these poems with the same title as the first line
"""
import os
from util import iterate_poems, read_tmp_file, write_to_tmp_file, Poem
import sys

TEMP_FILE = 'duplicate_title'

argv = sys.argv

if len(argv) > 1:
    path = argv[1]

if not os.path.exists(path):
    print('no directory')
    quit()

to_write = '-w' in argv
strict = '-s' in argv

whitelist = read_tmp_file(TEMP_FILE)
found_whitelist = []

found = []


def iterator(poem: Poem, poem_path: str, __file_name__):
    if poem_path in whitelist:
        found_whitelist.append(poem_path)
    content = poem.content
    if not len(content):
        return
    first_line = content[0]
    suspend_lines = [
        first_line,
        '《{}》'.format(first_line),
        '.{}'.format(first_line)
    ]
    if poem.title in suspend_lines:
        if strict:
            if not content[1].strip():
                found.append(poem_path)
        else:
            found.append(poem_path)


iterate_poems(path, iterator)


if to_write:
    for path in found:
        if path not in found_whitelist:
            found_whitelist.append(path)
    write_to_tmp_file(TEMP_FILE, found_whitelist)
else:
    for path in found:
        if path not in whitelist:
            print(path)
