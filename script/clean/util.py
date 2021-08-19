import sys
from os.path import join, exists
from os import makedirs, walk

TMP_DIR_PATH = join(sys.path[0], 'tmp')


def _file_path(file_name):
    return join(TMP_DIR_PATH, '{}.tmp'.format(file_name))


def read_tmp_file(file_name):
    """
        Read lines from temp file
    """
    file_path = _file_path(file_name)
    list_ = []
    if exists(file_path):
        with open(file_path, 'r', encoding='utf-8') as file:
            for line in file.readlines():
                line = line.strip()
                if line:
                    list_.append(line)
    return list_


def write_to_tmp_file(file_name: str, content: list):
    """
        Write content into the temp file
    """
    if not exists(TMP_DIR_PATH):
        makedirs(TMP_DIR_PATH)
    file_path = _file_path(file_name)
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write('\n'.join(sorted(content)))


def iterate_poems(data_dir_path: str, iterator):
    for path, __dir_list__, file_list in walk(data_dir_path):
        for file_name in file_list:
            if not file_name.endswith('.pt'):
                continue
            file_path = join(path, file_name)
            file = open(file_path, 'r', encoding='utf-8')
            lines = file.readlines()
            title = lines[0][6:]
            date = lines[1][5:]
            content_lines = lines[3:]
            file.close()
            iterator(Poem(title, date, content_lines), file_path, file_name)


class Poem:
    def __init__(self, title, date, content):
        self.title = title
        self.date = date
        self.content = content
