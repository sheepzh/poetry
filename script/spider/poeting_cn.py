import requests
import os
import re
from bs4 import BeautifulSoup
from util import Profile, write_poem

base_url = 'http://www.poeting.cn'


poets = []

exist_list = []
for root, dirs, _ in os.walk('tmp'):
    for dir in dirs:
        exist_list.append(dir[:dir.index('_')])


def find_poets():
    response = requests.get(base_url)
    soup = BeautifulSoup(response.text, 'lxml')
    nav_list = soup.find_all('div', class_='navpoet')
    for nav in nav_list:
        poet_links = nav.find_all('a')
        for link in poet_links:
            name = re.sub(r'（.+）', '', link.text)
            url = link['href']
            if name not in exist_list:
                poets.append((name, url))


def parse_poet(poet_name, poet_url):
    url = base_url + poet_url
    poet_res = requests.get(url)
    poet_soup = BeautifulSoup(poet_res.text, 'lxml')
    div_list = poet_soup.find_all('div', class_='item_wrap')
    if len(div_list) < 4:
        print('no content', url)
    div = div_list[3]
    content = re.sub(r'■\s*(.*?)\s*\n', '\n《\g<1>》\n', div.text)
    content = content.replace('回到首页\n返回顶部', '')
    write_poem(Profile(href=url, author=poet_name, title='城镇：散落的尘嚣或软语（组诗）'), content)


def main():
    find_poets()
    for poet in poets:
        parse_poet(poet[0], poet[1])


main()
