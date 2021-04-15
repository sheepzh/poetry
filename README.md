# 华语现代诗歌集合

<div align="center">
	<img src="./doc/image/poet_cloud.png" width="100%">
</div>

![](https://img.shields.io/badge/poets-1338-orange)
![](https://img.shields.io/badge/poems-18.0K-yellowgreen)
![](https://img.shields.io/badge/words-3.86M-lightgreen)
![](https://img.shields.io/github/license/sheepzh/poetry)
![](https://img.shields.io/github/repo-size/sheepzh/poetry)
[![Join the chat at https://gitter.im/poetry-room/community](https://badges.gitter.im/poetry-room/community.svg)](https://gitter.im/poetry-room/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
![visitors](https://visitor-badge.glitch.me/badge?page_id=sheepzh.poetry)

# STAR ME！！！！！PLS！！！！！

💁🏻：如果有友友知道标记构词方式的中文词典，请联系我！！！万分感谢！

## 写在前面

- 该目录及子目录下所有作品著作权归原作者所有。
- 收录诗人而不创造诗人。
- 数据格式见 [<u>data/README.md</u>](./data/README.md)
- 反馈
  - ISSUES：[<u>Github</u>](https://github.com/sheepzh/poetry/issues) | [<u>码云</u>](https://gitee.com/make-zero/poetry/issues)
  - 邮箱：returnzhy1996@outlook.com

## API

本项目依靠 Tencent Cloud 的 Serverless 服务搭建了简单的 RESTful 查询 API，你也可以根据项目内提供的数据自建。同时还有一个简陋的[查询页面](https://service-irzuty0y-1256916044.gz.apigw.tencentcs.com/)。

```shell
export API_BASE=http://service-irzuty0y-1256916044.gz.apigw.tencentcs.com
```

1. API-01：分页查询诗人

```shell
curl "$API_BASE/poets?wd=海&pn=4&ps=2"

{"list":[{"name":"海啸","pinyin":"haixiao","count":5},{"name":"鲸向海","pinyin":"jingxianghai","count":5}],"page":4,"size":2,"total":17}
```

参数列表

- pn: Page number，页数，可选，默认 1
- ps: Page size，页大小，可选，默认 10，超过 50 为 50
- wd: Keyword，关键字，可选

2. API-02：根据诗人分页查询诗歌

```shell
curl "$API_BASE/poet/海子/poems?wd=春天&pn=1&ps=2"

{"list":[{"title":"春天，十个海子","date":"","lines":{"0":"春天，十个海子全都复活","5":"春天，十个海子低低地怒吼","10":"在春天，野蛮而复仇的海子"}},{"title":"四姐妹","date":"","lines":{"23":"天上滚过春天的雷，"}}],"page":1,"size":2,"total":17}
```

参数列表：

- pn: 如 API-01
- ps: 如 API-01
- wd: Keyword, 内容关键字，可选。
  - 如果指定，将对诗歌进行筛选，返回内容里出现该字段的诗歌，以及出现该字段的所有诗行。
  - 如果不指定，返回该诗人的所有诗歌，以及诗歌的前三行。

3. API-O3：分页查询诗歌全文

```shell
curl "$API_BASE/poems?poet=海子&title=九月&wd=草原&pn=1&ps=2"

{"list":[{"title":"九月","poet":"海子","date":"1986","contents":["目击众神死亡的草原上野花一片","远在远方的风比远方更远","我的琴声呜咽泪水全无","我把这远方的远归还草原","一个叫马头一个叫马尾","我的琴声呜咽泪水全无","","远方只有在死亡中凝聚野花一片","明月如镜高悬草原映照千年岁月","我的琴声呜咽泪水全无","只身打马过草原"]}],"page":1,"size":2}
```

参数列表:

- pn: 如 API-01
- ps: 如 API-01
- poet: 诗人名，可选，全匹配。
- title: 标题名，可选，模糊匹配。
- wd: 同 API-02，但始终返回诗歌全文。

注意：

- 该接口不返回 total 字段，适合无限滚动查询

4. 其他 HTML 接口

   1. API-00-01：获取诗人的所有诗歌总目录

   ```shell
   wget $API_BASE/poet/张枣/list  -O zhangzao_list.html
   ```

   2. API-00-02：获取诗人的某具体诗歌全文

   ```shell
   wget $API_BASE/poet/张枣/poem/镜中 -O jingzhong.html
   ```

## 文件目录

```
+++ data            # 诗歌数据：原始数据、分词数据、意象分析数据
|
+++ doc             # 文档相关
|
+++ script          # 相关 Python 脚本
|	  |
|         +++ analyze     # 词云生成脚本
|	  |
|	  +++ simplify    # 繁体转简体脚本，以及转换词典
|         |
|         +++ spider      # 诗歌爬虫脚本
|
+++ tool            # 命令行工具源码
|
+++ poem.bat        # Windows 命令行工具
|
+++ poem.sh         # unix 命令行工具
```

## 命令行工具

> 需要<u>**JDK8**</u>以上，且已经安装 Maven

- Windows

```batch
cd %项目根目录%
cd tool
mvn clean package
cd ..
poem
```

- Linux/MacOS

```shell
cd ${项目根目录}
cd tool
mvn clean package
cd ..
sh poem.sh
```

- 具体命令

  - help：查看帮助信息

  - count：查询或统计

  - clean：格式化文本
