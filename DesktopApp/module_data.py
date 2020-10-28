from numpy import asarray, float32, uint8
from numpy import array as nparray
from os import listdir, path
from pandas import DataFrame, read_csv, concat
from sys import maxsize

files = {}
fileFrames = {}

maxFrameLength = maxsize
frameLength = 8
matrix = None


def getFiles():

	return files


def getInputFilePath(file : str) -> str:

	return path.join(path.curdir, "input", file)


def setFrameLength(length : int) -> bool:

	global frameLength

	if length >= 16 and length <= maxFrameLength:

		frameLength = length
		return True

	else:

		return False


def getFrameLength() -> int:

	return frameLength


def getMaxFrameLength() -> int:

	return maxFrameLength


def getFramesFromFile(fileName : str) -> DataFrame:

	file = read_csv(fileName)
	fileLength = len(file)
	maxFrames = fileLength - frameLength + 1
	framesList = []

	for i in range(maxFrames):

		framesList.append(file.iloc[i : frameLength + i])

	return concat(framesList)


def framesToMatrixFormat(frames : DataFrame, classFlag : int) -> DataFrame:

	columns = []
	rows = []
	features = ["x_acc", "y_acc", "z_acc", "x_gyro", "y_gyro", "z_gyro", "class"]
	frameCount = int(len(frames) / frameLength)

	for feature in features:

		if feature is "class":

			columns.append(feature)
			continue

		for i in range(0, frameLength):

			columns.append(feature + str(i))

	for i in range(frameCount):

		j = i * frameLength
		row  = []
		temp = []

		for feature in features:

			if feature is "class":

				continue

			temp.append(frames.iloc[j : frameLength + j][feature].to_numpy())
		
		for element in temp:

			for entry in element:

				row.append(entry)

		row.append(classFlag)
		rows.append(row)

	return DataFrame(rows, columns=columns)


def buildMatrix():

	global matrix
	frames = []

	for key in files:

		for file in files[key]:

			frames.append(framesToMatrixFormat( \
				getFramesFromFile(getInputFilePath(file)), key))

	matrix = concat(frames)


def getMatrix() -> DataFrame:

	return matrix


def getVectorsFromMatrix(splitRatio : float) -> \
((nparray, nparray), (nparray, nparray)):

	if splitRatio < 0 or splitRatio > 1:

		return None

	x_train = []
	y_train = []
	x_test = []
	y_test = []

	vectorLength = 6 * frameLength

	for key in fileFrames:

		tempMatrix = matrix.loc[matrix["class"] == key]
		tempMatrixRows = tempMatrix.shape[0]

		for i in range(int(tempMatrixRows * splitRatio)):
			x_train.append(tempMatrix.iloc[i][ : vectorLength])
			y_train.append(tempMatrix.iloc[i][vectorLength])

		for i in range(int(tempMatrixRows * splitRatio + 1), tempMatrixRows):

			x_test.append(tempMatrix.iloc[i][ : vectorLength])
			y_test.append(tempMatrix.iloc[i][vectorLength])

	x_train = asarray(x_train, dtype=float32)
	y_train = asarray(y_train, dtype=uint8)
	x_test = asarray(x_test, dtype=float32)
	y_test = asarray(y_test, dtype=uint8)

	return (x_train, y_train), (x_test, y_test)


def init() -> bool:

	global files
	global maxFrameLength

	try:

		temp = listdir("input")
		temp.remove("project.xml")

		for file in temp:

			key = int(file.split("_")[1].split(".")[0])
			
			if key in files:

				files[key].append(file)

			else:

				files[key] = [file]

			if key in fileFrames:

				fileFrames[key] += 1

			else:

				fileFrames[key] = 1

			fileLength = len(read_csv(getInputFilePath(file)))
			
			if fileLength < maxFrameLength:

				maxFrameLength = fileLength

	except:

		return False

	return True
