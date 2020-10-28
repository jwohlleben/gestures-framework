from time import time, localtime


def currTime() -> int:

	return time()


def timestamp() -> str:

	time = localtime()
	string = ""

	for i in range(6):

		string += str(time[i]).zfill(2)

	return string


def timeDiff(ts1 : int, ts2 : int) -> str:

	difference = abs(ts1 - ts2)
	seconds = str(round(difference % 60, 2))
	minutes = str(int(difference / 60))

	return minutes + " m " + seconds + " s"
