"""
    可能需要改写 WeReadScan 库内代码：跳过转 pdf 部分，不删除临时文件
"""
from selenium.webdriver import Chrome, ChromeOptions
# pip3 install WeReadScan
from WeReadScan import WeRead

# 重要！为webdriver设置headless
chrome_options = ChromeOptions()
chrome_options.add_argument('--headless')

# 启动webdriver(--headless)
headless_driver = Chrome("/home/zhy/software/chromedriver", options=chrome_options)

with WeRead(headless_driver) as weread:
    # 重要！登陆
    weread.login()
    # 爬去指定url对应的图书资源并保存到当前文件夹
    weread.scan2pdf('https://weread.qq.com/web/reader/d2332cf05b3a64d2302db21')
