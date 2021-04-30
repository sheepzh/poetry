"""
    豆瓣日记
"""
import requests
import re
from time import sleep
import random
from bs4 import BeautifulSoup, element
from util import write_poem, Profile

user_id = 'gassin'

author_name = '甜河'

cookie = 'bid=7Q9STV5gcjs; douban-fav-remind=1; ll="118282"; __utmc=30149280; gr_user_id=8da2b376-20f4-4ed8-8513-6c290986bbc1; __gads=ID=0afd5916ffc34ce5-220420b99bc70057:T=1619412950:RT=1619412950:S=ALNI_Ma7kq7zCfoIPGf6pKgzwkr-zlII0g; _ga=GA1.2.1099631925.1619080349; viewed="26342533_25773427_31160672"; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1619749927%2C%22https%3A%2F%2Fwww.google.com.hk%2F%22%5D; _pk_ses.100001.8cb4=*; __utma=30149280.1099631925.1619080349.1619663091.1619749928.9; __utmz=30149280.1619749928.9.6.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); ap_v=0,6.0; __yadk_uid=VNHS41eVshvpNifwGPQSZJMzMROqpN7H; regpop=1; push_doumail_num=0; __utmv=30149280.14479; __utmt=1; dbcl2="144794327:uv2snZ7WveM"; ck=WeKm; _pk_id.100001.8cb4=a28b38d3ffc4e1f7.1619080348.7.1619750629.1619663088.; __utmb=30149280.59.7.1619750396044; push_noty_num=1'

headers = {
    'Cookie': cookie,
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36',
}

base_url = 'https://www.douban.com/people/{}/notes?'.format(user_id)


def get_url_of_page(page_num):
    return base_url + 'start=' + str((page_num - 1) * 10)


def parse_content(soup, title_txt):
    """
        自定义处理内容
    """
    # print(soup)
    lines = []
    for child in soup.children:
        if type(child) is element.Tag:
            text = child.text
            if(child.name.startswith('h')):
                lines.append('《{}》'.format(text.strip().replace('\n', '——')))
            else:
                lines.append(text)
        else:
            text = str(child)
            lines.append(text)

    write_poem(Profile(href='', author=author_name, title=title_txt), '\n'.join(lines).strip())

    print('爬取成功:{}'.format(title_txt))


def crawl_note(note_id):
    url = 'https://www.douban.com/note/{}/'.format(note_id)
    response = requests.get(url, headers=headers)
    soup = BeautifulSoup(response.text, 'lxml')
    content_div_id = 'note_{}_full'.format(note_id)
    content = soup.find('div', id=content_div_id).find('div', class_='note')
    title_txt = soup.find('div', class_='note-header').find('h1').text.strip()
    parse_content(content, title_txt)


note_item_reg = re.compile(r'id="note-(\d*?)"')


def main():
    """
        主函数
    """
    page = 1
    while True:
        url = get_url_of_page(page)
        response = requests.get(url, headers=headers)
        html = response.text
        items = note_item_reg.findall(html)
        if not len(items):
            print('爬虫结束')
            return

        print('第{}页共{}条日记'.format(page, len(items)))
        page += 1
        for item in items:
            crawl_note(item)
            sleep(random.randint(2,4))


main()
