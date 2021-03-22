
'''
    pip3 install BeautifulSoup4
    pip3 install pypinyin
'''

import requests
import re
import os
import shutil
from bs4 import BeautifulSoup
from util import Profile, write_poem


def parse_poem_profile_td(td):
    container = td.find('div')
    if container is None:
        container = td

    title_a = container.find('a')

    if title_a is None:
        # maybe appears on the last page
        return None

    href = title_a.get('href')
    title = title_a.get('title')
    title = title.replace('\r\n', '').replace(
        '————', '——').replace(',', '，').replace('（长诗节选）', '_长诗节选').strip()
    title_a.extract()
    # Wrong name 席慕蓉
    author_text = container.text.replace('席慕蓉', '席慕容').strip()
    author = re.findall(r'(.*)\((\d*?)\)', author_text, re.S)[0][0]

    return Profile(href=href, title=title, author=author)


def read_poem_list(page):
    '''
        Read poem list
        @param  page:int 
        @return (poem_list:Profile[], has_next_page:Boolean)
    '''
    page_url = 'http://www.chinapoesy.com/XianDaiList_' + str(page) + '.html'
    response = requests.get(page_url)

    if response.status_code is not 200:
        return ([], False)
    text = response.text
    soup = BeautifulSoup(text, features='lxml')

    # profiles
    main_table = soup.find('table', id='DDlTangPoesy')
    td_ = main_table.find_all('td')

    poet_list = []
    for td in td_:
        poem = parse_poem_profile_td(td)
        if poem is not None:
            poet_list.append(poem)

    img_neg = soup.find('img', src='/Images/Pager/nextn.gif')

    return (poet_list, img_neg is not None)


def read_poem(poem):
    url = 'http://www.chinapoesy.com/' + poem.href
    response = requests.get(url)
    if response.status_code is not 200:
        return None
    soup = BeautifulSoup(response.text, features='lxml')
    container = soup.find_all('div', class_='HeightBorderCenter')[-1]
    return container.text.strip()


def main():
    # delete the temp directory
    has_next_page = True
    page_num = 1

    while has_next_page:
        (current_list, has_next_page) = read_poem_list(page_num)
        page_num = page_num + 1

        for poem in current_list:
            if(os.path.exists(poem.file_path())):
                continue
            content = read_poem(poem)
            if not content:
                print('Invalid content: ' + str(poem))
            else:
                write_poem(poem, content)
        print('Page ' + str(page_num) + ' parsed')


main()
