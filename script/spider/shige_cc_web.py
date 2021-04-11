import re
import requests
from bs4 import BeautifulSoup
from util import write_poem, Profile, remove_tmp_all

domain = 'http://shige.cc'


poet_reg = re.compile(r'^・(.*?)・$')
module_reg = re.compile(r'^【.*?】$')
title_reg = re.compile(r'◇(.*?)◇')
term_hrefs = []


def parse_poem(text, year):
    lines = text.split('\n')
    poet_name = ''
    title = ''
    content = []
    for line in lines:
        line = line.strip().replace('\ufffd', '')
        if '～　～　※　～　～' in line or module_reg.match(line):
            if title:
                write_poem(Profile(title=title, author=poet_name, href=''),
                           '\r\n'.join(content)+'\r\n'+year)
            title = ''
            continue
        poet = poet_reg.findall(line)
        if len(poet):
            poet_name = poet[0]
            continue
        title_maybe = title_reg.findall(line)

        if len(title_maybe):
            print(title_maybe, title, len(content))
            if title:
                write_poem(Profile(title=title, author=poet_name, href=''),
                           '\r\n'.join(content)+'\r\n'+year)
            title = title_maybe[0]
            content = []
        else:
            content.append(line)


def read_col(td):
    year_reg = re.compile(r'^(\d{4}年\d{1,2}月).*')
    lis = td.find_all('li')
    for li in lis:
        a = li.find('a')
        if not a:
            continue
        href = a.get('href')
        year = year_reg.findall(a.text)[0]

        response = requests.get(domain+href)
        response.encoding = 'gb2312'
        parse_poem(response.text, year)


def main():
    directory = requests.get('http://shige.cc/web/dir.php')

    soup = BeautifulSoup(directory.text, 'lxml')
    tables = soup.find_all('table')

    read_col(tables[1])


remove_tmp_all()

main()
