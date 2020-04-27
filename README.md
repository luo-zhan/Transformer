# 翻译工具Translator
业务中有没有遇到查询结果中经常需要进行数据字典翻译的情况，如果还在用连表查询或者for循环处理，可以试试这个！
## 快速开始
用一个简单的Demo来阐述Translator的功能:
现有一张`student`表，其结构如下:

### 1. 在具有数据字典功能的Entity类上标识注解@Dictionary
