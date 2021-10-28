# -*- coding: utf-8 -*-
from util import Profile, write_poem
import requests
import os
import re
from bs4 import BeautifulSoup
import sys

base_url = 'http://www.poeting.cn/Home/Category/zuozhe/cat_id/{}.html'
data_folder_path = os.path.join(sys.path[0], '..', '..', 'data')

ignored_poets = ['冰心']


def judge_exist(poet_name, title):
    """
        Judge whether this poem exist
        @return (poet_dir_name, True/False)
    """
    poet_dir_name = None
    for _, dirs, _ in os.walk(data_folder_path):
        for dir in dirs:
            if dir.startswith('{}_'.format(poet_name)):
                poet_dir_name = dir
    if not poet_dir_name:
        return (None, False)

    for _, _, files in os.walk(os.path.join(data_folder_path, poet_dir_name)):
        for file in files:
            if file == '{}.pt'.format(title) or (file.endswith('.pt') and file.startswith('{}——'.format(title))):
                return (poet_dir_name, True)

    return (poet_dir_name, False)


html_parser_reg = re.compile(r'<div\s[^<>]*><a\s[^<>]*></a><span\s[^<>]*>■</span>(.*)</div>[\n\s]*<p\s[^<>]*>(.*)</p>')
title_remove_empty_reg = re.compile(r'^.(\s+.)+$')


def parse_poet(id):
    """
        Parse and write according to the id of poeting.cn
        @return (poetName, poems:(title, exist)[])
    """
    url = base_url.format(id)
    poet_res = requests.get(url)
    poet_soup = BeautifulSoup(poet_res.text, 'lxml')
    div_list = poet_soup.find_all('div', class_='item_wrap add')
    if len(div_list) < 4:
        return (None, [])
    poet_title_div = div_list[2]
    poet_name_p = poet_title_div.find_all('p')
    if not poet_name_p or not len(poet_name_p):
        return (None, [])
    name = poet_name_p[0].text
    if not name or not name.endswith('简介'):
        return (None, [])
    name = name[:-2]
    if name in ignored_poets:
        return (name, [])

    # Start to parse poems
    poem_list_div = div_list[3]
    html = str(poem_list_div)
    html = html.replace('<br/>\r\n', '<br/>').replace('<br/>\n', '<br/><br/>')
    groups = html_parser_reg.findall(html)

    if not groups or not len(groups):
        return (name, [])

    poems = []
    for group in groups:
        title = group[0].strip().replace('──', '——')
        if title_remove_empty_reg.match(title):
            origin = title
            title = origin.replace(' ', '')
            print('Remove blanks in title: origin={}, removed={}'.format(origin, title))

        (poet_dir_name, exist) = judge_exist(name, title)
        if exist:
            poems.append((title, True))
            continue
        poems.append((title, False))
        if poet_dir_name:
            poem = Profile(url, name, title, poet_dir_path=os.path.join(data_folder_path, poet_dir_name))
        else:
            poem = Profile(url, name, title, data_dir_path=data_folder_path)
        content = group[1]
        write_poem(poem, content.replace('<br/>', '\n'))
        print('wrote {} by {}'.format(title, name))
    return (name, poems)


history_file_name = os.path.join(sys.path[0], 'poeting_cn_history.txt')


def main():
    start = 1
    if os.path.exists(history_file_name):
        with open(history_file_name, 'r') as file:
            first_line = file.readline()
            start = int(first_line)
    while True:
        print('Start to parse id={}'.format(start))
        (poet, poems) = parse_poet(start)
        if not poet or not poems or not len(poems):
            # Any poems do not exist

            if start <= 2000:
                start += 1
                continue
            else:
                print('Finished when id={}'.format(start))
                break
        start += 1
        with open(history_file_name, 'w') as file:
            # Write the start sequence of next task into file
            file.write(str(start))


main()
