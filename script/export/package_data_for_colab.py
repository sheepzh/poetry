"""
    Package data for google colab
"""
import os

file_path = os.path.join('.', 'colab_file.txt')

if os.path.exists(file_path):
    os.remove(file_path)

count = 0

with open(file_path, 'a') as to_write:
    for root, dirs, _ in os.walk(os.path.join("..", "..", "data")):
        for dir in dirs:
            dir_path = os.path.join(root, dir)
            for root1, _, files1 in os.walk(dir_path):
                for file_path in files1:
                    if not file_path.endswith('.pt'):
                        continue
                    with open(os.path.join(root1, file_path), 'r') as poem_file:
                        lines = poem_file.readlines()[2:]
                        lines = list(filter(lambda l: len(l), lines))
                        lines = list(map(lambda l: l.replace('', ' '), lines))
                        to_write.writelines(''.join(lines))
                        count = count + 1
                        print(count / 100)
