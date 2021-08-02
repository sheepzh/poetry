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
poet_count = format_digit(poet)
poet_re = re.compile(r'badge/poets-(.*)-')
readme = poet_re.sub('badge/poets-{}-'.format(poet_count), readme)

poem_count = format_digit(poem, 'K')
poem_re = re.compile(r'badge/poems-(.*)-')
readme = poem_re.sub('badge/poems-{}-'.format(poem_count), readme)

word_count = format_digit(word, 'M')
word_re = re.compile(r'badge/words-(.*)-')
readme = word_re.sub('badge/words-{}-'.format(word_count), readme)

open(readme_filename, 'w', encoding='utf-8').write(readme)

# Update this description
# @see
# https://docs.github.com/en/rest/reference/repos?query=description#update-a-repository
auth = os.getenv('GITHUB_AUTH')
if not auth:
    print("No github auth set")
    quit()
else:
    print("GITHUB_AUTH={}".format(auth))

desc = "最全的汉语现代诗歌语料库整理，{}诗人，{}诗歌，{}字，包括五四至今的所有流派。持续扩充...".format(poet_count, poem_count, word_count)
import requests
import json
data = {
    "description": desc
}

headers = {
    "Accept": "application/vnd.github.v3+json",
    "Content-Type": "application/json;charset:utf-8;",
    "Authorization": "token {}".format(auth)
}

response = requests.patch('https://api.github.com/repos/sheepzh/poetry', data=json.dumps(data), headers=headers)
if(response.status_code != 200):
    print("Failed to update description: {}".format(response.text))
else:
    print("Updated description")
