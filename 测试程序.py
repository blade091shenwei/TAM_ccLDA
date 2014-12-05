#encoding=utf-8
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


# #字典复制的方式
# dict = {}
# 
# dict['a'] = {}
# 
# dict['a']['z'] = 0
# 
# print dict


#itemgetter的使用
import operator
dict = {'a':1, 'b':2}
for k,v in dict.items():
    print k,v

