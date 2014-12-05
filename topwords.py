#!/usr/bin/python

<<<<<<< HEAD
#encoding=utf-8
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

# ＊＊＊＊＊＊＊＊程序功能＊＊＊＊＊＊＊＊
# 	程序读入的是一个LDA迭代若干次数之后产生的每个单词的标注：
# 		1. 属于哪一个话题
# 		2. 是background word还是topic word
# ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊

from operator import itemgetter

def main(): 
	#命令行中地一个参数是文件名（或者文件的绝对路径）
=======
import sys
from operator import itemgetter

def main(): 
>>>>>>> origin/master
	filename = sys.argv[1]
	infile = file(filename, "r")

	Z = 0
<<<<<<< HEAD
	#两个字典
	count = {}
	countB = {}

	for line in infile: #这里一行是一篇文章，我们只用一篇文章的话就只循环一次
		for token in line.split():	#对于按照空格分割的每一个token
			parts = token.split(":")	#按照':'分割
			x = int(parts.pop())	#pop()是列表方法，返回一个对象
			z = int(parts.pop())
			word = ":".join(parts) #重新用冒号连接在一起，是原来的word
  			
  			#x表示是否是background
  			#z表示词汇属于哪一个话题
			if x == 1:
				if z not in count:
					count[z] = {}		#每一个话题对应一个字典
				if word not in count[z]:
					count[z][word] = 0	
					#如果word第一次在这个字典中出现的话，相当于这个字典的键和值的初始化
				count[z][word] += 1

				if z > Z: Z = z		#Z是topic数目的记录
			#如果单词是background词汇的话
=======
	count = {}
	countB = {}

	for line in infile:
		for token in line.split():
			parts = token.split(":")
			x = int(parts.pop())
			z = int(parts.pop())
			word = ":".join(parts)
  
			if x == 1:
				if z not in count:
					count[z] = {}
				if word not in count[z]:
					count[z][word] = 0
				count[z][word] += 1

				if z > Z: Z = z
>>>>>>> origin/master
			else:
				if word not in countB:
					countB[word] = 0
				countB[word] += 1
<<<<<<< HEAD
	
	infile.close()
	Z += 1			#总的话题数目再加1
	
	#输出背景词汇
	print "Background\n"
	w = 0
	#按照单词出现的次数进行排序
	words = sorted(countB.items(), key=itemgetter(1), reverse=True)
	for word, v in words:
		print word			#如果使用命令行的>命令的话，会将print的东西写进文件中
		w += 1
		if w >= 30: break	#输出每个话题中出现次数最多的前30个单词
	print "\n"
	
	#输出topic词汇
=======
	infile.close()
	Z += 1

	print "Background\n"
	w = 0
	words = sorted(countB.items(), key=itemgetter(1), reverse=True)
	for word, v in words:
		print word
		w += 1
		if w >= 30: break
	print "\n"

>>>>>>> origin/master
	for z in range(Z):
		print "Topic %d\n" % (z+1)

		w = 0
		words = sorted(count[z].items(), key=itemgetter(1), reverse=True)
		for word, v in words:
<<<<<<< HEAD
			print word		
=======
			print word
>>>>>>> origin/master
			w += 1
			if w >= 20: break
		print "\n"

if __name__ ==  "__main__":
  main()
