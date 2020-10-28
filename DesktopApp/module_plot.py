#!/usr/bin/python3

import module_display as mdp
import module_data as data
import module_input as mip
import module_xml as xml
import matplotlib as mpl
import matplotlib.pyplot as plt
import matplotlib.patches as pts

from numpy import genfromtxt
from os import path


"""

WICHTIGES TODO:

Es muss index entfernt werden!


"""

FEATURES = ["x_acc", "y_acc", "z_acc", "x_gyro", "y_gyro", "z_gyro"]


COLORS = ["black", "gray", "white", "brown", "darkred", "red",  \
	"tomato", "coral", "orangered", "darkorange", "orange", "gold", \
	"olive", "yellow", "yellowgreen", "limegreen", "green", "lime", \
	"springgreen", "aqua", "deepskyblue", "skyblue", "royalblue", \
	"darkblue", "blue", "indigo", "darkviolet", "violet", "purple", \
	"deeppink", "crimson", "pink"]


def chooseColor(default : str) -> str:

	print("Do you want to see all available colors?")
	choice = mip.getYNChoice(False)
	print()

	if choice:

		print("Colors:")
		mdp.prettyPrintList(COLORS, 5, 2)
		print()

	return mip.getStringChoice(default, COLORS)


def plotOption1(fileName : str):

	file = path.join(path.curdir, "input", fileName)
	graphData = genfromtxt(file, skip_header=1, delimiter=',',
		names = ["index"] + FEATURES) # REMOVE INDEX LATER!!!

	x_values = []

	for i in range(len(graphData)):

		x_values.append(i)

	graph = plt.figure()
	graphAxis = graph.add_subplot(111)

	for feature in FEATURES:

		print("Would you like to add " + feature + '?')

		if mip.getYNChoice(True):

			graphAxis.plot(x_values, graphData[feature], '-', label=feature)

		print()

	print("Would you like to add a legend?")

	if mip.getYNChoice(True):

		graphAxis.legend(loc="best")
	
	graph.show()


def plotOption2():

	print("Features:")
	mdp.prettyPrintList(FEATURES, 3, 2)

	print()
	print("Please choose a feature")
	feature = mip.getStringChoice(None, FEATURES)
	print()

	print("Please choose alpha value")
	alpha = mip.getFloatMinMaxChoice(0, 1, 0.2)
	print()

	print("Colors:")
	mdp.prettyPrintList(COLORS, 5, 2)
	print()

	classes = xml.getAllClasses()
	colors = []

	for element in classes:

		print("Please choose color for " + element["label"])
		colors.append(mip.getStringChoice(None, COLORS))
		print()

	files = data.getFiles()

	graph = plt.figure()
	graphAxis = graph.add_subplot(111)

	x_values = []

	for key in files:

		for file in files[key]:

			graphData = genfromtxt(path.join(path.curdir, "input", file), \
				skip_header=1, delimiter=',', names=["index"] + FEATURES) # REMOVE INDEX!!!

			x_values = []

			for i in range(len(graphData)):

				x_values.append(i)

			graphAxis.plot(x_values, graphData[feature], '-',
				color=colors[key - 1], alpha=alpha)

	print("Would you like to add a legend?")

	if mip.getYNChoice(True):

		handles = []

		for i, element in enumerate(classes):

			handles.append(pts.Patch(color=colors[i], label=element["label"]))

		graph.legend(handles=handles, loc=4, bbox_to_anchor=(0.895, 0.12))

	graph.show()

def menu() -> int:

	mdp.printWithSeperator("Plot Data Menu")
	print()
	print("[1] Plot graph with features from file")
	print("[2] Plot graph with single class from all files")
	print("[9] Back to main menu")
	print()
	return mip.getNumericListChoice([1, 2, 3, 9], None)


def run():

	if not data.init() or not xml.init():

		mdp.printError("Could not initialize data")
		return
	
	menuChoice = menu()

	print("\nIf you are not sure which option to use, " + \
		"simply press ENTER to use the default setting.")
	mdp.pause()
	print()

	if menuChoice is 1:

		print("Do you want to see all available files?")

		filesChoice = mip.getYNChoice(False)

		fileList = []

		for listElement in data.getFiles().values():

			for element in listElement:

				fileList.append(element)

		if filesChoice:

			print("\nAvailable files:")
			mdp.prettyPrintList(fileList, 8, 2)
				
		print("\nPlease enter filename: ")

		fileName = mip.getStringChoice(None, fileList)
		print()

	if menuChoice is 1:

		plotOption1(fileName)

	elif menuChoice is 2:

		plotOption2()

	else:

		return
