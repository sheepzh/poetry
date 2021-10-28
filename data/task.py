import re


def write(title, content, prefix=None, date=None):
    print(title)
    if not date:
        date = ''
    if prefix:
        title = prefix + '：' + title
    with open(title + '.pt', 'w', encoding='UTF-8')as to_w:
        to_w.writelines(
            'title:' + title + '\n' + 'date:' + date + '\n' + '\n'.join(content))


def split_by_regrex(file_name, regrex_str, prefix='', date=None, title_map=None, regrex_map=None):
    title = ''
    content = []
    regrex = re.compile(regrex_str)
    index = 1
    origin_date = ''
    with open(file_name + '.pt', 'r', encoding='UTF-8') as file:
        file_lines = file.readlines()
        # 1st line: title:${title}
        # 2nd line: date:${date}
        # 3rd line: url:${origin url}
        lines = file_lines[3:]
        date_line = file_lines[1]
        if len(date_line) > 5:
            origin_date = date_line[5:]
    date = date if date else origin_date

    for line in lines:
        line = line.strip()

        if regrex.match(line):
            if title:
                if title_map:
                    title = title_map(title, index)
                write(title, content, prefix=prefix, date=date)
                index += 1
            if regrex_map:
                title = regrex_map(regrex.findall(line)[0])
            else:
                title = regrex.findall(line)[0]
            content = []
        else:
            content.append(line)
    if title:
        if title_map:
            title = title_map(title, index)
        write(title, content, prefix, date)


def split_by_titles(file_name, titles=[], prefix='', date=None):
    l = len(titles)
    t = titles[0]
    content = []
    ti = 1
    tn = titles[ti]

    with open(file_name + '.pt', 'r', encoding='UTF-8') as file:
        lines = file.readlines()[3:]
    for line in lines:
        line = line.strip()
        if not line and ti == 1:
            continue
        if line == tn:
            if t:
                if ti == 1:
                    content = content[1:]
                write(t, content, prefix, date)
            content = []
            t = tn
            ti += 1
            if l > ti:
                tn = titles[ti]
            else:
                tn = None
        else:
            content.append(line)

    if t:
        write(t, content, prefix, date)


# split_by_titles('1', ['“有人吗？”', '致敬', '哲学家和诗人'], prefix='', date='')

def index_to_title(_title, index): return str(index)


def two_regrex(arr): return arr[0].strip() + '——' + arr[1].strip()


split_by_regrex('1', r'^《(.*)》$', prefix='', date='')
