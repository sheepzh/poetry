import requests
import re
from bs4 import BeautifulSoup
import os.path

response = requests.get('http://shiyang.net/')
soup = BeautifulSoup(response.text, 'lxml')


def read_content(content):
    title = content.find('h3').text.strip()
    if title.startswith('【诗歌】'):
        title = title[4:]

    line_p = content.find_all('p')

    content = ''

    for p in line_p:
        line = p.text.strip()
        if line.startswith(title) and content is '':
            line = line[len(title):]
        elif len(re.findall(r'[（(]?选自.*?《.*?》[）)]?', line)):
            continue

        if line.startswith('诗阳 '):
            line = line[3:]

        content = content + '\r\n' + line
    title_reg = re.findall(r'(.*?)[（(]选自组诗《(.*?)》[）)]', title)
    if len(title_reg):
        title = title_reg[0][1]+'：'+title_reg[0][0]
    else:
        title_reg = re.findall(r'(.*?)[（(]组诗[）)]', title)
        if len(title_reg):
            print(title_reg)
            title = title_reg[0] + '_组诗'
    content = content.replace('·诗阳·', '').replace('诗阳', '')
    path = './tmp/诗阳_shiyang/'+title+'.pt'

    if os.path.isfile(path):
        return
    with open(path, 'w') as file:
        # print(title)
        file.write('title:'+title+'\r\ndate:\r\n'+content)


def read_page(page_num):
    url = 'http://shiyang.net/?cat=5&paged=' + str(page_num)
    print(url)
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'lxml')
    content_containers = soup.find_all('div', class_='post')
    for container in content_containers:
        read_content(container)
    if '下一页 &raquo;' in response.text:
        return page_num + 1
    else:
        return 0


if not os.path.exists('./tmp/诗阳_shiyang'):
    os.makedirs('./tmp/诗阳_shiyang')
page_num = 1
while page_num > 0:
    page_num = read_page(page_num)
