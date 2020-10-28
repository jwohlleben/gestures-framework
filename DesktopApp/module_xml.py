#!/usr/bin/python3

from os import path
from xml.etree import ElementTree as ET
from xml.dom import minidom

root = None


def getName() -> str:

	if root is None:

		return None

	name = root.find("name")

	if name is None:

		return None

	return name.text


def getDescription() -> str:

	if root is None:

		return None

	description = root.find("description")

	if description is None:

		return None

	return description.text


def getClass(id : int) -> dict:

	if root is None:

		return None

	res = {}

	for element in root.findall("class"):

		try:

			classId = int(element.get("id"))

		except:

			return None

		label = element.find("label")
		description = element.find("description")

		if label is None or description is None:

			return None

		if classId is id:

			res["id"] = id
			res["label"] = label.text
			res["description"] = description.text

			return res

	return None


def getAllClasses() -> list:

	if root is None:

		return None

	tempDic = {}
	res = []

	for element in root.findall("class"):

		id = element.get("id")

		try:

			id = int(id)

		except:

			return None

		label = element.find("label")
		description = element.find("description")

		if label is None or description is None:

			return None

		tempDic["id"] = id
		tempDic["label"] = label.text
		tempDic["description"] = description.text

		res.append(tempDic.copy())

	return res


def createModelFile(frameLength : int, outputNeurons : int, timestamp : str) -> bool:

	modelRoot = root
	frameLengthElement = ET.Element("frameLength")
	frameLengthElement.text = str(frameLength)
	outputNeuronsElement = ET.Element("outputNeurons")
	outputNeuronsElement.text = str(outputNeurons)
	modelRoot.append(frameLengthElement)
	modelRoot.append(outputNeuronsElement)
	xmlString = ET.tostring(modelRoot)

	tempList = list(xmlString)

	while 9 in tempList:

		tempList.remove(9)
	
	while 10 in tempList:

		tempList.remove(10)

	xmlString = bytearray(tempList)
	xmlString = minidom.parseString(xmlString).toprettyxml()

	try:

		file = open(path.join(path.curdir, "output", timestamp + "_model.xml"), 'w')
		file.write(xmlString)
		file.close()

	except:

		return False

	return True


def init() -> bool:

	global root

	try:

		file = ET.parse(path.join(path.curdir, "input", "project.xml"))
		root = file.getroot()

	except:

		return False

	return True
