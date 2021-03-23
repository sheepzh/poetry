import requests
import re
import time
from bs4 import BeautifulSoup
from util import Profile, write_poem
PAHE_URL_SUFFIX = 'http://www.dushu369.com/shici/HTML/48365'


def get_page_url(page_num):
    if page_num is 1:
        return PAHE_URL_SUFFIX + '.html'
    else:
        return PAHE_URL_SUFFIX + '_' + str(page_num) + '.html'


TITLE_PATTERN = re.compile(r'^[〇一—二三四五六七八九]{1,3}$')


def split(lines):
    title = ''
    author = '冰心'
    url = ''
    content = ''
    for line in lines:
        line = line.strip()
        new_t = TITLE_PATTERN.findall(line)
        if len(new_t):
            if title:
                write_poem(Profile(url, author, '春水：' + title), content)
                content = ''
            title = new_t[0].replace('—', '一')
        else:
            content = content + '\r\n' + line
    if title:
        write_poem(Profile(url, author, '春水：' + title), content)


def read_by_page(page_num):
    url = get_page_url(page_num)
    response = requests.get(url)
    response.encoding = 'GBK'
    soup = BeautifulSoup(response.text, 'lxml')
    line_str = soup.find('td', class_='content').text.replace(
        '\u3000', '').replace('\r', '')
    lines = line_str.split('\n')[:-1]

    if page_num is 1:
        lines = lines[1:]

    split(lines)


def main():
    for i in range(1, 10):
        read_by_page(i)


main()
