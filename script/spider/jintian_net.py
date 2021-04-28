"""
    爬去今天的指定页面
"""
items = []


from bs4 import BeautifulSoup as soup
from util import Profile, write_poem

base_url = 'http://www.jintian.net'


import requests


def parse(item, page=1):
    url = base_url + item + '-page-' + str(page)
    response = requests.get(url)
    response.encoding = 'utf-8'
    html_text = response.text.replace(u'<br>', '\n').replace('<BR>', '\n').replace(u'</P>', '\n</P>').replace(u'</p>', '\n</p>')
    html = soup(html_text, 'lxml')

    title = html.find('h1', id='articletitle').text
    author = html.find('font', color='#996666').text[3:]

    article = html.find('div', id='articlebody').text
    total = html.find('span', class_='xspace-totlerecord')

    if total is not None:
        total = int(total.text)
        if(total > page):
            article += '\r\n\r\n\r\n\r\n\r\n' + parse(item, page + 1)
    write_poem(Profile(title=title, author=author, href=item), article)

    if page == 1:
        print(article)

    return article


# parse('/today/?action-viewnews-itemid-2469')

for item in items:
    parse(item)
