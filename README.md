
# 华语现代诗歌集合

<div align="center">
	<img src="./doc/image/poet_cloud.png" width="100%">
</div>


![](https://img.shields.io/badge/poets-1058-orange)
![](https://img.shields.io/badge/poems-8832-yellowgreen)
![](https://img.shields.io/badge/words-2.19M-lightgreen)
![](https://img.shields.io/github/license/sheepzh/poetry)
![](https://img.shields.io/github/repo-size/sheepzh/poetry)
![visitors](https://visitor-badge.glitch.me/badge?page_id=sheepzh.poetry)


# STAR ME！！！！！PLS！！！！！

💁🏻：如果有友友知道标记构词方式的中文词典，请联系我！！！万分感谢！

## 写在前面

+ 该目录及子目录下所有作品著作权归原作者所有。**`禁止用于任何商业用途`**。
+ 收录诗人而不创造诗人。
+ 数据格式见 [<u>data/README.md</u>](./data/README.md)
+ 反馈
	+ ISSUES：[<u>Github</u>](https://github.com/sheepzh/poetry/issues) | [<u>码云</u>](https://gitee.com/make-zero/poetry/issues)
	+ 邮箱：returnzhy1996@outlook.com

## 目录

```
+++ data     # 诗歌数据：原始数据、分词数据、意象分析数据
|  
+++ doc      # 文档相关
|
+++ script   # 相关 Python 脚本
|	|
|       +++ analyze     # 词云生成脚本
|	|
|	+++ simplify    # 繁体转简体脚本，以及转换词典
|       |
|       +++ spider      # 诗歌爬虫脚本
|
+++ tool     # 命令行工具源码
|
+++ poem.bat # Windows 命令行工具
|
+++ poem.sh  # unix 命令行工具
```

## 命令行工具

>需要<u>**JDK8**</u>以上，且已经安装 Maven

+ Windows

```batch
cd %项目根目录%
cd tool
mvn clean package
cd ..
poem
```

+ Linux/MacOS
```shell
cd ${项目根目录}
cd tool
mvn clean package
cd ..
sh poem.sh
```

+ 具体命令

	+ help：查看帮助信息
	
	+ count：查询或统计

	+ clean：格式化文本
