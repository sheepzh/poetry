"""
    诗人频率调整
    通过搜索引擎统计
"""
import asyncio
from genericpath import exists
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

temp_file_path = 'search_count.tmp'

if os.path.exists(temp_file_path):
    os.remove(temp_file_path)


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

    exists = {}
    to_write = []
    if os.path.exists('search_count.csv'):
        file = open('search_count.csv', 'r', encoding='utf-8')
        lines = file.readlines()
        if len(lines):
            for line in lines[1:]:
                segs = line.split(',')
                name = segs[0].strip()
                score = segs[1].strip()
                exists[name] = score
            file.close()

    data_path = os.path.join('..', '..', 'data')
    searched = 0
    for dir_ in os.listdir(data_path):
        if '_' not in dir_:
            continue
        searched += 1
        print('start to search {}th, {}'.format(searched, dir_))

        index = dir_.rindex('_')
        name_query = dir_[:index]
        if name_query in exists:
            to_write.append((name_query, exists[name_query]))
            continue

        browser = await launch(headless=True, ignoreDefaultArgs=['--enable-automation'], args=["--proxy=127.0.0.1:8001"])
        browser.userAgent = user_agent.random
        # google_count = await search(browser, name_query, google, '#result-stats', google_reg, google_reg2)
        google_scholar_count = await search(browser, name_query, google_scholar_proxy, '.gs_ab_mdw', google_scholar_reg, google_scholar_reg2)
        if not google_scholar_count:
            quit()
        line = '{},{}'.format(name_query, google_scholar_count)
        print(line)
        temp_file = open(temp_file_path, 'a', encoding='utf-8')
        temp_file.write(line + '\n')
        temp_file.close()

        to_write.append((name_query, int(google_scholar_count)))
        browser.close()

    to_write = sorted(to_write, key=lambda e: int(e[1]), reverse=True)

    file = open('search_count.csv', 'w', encoding='utf-8')
    file.write('name, google_scholar')
    for line in to_write:
        file.write('\n{},{}'.format(line[0], line[1]))
    file.close()
    if os.path.exists(temp_file_path):
        os.remove(temp_file_path)
asyncio.get_event_loop().run_until_complete(main())
