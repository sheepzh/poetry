import sys
import os
from util import iterate_poems, Poem

argv = sys.argv
if len(argv) > 1:
    path = argv[1]

if not os.path.exists(path):
    print('no directory')
    quit()


def iterator(poem: Poem, poem_path: str, __file_name__):
    if not list(filter(lambda line: line, poem.content)):
        print(poem_path)
        os.remove(poem_path)


iterate_poems(path, iterator)
