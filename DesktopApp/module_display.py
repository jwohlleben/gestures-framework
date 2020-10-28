#!/usr/bin/python3

from os import name, system
from sys import exit
from pprint import pprint


VERSION = "1.0.0"
SEPERATOR = "-----"


def clear():

	if name is "nt":

		system("cls")

	elif name is "posix":

		system("clear")

	else:

		return


def printInfo(string : str):

	print("[*]", string)


def printCheck(string : str):

	print("[\u2713]", string)


def printCross(string : str):

	print("[x]", string)


def printWarning(string : str):

	print("[~]", string)


def printError(string : str):

	print("[!]", string)


def pause():

	try:
		
		input("Press ENTER to continue...")

	except KeyboardInterrupt:
		
		print("\n\nBye!")
		exit(0)


def printWithSeperator(string : str):

	print(SEPERATOR + ' ' + string + ' ' + SEPERATOR)


def prettyPrintList(l : list, columns : int, padding : int):

	maxLength = 0

	for element in l:

		if len(element) > maxLength:

			maxLength = len(element)

	for i in range(len(l)):

		print(l[i].ljust(maxLength + padding), end='')

		if i % columns is (columns - 1) or i is (len(l) - 1):

			print()


def printHeader():

	string = \
	"   ____           _                       \n" + \
	"  / ___| ___  ___| |_ _   _ _ __ ___  ___ \n" + \
	" | |  _ / _ \\/ __| __| | | | '__/ _ \\/ __|\n" + \
	" | |_| |  __/\\__ \\ |_| |_| | | |  __/\\__ \\\n" + \
	"  \\____|\\___||___/\\__|\\__,_|_|  \\___||___/" + \
	" V " + VERSION

	print(string)
