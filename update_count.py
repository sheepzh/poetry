import os
import re
import math


def format_digit(digit, unit=''):
    if unit == 'M':
        return str(math.floor(digit / 1000000 * 100) / 100) + 'M'
    elif unit == 'K':
        return str(math.floor(digit / 1000 * 10) / 10) + 'K'
    else:
        return str(digit)


poet = 0
poem = 0
word = 0

for root, ds, _ in os.walk('data'):
    for d in ds:
        poet += 1
        d_path = os.path.join('data', d)
        for filename in os.listdir(d_path):
            if not filename.endswith('.pt'):
                continue
            poem += 1
            file_path = os.path.join(d_path, filename)
            file = open(file_path, 'r', encoding='utf-8')
            contents = file.readlines()[3:]
            for line in contents:
                word += len(line.strip())


print('poet={}, poem={}, word={}'.format(poet, poem, word))

readme_filename = 'README.md'
readme = open(readme_filename, 'r', encoding='utf-8').read()

# Update the count
poet_re = re.compile(r'badge/poets-(.*)-')
readme = poet_re.sub('badge/poets-{}-'.format(format_digit(poet)), readme)

poem_re = re.compile(r'badge/poems-(.*)-')
readme = poem_re.sub('badge/poems-{}-'.format(format_digit(poem, 'K')), readme)

word_re = re.compile(r'badge/words-(.*)-')
readme = word_re.sub('badge/words-{}-'.format(format_digit(word, 'M')), readme)

open(readme_filename, 'w', encoding='utf-8').write(readme)
