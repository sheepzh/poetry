# Poetry API 文档

本项目依靠 [Tencent Cloud Serverless](https://cloud.tencent.com/product/scf) 服务搭建了简单的 RESTful 查询 API，你也可以根据项目内提供的数据自建。

仓库地址 [sheepzh/poetry-web](https://github.com/sheepzh/poetry-web)

## 1. API 地址

<b>http://service-irzuty0y-1256916044.gz.apigw.tencentcs.com</b>

```shell
export API_BASE=http://service-irzuty0y-1256916044.gz.apigw.tencentcs.com
```

## 2. RESTful

### 2.1 分页查询诗人

```shell
curl "$API_BASE/poets?wd=海&pn=4&ps=2"
```

参数列表

- pn: Page number，页数，可选，默认 1
- ps: Page size，页大小，可选，默认 10，超过 50 为 50
- wd: Keyword，关键字，可选

返回结果：

```json
{
  "list": [
    {
      "name": "海啸",
      "pinyin": "haixiao",
      "count": 5
    },
    {
      "name": "鲸向海",
      "pinyin": "jingxianghai",
      "count": 5
    }
  ],
  "page": 4,
  "size": 2,
  "total": 17
}
```

### 2.2 根据诗人分页查询诗歌

```shell
curl "$API_BASE/poet/海子/poems?wd=春天&pn=1&ps=2"
```

参数列表：

- pn: 如 API-01
- ps: 如 API-01
- wd: Keyword, 内容关键字，可选。
  - 如果指定，将对诗歌进行筛选，返回内容里出现该字段的诗歌，以及出现该字段的所有诗行。
  - 如果不指定，将不依内容筛选，并返回每首诗歌的前三行。

返回结果：

```json
{
  "list": [
    {
      "title": "春天，十个海子",
      "date": "",
      "lines": {
        "0": "春天，十个海子全都复活",
        "5": "春天，十个海子低低地怒吼",
        "10": "在春天，野蛮而复仇的海子"
      }
    },
    {
      "title": "四姐妹",
      "date": "",
      "lines": {
        "23": "天上滚过春天的雷，"
      }
    }
  ],
  "page": 1,
  "size": 2,
  "total": 17
}
```

### 2.3 分页查询诗歌，并返回全文

```shell
curl "$API_BASE/poems?poet=海子&title=九月&wd=草原&pn=1&ps=2"
```

参数列表:

- pn: 如 API-01
- ps: 如 API-01
- poet: 诗人名，可选，全匹配。
- title: 标题名，可选，模糊匹配。
- wd: 同 API-02，但始终返回诗歌全文。

返回结果：

```json
{
  "list": [
    {
      "title": "九月",
      "poet": "海子",
      "date": "1986",
      "contents": [
        "目击众神死亡的草原上野花一片",
        "远在远方的风比远方更远",
        "我的琴声呜咽泪水全无",
        "我把这远方的远归还草原",
        "一个叫马头一个叫马尾",
        "我的琴声呜咽泪水全无",
        "",
        "远方只有在死亡中凝聚野花一片",
        "明月如镜高悬草原映照千年岁月",
        "我的琴声呜咽泪水全无",
        "只身打马过草原"
      ]
    }
  ],
  "page": 1,
  "size": 2
}
```

注意：

- 该接口不返回 total 字段，适合无限滚动查询

## 3. HTML 接口

### 3.1 获取诗人的所有诗歌总目录

```shell
wget $API_BASE/poet/张枣/list  -O zhangzao_list.html
```

### 3.2 获取诗歌全文

```shell
wget $API_BASE/poet/张枣/poem/镜中 -O jingzhong.html
```
