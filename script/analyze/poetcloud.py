# 诗人词云生成，字数=频率
# pip install pyecharts
import os.path as path
import os

import random
import numpy as np
import math
from pyecharts.charts import WordCloud

adjust = {
    '陈宗华': 0.05,
    '槐蓝言白': 0.1,
    '龙羽生': 0.8
}

origin_dir_path = path.join('..', '..', 'data')

frequencies = []

total_word_num = 0

for poet_path, dir_list, file_list in os.walk(origin_dir_path):
    for poet_dir in dir_list:
        poet_name = poet_dir[0:poet_dir.index('_')]
        frequency = 0

        poem_count = 0
        for pp, dd, ff in os.walk(path.join(origin_dir_path, poet_dir)):
            for poem_file_name in ff:
                # not poem file
                if not poem_file_name.endswith('.pt'):
                    continue
                poem_file = open(path.join(pp, poem_file_name),
                                 mode='rt', encoding='utf-8')

                for line in poem_file.readlines():
                    line = line.strip()
                    if not line.startswith("date") and not line.startswith('title'):
                        frequency = frequency + len(line)
                # Score of poem number
                poem_count = poem_count + 1
        total_word_num = (total_word_num + frequency)
        frequency = frequency + poem_count * 30
        if poet_name in adjust:
            rate = adjust.get(poet_name)
            print('Adjusted ' + poet_name + ' with ' + str(rate))
            frequency = math.floor(frequency*rate)
        frequencies.append((poet_name, frequency))

frequencies.sort(reverse=True, key=lambda bi: bi[1])
frequencies = frequencies[0:200]

print(frequencies)
print('Total word number: ' + str(total_word_num))

wc = (
    WordCloud().add(shape='circle', series_name="诗人分布",
                    data_pair=frequencies, rotate_step=10, width=1000, height=600)
)
wc.render('index.html')
