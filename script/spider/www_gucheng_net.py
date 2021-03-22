import requests
from bs4 import BeautifulSoup
import re
import os
import sys
import time
from util import Profile, write_poem

URL = 'http://www.gucheng.net/'
HOME_PAGE = URL + 'gc/gczp/gcsg/sgzj/200806/5798.html'


def read_poem(href):
    poem_url = URL+href

    response = requests.get(poem_url)
    response.encoding = 'GB2312'
    text = response.text.replace('<br/>', '\n')

    soup = BeautifulSoup(text, "lxml")
    title_container = soup.find('td', class_='main_ArticleTitle')
    title = ''
    if title_container:
        title = title_container.text.strip()
    content_td = soup.find('td', id='fontzoom')
    if not content_td:
        print('No content: ' + poem_url)
        return
    content_p = content_td.find_all('p')
    lines = []
    for p in content_p:
        lines.append('\r\n\r\n')
        p_contents = p.contents
        for p_c in p_contents:
            line = str(p_c).strip().replace('<br/>', '\r\n')
            lines.append(line)
    poem_content = ''.join(lines)

    # print(title, poem_url)
    write_poem(Profile(href=poem_url, author='顾城', title=title), poem_content)


def main():
    response = requests.get(
        'http://www.gucheng.net/gc/gczp/gcsg/sgzj/200806/5798.html')
    soup = BeautifulSoup(response.content, "lxml", from_encoding="GB2312")
    # print(soup)
    mainbox = soup.find('table', class_='mainbox')
    # print(mainbox)
    hrefs = []
    mso_normal_p = mainbox.find_all('p', class_='MsoNormal')

    for p in mso_normal_p:
        for a in p.find_all('a'):
            href = a.get('href')
            if not href:
                continue
            href = href.strip()
            title = a.text.strip()
            valid_href = href and href not in hrefs and not href.endswith(
                '200809/5758.html') and 'http' not in href and 'jpg' not in href
            valid_title = '(寓' not in title and '(歌词' not in title and '(旧' not in title and '(工' not in title
            if valid_href and valid_title:
                hrefs.append(href)

    for href in hrefs:
        # time.sleep(1)
        read_poem(href)


main()
