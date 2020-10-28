#!/usr/bin/python3

import module_display as mdp

from shutil import which
from sys import stdout, stderr, __stdout__, __stderr__
from os import devnull, environ, path


MODULES = ["matplotlib", "numpy", "pandas", "tensorflow"]
PROGRAMS = ["jupyter-notebook"]
DIRECTORIES = ["input", "output"]
FNULL = open(devnull, 'w')


def trapText():

	stdout = FNULL
	stderr = FNULL
	environ["TF_CPP_MIN_LOG_LEVEL"] = '3'


def untrapText():

	stdout = __stdout__
	stderr = __stderr__
	environ["TF_CPP_MIN_LOG_LEVEL"] = '0'


def checkModule(module : str) -> bool:

	trapText()

	try:

		__import__(module)

	except:

		untrapText()
		return False

	untrapText()
	return True


def checkProgram(program : str) -> bool:

	if which(program) is not None:

		return True

	else:

		return False


def checkDirectory(directory : str) -> bool:

	return path.exists(directory)


def run():

	errorOcurred = False
	warningOccured = False

	mdp.printWithSeperator("Check Requirements")
	print()
	mdp.printInfo("Checking modules...")

	for module in MODULES:

		if (checkModule(module)):

			mdp.printCheck(module + " installed")

		else:

			errorOcurred = True
			mdp.printCross(module + " not installed")

	
	mdp.printInfo("Done checking modules.")
	
	print()

	mdp.printInfo("Checking programs...")

	for program in PROGRAMS:

		if (checkProgram(program)):

			mdp.printCheck(program + " installed")

		else:

			warningOccured = True
			mdp.printWarning(program + " not installed")

	mdp.printInfo("Done checking programs.")

	print()

	mdp.printInfo("Checking directories...")

	for directory in DIRECTORIES:

		if (checkDirectory(directory)):

			mdp.printCheck(directory + " exists")

		else:

			errorOcurred = True
			mdp.printCross(directory + " doesn't exist")

	mdp.printInfo("Done checking directories.")

	print()

	if not errorOcurred and not warningOccured:

		print("Everything's fine. Now you're ready to go!")

	else:

		if errorOcurred:

			print("Something is missing. Certain parts of the program can't " + \
				"be used.\nPlease read the manual for further information.")
		
		else:

			print("Some programs are missing. This application can still " + \
				"be used.\nPlease read the manual for further information")
