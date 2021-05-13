# 贡献指南

首先 fork 自己的仓库，然后 clone 到本地。

> 建议使用 [VS Code](https://code.visualstudio.com) 作为文本编辑器

## 1. 新增数据

诗歌数据按诗人分组存放在 [/data](../../data) 目录下，请先阅读数据格式文档 [/data/README.md](../../data/README.md)。

### 1.1 数据来源

数据来源可以参考以下几种：

1. 爬虫。以往使用过的爬虫脚本，如果有后续的使用价值，都提交在 [/script/spider](../../script/spider) 目录下，可以参考参考。

2. 结构化文件，txt，XML，HTML，EPUB，Doc 等。可以根据具体的文件类型写脚本解析。

3. 图片或者扫描版 PDF，可以使用 OCR 工具解析，然后手动新增修改，推荐 WPS 的会员功能。当然你也可以放弃。

### 1.2 新增

如果直接是批量解析出来的，直接使用脚本新增就可以了。本节主要展示只能使用，或者使用 Ctrl+C/V 手动新增更方便的场景。

1. cd 到诗人的工作目录。以 <b><u>海子</u></b> 为例。

```shell
cd data/海子_haizi
```

2. 复制脚本到当前工作目录

```
cp ../add.sh ./
cp ../task.py ./

# Windows
copy ../add.bat ./
copy ../task.py ./
```

3. 新增诗歌文件

```
# 使用上述的 add 脚本
./add.sh [诗歌名] [创作时间]
# Windows
./add.bat [诗歌名] [创作时间]
```

4. 打开文件，粘贴

> VS Code 可以使用 Ctrl+左键 打开

### 1.3 批量新增

如果是多首诗歌的 标题+内容，可以同时粘贴

1. 创建一个缓存文件，比如说 test.pt

```
./add.sh test
```

2. 将多首诗歌内容连标题一起粘贴进去
3. 选择修改 task.py 文件里的这两行内容中的一行

```python
# 参数：文件名, 子标题按顺序, 组诗前缀, 写作时间
split_by_titles('test', ['标题1', '标题2'...], prefix='', date='')
# 参数：文件名, 标题正则(按行匹配), 组诗前缀, 写作时间
split_by_regrex('test', r'^《(.*)》$', prefix='',date='')
```

4. 运行 task.py 文件

```shell
python3 ./task.py
```

## 2. 数据整理

项目附带一个诗歌整理工具，源码在 [/tool](../../tool) 下

### 2.1 编译

> 需要 <u>**JDK8**</u> 以上，且已经安装 Maven

```batch
cd tool
mvn clean package
```
然后切换回根目录

```
cd ..
```

### 2.2 查看帮助

> Windows 使用项目根目录下的 poem.bat，Linux 和 MacOS 调用项目根目录下的 poem.sh，参数相同
> 该小节统一使用 poem.sh
```shell
./poem.sh help
```
打印如下内容
```
All the instructions:
clean        Format all the text files.
count        Count the poets or poems, according to certain query criteria.
help         Get the help info.

Use 'help [specific instructions, clean, count etc.]' to get specific help information for an instruction
```
查看具体指令的参数，以 count 为例
```shell
./poem.sh help count
```
打印如下内容
```
count:
[-a]                    Count all poetry files.
                        The following switches will be invalid if this one keeps on.
                        This switch turns off by default, but on if no other switches keep on.
[-d]                    Display details, default off.
[-s]                    Display briefs, default off.
                        Only [-s] is valid while turn on with [-d].
[[-[t][c]] WORD]        Query conditions and their switches：
    [t][c]              Query coverage: [t]=title,[c]=content.
                        [t] is on, if none is on.
    WORD                Word in title/content.

e.g.                    count -d -tc moon
```
### 2.3 clean 指令说明

clean 指令会将 /data 目录下的所有诗歌数据格式化，包含以下项功能

+ 删除多余的空行
+ 全角符号，数字，字母替换为半角
+ 会自动识别最后一行的写作时间，识别规则可以看该工具的 [单元测试](../../tool/src/test/java/cn/modernpoem/date)
