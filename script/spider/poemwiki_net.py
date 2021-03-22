import requests
import re
import time
from bs4 import BeautifulSoup
from util import Profile, write_poem
HOME_PAGE = 'https://poemwiki.net/'


NEXT_PAGE_PATTERN = re.compile(
    r'<a class="no-bg title font-hei no-select title-bar" href="(.*?)">.*?</a>', re.S)


SPIDERED = []


def get_hash(url):
    return url[23:]


def read_one_by_one(url):
    print(url)
    SPIDERED.append(get_hash(url))
    next_page_url = ''
    for __ in range(1, 20):
        response = requests.get(url)
        text = response.text

        next_page_container = NEXT_PAGE_PATTERN.findall(text)
        if len(next_page_container):
            url_temp = next_page_container[0]
        else:
            print('Page end')
            print(text)
            break
        page_hash = get_hash(url_temp)
        if page_hash not in SPIDERED:
            next_page_url = url_temp
            break
        time.sleep(1)

    soup = BeautifulSoup(text, 'lxml')
    title_h1 = soup.find('h1', class_='title')
    title = title_h1.text
    if len(title) == 3 and title[1] == ' ':
        title = title[0] + title[2]
    content_lines = soup.find(
        'div', class_='poem-content').find_all('code')
    content = '\r\n'.join(map(lambda code: code.text, content_lines))
    author = soup.find('address', class_='poem-writer').find('a').text
    if '<dt>原作' in text or '·' in author:
        print('Not chinese poem: ' + author)
    else:
        print('Parsed: '+title+' @ ' + author)
        write_poem(Profile(href=url, author=author, title=title), content)
    return next_page_url


def main():
    response = requests.get(HOME_PAGE)
    page_url = re.compile(
        r'<div class="title m-b-md"><a class="no-bg" href="(.*?)">PoemWiki</a>').findall(response.text)[0]

    while page_url is not '':
        page_url = read_one_by_one(page_url)
        time.sleep(2)


main()
