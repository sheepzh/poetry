"""
 Copyright (c) 2022 Hengyang Zhang
 
 This software is released under the MIT License.
 https://opensource.org/licenses/MIT
"""

import os.path as path
import json
import sys
import os

same_config = path.join(path.dirname(__file__), "same.json")
data_path = None

argv = sys.argv
if len(argv) > 1:
    data_path = argv[1]

if not data_path or not path.exists(data_path):
    print('no directory')
    quit()

with open(same_config, "r") as config_file:
    config_json = "\n".join(config_file.readlines())

try:
    config = json.loads(config_json)
except:
    print("config error")
    quit()

name_dict = dict()

for item in config:
    if "names" not in item:
        continue
    names = item["names"]
    if not len(names):
        continue
    target_name = names[0]
    for name in names:
        if name == target_name:
            continue
        if name in name_dict:
            print("Duplicate name: " + name)
            quit()
        name_dict[name] = target_name

all_names = []

for dir in os.listdir(data_path):
    if "_" not in dir:
        continue
    idx = dir.rindex("_")
    name = dir[:idx]
    if name in name_dict:
        print("{} --> {}".format(name, name_dict[name]))
