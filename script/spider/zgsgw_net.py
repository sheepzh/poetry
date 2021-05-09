import requests
import re
from util import Profile, write_poem
from bs4 import BeautifulSoup
import time
from random import randint

BASE_URL = 'http://www.zgshige.com'

list_reg = re.compile(r"javascript:window.location.href='(http://www.zgshige.com/zcms/poem/list\?SiteID=\d+&poetname=)'\+encodeURI\('(.+?)'\)\+'(&articleID=\d+&articleContributeUID=\d+&catalogID=\d+)';")

title_reg = re.compile(r'class="sr_dt_title">\s*<a href="(http://www.zgshige.com/c/([\d-]+)/\d+.shtml)">《(.+?)》</a>')


def get(url):
    response = requests.get(url)
    response.encoding = 'utf-8'
    return response.text


def read_content(url):
    text = get(url).replace(u'<br>', '\n').replace('<BR>', '\n').replace(u'<br/>', '\n').replace('<BR/>', '\n').replace(u'</P>', '\n</P>').replace(u'</p>', '\n</p>')
    soup = BeautifulSoup(text, 'lxml')
    result = soup.find('div', id='content').text
    return result


def read_list(poet_name, list_url):
    '''
        读取所有的标题和链接
    '''
    text = get(list_url).replace('&#32;', '')
    titles = title_reg.findall(text)
    for title in titles:
        url = title[0]
        date = title[1]
        content = read_content(url) + '\n' + date
        write_poem(Profile(href=url, author=poet_name, title=title[2]), content)


def read_by_poet(url):
    '''
        根据诗人的 url 获取作品绝对链接
    '''
    text = get(BASE_URL + url)
    list_result = list_reg.findall(text)
    if len(list_result):
        result = list_result[0]
        poet_name = result[1]
        url = ''.join(result)
        read_list(poet_name, url)


poet_reg = re.compile(r'<a href="(/c/[\d-]+?/\d+.shtml)" class="p-t-xs p-b-xs block text-center" target="_blank">.*?</a>')


def read_teyao():
    '''特邀诗人'''
    text = get('http://www.zgshige.com/tysr/')
    poets = poet_reg.findall(text)
    for poet_url in poets:
        read_by_poet(poet_url)
        print(BASE_URL + poet_url)
        time.sleep(randint(4, 10))


def read_zhuzhan():
    '''驻站诗人'''
    text = get('http://www.zgshige.com/zzsr/')
    poets = poet_reg.findall(text)
    for poet_url in poets:
        read_by_poet(poet_url)
        print(BASE_URL + poet_url)
        time.sleep(randint(4, 10))


mingjia_reg = re.compile(r'<a class="h4 bold" href="(/c/[\d-]+/\d+?.shtml)" target="_blank">')


def read_mingjia_page(page_num):
    if page_num == 1:
        url = 'http://www.zgshige.com/zzmjx/index.shtml'
    else:
        url = 'http://www.zgshige.com/zzmjx/index_{}.shtml'.format(str(page_num))
    text = get(url)
    poets = mingjia_reg.findall(text)
    for url in poets:
        read_by_poet(url)
        print(BASE_URL + url)
        # time.sleep(randint(4, 10))


def read_mingjia():
    for i in range(13):
        read_mingjia_page(i + 1)
        print(i + 1)


read_mingjia()
# read_zhuzhan()
# read_teyao()

# read_by_poet('/c/2018-08-26/6985461.shtml')
# read_content('http://www.zgshige.com/c/2021-01-15/16545743.shtml')
