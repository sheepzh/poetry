import requests
from bs4 import BeautifulSoup
import re
import os
import sys
import time
from util import Profile, write_poem, remove_tmp_all

HOME_PAGE = 'https://bedtimepoem.com/'


def get_total_page_num():
    response = requests.get(HOME_PAGE)
    soup = BeautifulSoup(response.text, features='lxml')
    count_container = soup.find('small', class_='page-count')
    page_info = count_container.text
    return int(page_info.split(' ')[-1])


def read_poems(page_num):
    failed_urls = []
    url = HOME_PAGE + 'page/' + str(page_num)
    response = requests.get(url)
    if response.status_code is not 200:
        print('http error: page='+str(page_num))
        return
    soup = BeautifulSoup(response.text, features='lxml')
    posts = soup.find_all('div', class_='post-image fitvid')
    hrefs = list(map(lambda post: post.find('a').get('href'), posts))

    for href in hrefs:
        resolve_poem(href, failed_urls)
        time.sleep(0.2)
    return failed_urls


CONTENT_PATTERN = re.compile(r'(<p>(.*?)</p>)*')


def resolve_content(ele):
    '''
        Resolve the content of the poem
    '''
    ele = ele.replace('<p>', '\n').replace('<p align="justify">', '\n').replace(
        '</p>', '').replace('<br />', '').strip().replace('\n', '\r\n').replace('&emsp;', ' ').replace('&ensp;', ' ')
    # print(ele)
    return ele


def resolve_title(ele):
    ele = ele.replace('<strong>', '').replace(
        '</strong>', '').replace('<b>', '').replace('</b>', '').strip()
    reg = re.compile(r'<a.*>(.*)</a>')
    mat = reg.findall(ele)
    if len(mat):
        return mat[0]
    else:
        return ele


AUTHOR_PATTERN = re.compile(r'<p( data-page="0")?>作者\s*/\s*\[.*?\](.*)</p>')
AUTHOR_PATTERN1 = re.compile(
    r'<p( data-page="0")?>作者\s*/\s*\[.*?\](.*)<br />\n(.*?)</p>')
AUTHOR_PATTERN2 = re.compile(
    r'<p><span.*?>作者\s*/\s*\[.*?\](.*)</span><br />\n(.*?)</p>')
AUTHOR_PATTERN3 = re.compile(
    r'>\s*作者\s*/\s*\[.*?\]\s*(.*?)<'
)
# print(AUTHOR_PATTERN3.findall('<p align="justify"> 作者 / [清] 纳兰性德</p>'))


def is_not_modern_chinese_poet(text):
    # print(text)
    authors = AUTHOR_PATTERN.findall(text)

    if len(authors):
        author = authors[-1]
        if isinstance(author, tuple):
            author = author[-1]
        return author.strip()

    authors = AUTHOR_PATTERN1.findall(text)
    if not len(authors):
        authors = AUTHOR_PATTERN2.findall(text)

    # print(authors)
    if len(authors):
        author = authors[0]
        if isinstance(author, tuple):
            author = author[-2]
        else:
            author = authors[-2]
        return author.strip()

    authors = AUTHOR_PATTERN3.findall(text)

    if len(authors):
        return authors[0].strip()
    return None


# Only match poems writen by modern poets

POEM_PATTERN = re.compile(
    r'题图.*<h[12]>(.*)</h[12]>(.*)<p( align="justify")?( data-page="0")?>作者\s*/\s*(.*?)</p>', re.S)
POEM_PATTERN1 = re.compile(
    r'<h2>(.*?)</h2>(.*)<p( align="justify")?( data-page="0")?>作者\s*/\s*(.*?)</p>', re.S)
POEM_PATTERN2 = re.compile(
    r'<h2><a href=".*"><img.*></a><span style="color: #000000;">(.*?)</span></h2>(.*)<p><span style="color: #000000;">作者\s*/\s*(.*?)</span><br />.*?</p>', re.S
)


def resolve_poem(url, failed_urls):
    response = requests.get(url)
    if response.status_code is not 200:
        print('http error: url=' + url)
        return False
    text = response.text
    bad_author = is_not_modern_chinese_poet(text)

    if bad_author:
        print('not mofailed_urlsdern chinese poet: ' + bad_author)
        return
    poem_elements = POEM_PATTERN.findall(text)

    if not len(poem_elements):
        poem_elements = POEM_PATTERN1.findall(text)
    if not len(poem_elements):
        poem_elements = POEM_PATTERN2.findall(text)

    # print(text)
    # print(poem_elements)
    if len(poem_elements):
        poem = poem_elements[0]
        if len(poem) >= 3:
            title = resolve_title(poem[0])
            content = resolve_content(poem[1])
            author = poem[-1].split('<br />')[0].strip()
            # remove suffixes
            author = author.split('（')[0].split(
                '(')[0].split('，')[0].split(',')[0]

            if '·' in author or '•' in author or '[' in author or '[' in author:
                print('not mofailed_urlsdern chinese poet: ' + author)
            else:
                print('PARSED: ' + url + ' ' + title + ' @ ' + author)
                write_poem(
                    Profile(href=url, author=author, title=title), content)
        else:
            print(len(poem))
            print(poem)

    else:
        print("Parsed failed: " + url)
        if url not in failed_urls:
            failed_urls.append(url)


def main():
    argv = sys.argv
    start_page = 1
    if len(argv) > 1:
        try:
            start_page = int(argv[1])
        except Exception:
            print("Invalid start page, will start from 1: " + argv[1])
    page_total = get_total_page_num()
    print('page_total=' + str(page_total))

    if start_page == 1:
        remove_tmp_all()
    for page_num in range(start_page, page_total + 1):
        failed_urls = read_poems(page_num)
        with open(os.path.join('tmp', 'failed_urls.txt'), 'a')as file:
            for f_url in failed_urls:
                file.write(f_url+'\r\n')
        print('Parsed page: ' + str(page_num))
    write_poem(Profile(author='failed', title='urls',
                       href=''), '\r\n'.join(failed_urls))


main()
# read_poems(8)
# resolve_poem('https://bedtimepoem.com/archives/11990', [])
