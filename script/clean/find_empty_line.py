"""
    To find the empty_line
"""

from util import Poem, iterate_poems, write_poem
import os
import sys


argv = sys.argv
path = None
if len(argv) > 1:
    path = argv[1]

if not path or not os.path.exists(path):
    print('no directory')
    quit()


def parse_poem(poem: Poem, file_path, __file_name__):
    content = poem.content
    if len(content) <= 2:
        return
    prev_empty = True
    need_remove_empty = True
    for line in content:
        if line and not prev_empty:
            need_remove_empty = False
            break
        prev_empty = not bool(line)
    if need_remove_empty:
        print(file_path)
        content = list(map(lambda line: line.replace('\n', '').replace('\r', ''), filter(lambda line: line.strip(), content)))
        poem.content = content
    write_poem(poem, file_path)


iterate_poems(path, parse_poem)
