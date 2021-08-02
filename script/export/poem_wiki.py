"""
    Generate sql file, which contains all the poems, for https://poemwiki.org
    @Python3.6+
    @date 2021/08/02

    The schema of table:
        CREATE TABLE `poem` (
            `id`, bigint unsigned NOT NULL AUTO_INCREMENT,
            `title`, varchar(255) DEFAULT NULL,            -- 诗题
            `language_id`, bigint DEFAULT NULL,            -- 语言 1: 简体中文 2: 英语
            `is_original`, tinyint unsigned DEFAULT NULL,  -- 0: 译作 1: 原作
            `original_id`, bigint unsigned NOT NULL,       -- 翻译自的原作id，若条目本身为原作，该字段应为 0
            `poet`, varchar(127) DEFAULT NULL,             -- 诗人名
            `poet_cn`, varchar(255) DEFAULT NULL,          -- 诗人中文名
            `poem`, text,                                  -- 诗歌正文
            `length`, smallint unsigned DEFAULT NULL,      -- 正文字符数
            `translator`, varchar(64) DEFAULT NULL,        -- 译者名
            `from`, varchar(255) DEFAULT NULL,             -- 来源
            `year`, varchar(32) DEFAULT NULL,              -- 写作时间-年
            `month`, varchar(32) DEFAULT NULL,             -- 写作时间-月
            `date`, varchar(32) DEFAULT NULL,              -- 写作时间-日
            `updated_at`, datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 更新时间
            `created_at`, datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 创建时间
            `deleted_at`, datetime DEFAULT NULL,                       -- 删除时间
            `subtitle`, char(128) DEFAULT NULL,                        -- 副标题
            `genre_id`, bigint unsigned DEFAULT NULL,                  -- 体裁 13: 现代汉诗 14: 古诗词
            `location`, varchar(255) DEFAULT NULL,                     -- 写作地点
            `preface`, varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,   -- 题记
            PRIMARY KEY (`id`),
            KEY `author` (`poet`),
            KEY `original_id_idx` (`original_id`),
            FULLTEXT KEY `poem` (`poem`)
        ) ENGINE=InnoDB AUTO_INCREMENT=27266 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

"""
import os

SOURCE = 'www.chinese-poetry.org'
DUMP_FILE_NAME = 'dump.sql'
TABLE_NAME = 'poe.poem'

if os.path.exists(DUMP_FILE_NAME):
    os.remove(DUMP_FILE_NAME)


def process_file(poet_name, poem_title, file_path):
    """
        Process file and generate sql
    """
    file = open(file_path, 'r', encoding='utf-8')
    lines = file.readlines()
    file.close()
    date_str = lines[1][5:]
    date_len = len(date_str)
    year = date_str[0:4] if date_len >= 4 else 'NULL'
    month = date_str[4:6] if date_len >= 6 else 'NULL'
    date = date_str[6:8] if date_len == 8 else 'NULL'
    content = '\\n'.join(map(lambda s: s.strip(), lines[3:])).replace("'", "\'")

    col = "`title`,`language_id`,`is_original`,`original_id`,`poet`,`poet_cn`,`poem`,`length`,`from`,`year`,`month`,`date`,`subtitle`,`genre_id`"
    val = "'{}',1,1,0,'{}','{}','{}',{},'{}',{},{},{},'',13".format(
        poem_title, poet_name, poet_name, content, len(content), SOURCE, year, month, date
    )

    sql = "insert into `{}` ({}) values ({});\n".format(TABLE_NAME, col, val)
    file = open(DUMP_FILE_NAME, 'a', encoding='utf-8')
    file.write(sql)
    file.close()
    print(poet_name, poem_title)


def poet_name_of(dir_name):
    """
        Get the poet name from directory name
    """
    if '_' not in dir_name:
        return None
    split_index = dir_name.rfind('_')
    return dir_name[:split_index]


for root, dirs, _ in os.walk(os.path.join("..", "..", "data")):
    for dir_name in dirs:
        poet_name = poet_name_of(dir_name)
        if not poet_name:
            continue
        dir_path = os.path.join(root, dir_name)
        for root1, _, files1 in os.walk(dir_path):
            for file_name in files1:
                if not file_name.endswith('.pt'):
                    continue
                poem_title = file_name[:-3]
                file_path = os.path.join(root1, file_name)
                process_file(poet_name, poem_title, file_path)
        quit()
