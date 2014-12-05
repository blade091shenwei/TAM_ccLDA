<<<<<<< HEAD
#encoding=utf-8
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

# ＊＊＊＊＊＊＊＊程序功能＊＊＊＊＊＊＊＊
# 	程序解析TAM模型标注的结果，根据assign的结果得出readable的结果
# 	相关的变量：
# 		x	0或1 是否属于aspect词 whether word depend on aspect
# 		l	0或1 是否属于background词 whether word depend on background
# 		y	0或1 单词属于那一个aspect 因为规定了Y=2 所以y是0或1
# 		z	Topic ID 属于哪一个话题
# 对一个单词的判断有一下过程：
# 	（最终生成的是两个字典count[z][word]和count[y][z][word]）
# 	1. 是否属于background word，如果是的话把topic ID改成-1：z=-1
# 		这一步是x的值（0或1）会对属于哪一个topic有影响，
# 		如果是back ground词汇的话，话题ID就是-1
# 		如果l=0，不用再管z的值，z全部是-1
# 	2. 开始构造字典：
# 		如果x=0的话，不用再管y的值，因为与aspect无关了，单词属于count[z][word]
# 			字典
# 		如果x=1的话，单词属于count[y][z][word]，这时y和z的值都要考虑
# ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊

=======
#!/usr/bin/python

import sys
>>>>>>> origin/master
from operator import itemgetter

def main(): 
	filename = sys.argv[1]
	infile = file(filename, "r")

<<<<<<< HEAD
	Y = 0	#aspect的数目
	Z = 0	#topic的数目
	count = {}	#topic-word字典
	countY = {}	#aspect-topic-word字典

	for line in infile:
		tokens = line.split()
		#表明属于哪一个collection，不会使用这个数据，但是需要把它去除掉
		#就是吧输入的那个前面属于哪个collection的标识去除掉
=======
	Y = 0
	Z = 0
	count = {}
	countY = {}

	for line in infile:
		tokens = line.split()
>>>>>>> origin/master
		collection = int(tokens.pop(0))	# we don't use this but we need to remove it from the token list

		for token in tokens:
			parts = token.split(":")
<<<<<<< HEAD
			x = int(parts.pop())	#0或1 单词是aspect-dependent或aspect-mutual
			l = int(parts.pop())	#0或1 单词是background word或topic word
			y = int(parts.pop())	#0或1 单词属于哪个aspect或viewpoint
			z = int(parts.pop())	#单词属于哪一个话题
			word = ":".join(parts)

			if y > Y: Y = y		#aspect的个数
			if z > Z: Z = z		#topic的个数

			if l == 0: z = -1	# this will represent the "background"
				#l=0的话是background word，无论在迭代过程中分配的topic是什么
				#其实是跟topic无关的，所以topic ID是-1
				
			if x == 0:	
				#单词是aspect-neutral的，跟aspect无关
				if z not in count:
					count[z] = {}	#建立这个topic的字典
				if word not in count[z]:
					count[z][word] = 0	#初始化这个topic下word出现的次数
				count[z][word] += 1	
			else:
				#单词是aspect-dependent的，与aspect有关
				if y not in countY:
					countY[y] = {}		#初始化这一aspect的字典
				if z not in countY[y]:
					countY[y][z] = {}	#初始化这一aspect下某topic的字典
=======
			x = int(parts.pop())
			l = int(parts.pop())
			y = int(parts.pop())
			z = int(parts.pop())
			word = ":".join(parts)

			if y > Y: Y = y
			if z > Z: Z = z

			if l == 0: z = -1	# this will represent the "background"

			if x == 0:	
				if z not in count:
					count[z] = {}
				if word not in count[z]:
					count[z][word] = 0
				count[z][word] += 1
			else:
				if y not in countY:
					countY[y] = {}
				if z not in countY[y]:
					countY[y][z] = {}
>>>>>>> origin/master
				if word not in countY[y][z]:
					countY[y][z][word] = 0
				countY[y][z][word] += 1

	infile.close()

	Y += 1
	Z += 1
<<<<<<< HEAD
	
	#按background和各个topic的循环，依次输出background和各个topic中
		#aspect-neutral的词、aspect-1、aspect-2的词
	for z in range(-1,Z):	
		if z == -1: print "Background"
		else: print "Topic %d" % (z+1)

		w = 0	#计数变量，输出出现频率最高前几个词
		if (z not in count):	
			#如果在字典count[z][word]中没有相应ID的话题词，count[z]是空的
			#那words只能是空的
			words = {}
		else:
			#count[z]不是空的，对每一个topic下的word，按照出现次数进行排序，由大到小进行排序
=======

	for z in range(-1,Z):
		if z == -1: print "Background"
		else: print "Topic %d" % (z+1)

		w = 0
		if (z not in count):
			words = {}
		else:
>>>>>>> origin/master
			words = sorted(count[z].items(), key=itemgetter(1), reverse=True)

		for word, v in words:
			print word 
			w += 1
			if w >= 20: break

		for y in range(Y):
			print " Aspect %d" % (y+1)

			w = 0

			if (y not in countY or z not in countY[y]):
				words = {}
			else:
				words = sorted(countY[y][z].items(), key=itemgetter(1), reverse=True)
			for word, v in words:
				print "  "+word 
				w += 1
				if w >= 20: break

		print "\n"

if __name__ ==  "__main__":
  main()
