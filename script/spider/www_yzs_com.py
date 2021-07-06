import requests
from bs4 import BeautifulSoup
from util import Profile, write_poem

HOME_PAGE = "http://yzs.com/"


def get_list():
    """
    Get the poet list
    """
    url = HOME_PAGE + "zhongguoshiren"

    response = requests.get(url)
    response.encoding = "UTF-8"
    text = response.text

    soup = BeautifulSoup(text, "lxml")
    div_list = soup.find_all("div", class_="fontlist")
    poet_list = []
    for div in div_list:
        links = div.find_all("a")
        for link in links:
            poet_list.append((link.get("href"), link.text))
    return poet_list


def read_poem(href):
    url = HOME_PAGE + href
    response = requests.get(url)
    response.encoding = "UTF-8"
    text = response.text
    text = (
        text.replace("<br />", "\n").replace("<strong>", "《").replace("</strong>", "》")
    )
    soup = BeautifulSoup(text, "lxml")
    content = soup.find("div", class_="content")
    if not content:
        print("no poems", url)
        return ''
    ps = content.find_all("p")
    lines = []
    for p in ps:
        lines.append(p.text.strip())
    lines = '\n'.join(lines).split('\n')
    if len(list(filter(lambda a: len(a), lines))) < 2:
        lines = []
        divs = content.find_all("div")
        # print(divs)
        for div in divs:
            lines.append(div.text.strip())

    if len(list(filter(lambda a: len(a), lines))) < 2:
        return content.text.strip()

    return "\n".join(lines)


def process_poems(href, name):
    url = HOME_PAGE + href

    response = requests.get(url)
    response.encoding = "UTF-8"
    text = response.text

    soup = BeautifulSoup(text, "lxml")

    list = soup.find("ul", class_="tw_list_zgsg")
    if not list:
        print("no content:", href)
        return
    links = list.find_all("a")
    # print(links)
    for link in links:
        title = link.text.replace(" ", "")
        if title.endswith("简介"):
            continue
        href = link.get("href")
        content = read_poem(href)
        if not content:
            print("empty", HOME_PAGE + href, name, title)
        write_poem(Profile(href=HOME_PAGE + href, author=name, title=title), content)
        # quit()
    # quit()


def main():
    poet_list = get_list()
    for poet in poet_list:
        process_poems(poet[0], poet[1])


# main()

process_poems('zswlistinfo-245-0.html', '陈维')
