#!/usr/bin/python

import sys
from operator import itemgetter

def main(): 
	filename = sys.argv[1]
	infile = file(filename, "r")

	C = 0
	Z = 0
	count = {}
	countC = {}

	for line in infile:
		tokens = line.split()
		c = int(tokens.pop(0))

		for token in tokens:
			parts = token.split(":")
			x = int(parts.pop())
			z = int(parts.pop())
			word = ":".join(parts)
  
			if c > C: C = c
			if z > Z: Z = z

			if x == 0:
				if z not in count:
					count[z] = {}
				if word not in count[z]:
					count[z][word] = 0
				count[z][word] += 1
			else:
				if c not in countC:
					countC[c] = {}
				if z not in countC[c]:
					countC[c][z] = {}
				if word not in countC[c][z]:
					countC[c][z][word] = 0
				countC[c][z][word] += 1

	infile.close()

	C += 1
	Z += 1

	for z in range(Z):
		print "Topic %d\n--------" % (z+1)

		w = 0
		if (z not in count):
			words = {}
		else:
			words = sorted(count[z].items(), key=itemgetter(1), reverse=True)

		for word, v in words:
			print word 
			w += 1
			if w >= 20: break

		for c in range(C):
			print "-Collection %d" % (c)

			w = 0

			if (c not in countC or z not in countC[c]):
				words = {}
			else:
				words = sorted(countC[c][z].items(), key=itemgetter(1), reverse=True)
			for word, v in words:
				print "  "+word 
				w += 1
				if w >= 20: break

		print "\n"

if __name__ ==  "__main__":
  main()
