"""
    诗人频率调整
    通过搜索引擎统计
"""
import asyncio
from pyppeteer import launch
from pyquery import PyQuery as pq
import re
import os
import time

from fake_useragent import UserAgent
user_agent = UserAgent()

google = 'https://www.google.com.hk/search?q="诗歌"+OR+"诗人"+"{}"&hl=zh&btnG=Search&safe=active&gbv=1'
google_scholar_proxy = 'https://xs.paodekuaiweixinqun.com/scholar?hl=zh-CN&as_sdt=0%2C5&q="诗人{word}" OR "{word}的诗"&btnG='
google_scholar = 'https://scholar.google.com.hk/scholar?hl=en&as_sdt=0%2C5&q="诗歌"+OR+"诗人"+"{}"&btnG=&hl=zh'

google_reg = re.compile(r'找到约\s?([\d,]*)\s?条结果')
google_reg2 = re.compile(r'获得\s([\d,]*)\s条结果')
google_scholar_reg = re.compile(r'找到约\s([\d,]*)\s条结果')
google_scholar_reg2 = re.compile(r'获得\s([\d,]*)\s条结果')


async def search(browser, name, url, id, reg, reg2=None):
    url = url.format(word=name)
    print(url)
    page = await browser.newPage()
    await page.goto(url)
    doc = pq(await page.content())
    result = doc(id).text()
    number = reg.findall(result)
    count = ''
    if not len(number):
        number = reg2.findall(result)
    if not len(number):
        print('error number', result)
        count = '0'
    else:
        count = number[0].replace(',', '')
    await page.close()
    return count


async def main():

    exist = {}
    if not os.path.exists('search_count.csv'):
        file = open('search_count.csv', 'a', encoding='utf-8')
    else:
        file = open('search_count.csv', 'r', encoding='utf-8')
        lines = file.readlines()
        if len(lines):
            for line in lines[1:]:
                segs = line.split(',')
                exist[segs[0].strip()] = segs[1].strip()
            file.close()
        else:
            file.close()
            file = open('search_count.csv', 'a', encoding='utf-8')
            file.write('name, google_scholar', encoding='utf-8')
            file.close()
    data_path = os.path.join('..', '..', 'data')
    for dir_ in os.listdir(data_path):
        if '_' not in dir_:
            continue

        index = dir_.rindex('_')
        name_query = dir_[:index]
        if name_query in exist:
            continue

        browser = await launch(headless=True, ignoreDefaultArgs=['--enable-automation'], args=["--proxy=127.0.0.1:8001"])
        browser.userAgent = user_agent.random
        # google_count = await search(browser, name_query, google, '#result-stats', google_reg, google_reg2)
        google_scholar_count = await search(browser, name_query, google_scholar_proxy, '.gs_ab_mdw', google_scholar_reg, google_scholar_reg2)
        if not google_scholar_count:
            quit()
        line = '{},{}'.format(name_query, google_scholar_count)
        print(line)

        file = open('search_count.csv', 'a', encoding='utf-8')
        file.write('\n{}'.format(line))
        file.close()
        # time.sleep(10)
        browser.close()

asyncio.get_event_loop().run_until_complete(main())
