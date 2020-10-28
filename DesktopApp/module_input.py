import module_display as mdp
from sys import exit


def bye():

	print("\n\nBye!")
	exit(0)


def getYNChoice(default : bool) -> bool:

	choice = ""

	while (True):

		try:

			if default is None:

				choice = input("Choice [y/n]: ")

			else:

				if default:

					choice = input("Choice (default: y) [y/n]: ")

				else:

					choice = input("Choice (default: n) [y/n]: ")

				if choice is '':

					return default

		except KeyboardInterrupt:

			bye()

		if choice is 'y' or choice is 'Y':

			return True

		elif choice is 'n' or choice is 'N':

			return False

		else:

			mdp.printError("Please use 'y' or 'n' only")


def getNumericListChoice(allowedOptions : list, default : int) -> int:

	choice = None

	while (True):

		try:

			if default is None:

				choice = input("Choice: ")

			else:

				choice = input("Choice (default: " + str(default) + "): ")

				if choice is '':

					return default

			choice = int(choice)

		except KeyboardInterrupt:

			bye()

		except:

			mdp.printError("Please use numbers only")
			continue

		if choice in allowedOptions:

			return choice

		else:

			valid = ""

			for i, element in enumerate(allowedOptions):

				valid += str(element)

				if not i is len(allowedOptions) - 1:

					valid += ", "

			mdp.printError("Invalid Choice. Valid Options are: " + valid)


def getIntMinMaxChoice(min : int, max : int, default : int) -> int:

	choice = 0

	while (True):

		try:

			if default is None:

				choice = input("Choice [" + str(min) + " -> " + str(max) + "]: ")

			else:

				choice = input("Choice (default: " + str(default) + ") " \

					'[' + str(min) + " -> " + str(max) + "]: ")

				if choice is '':

					return default

			choice = int(choice)

		except KeyboardInterrupt:

			bye()

		except:

			mdp.printError("Please use numbers only")
			continue

		if choice >= min and choice <= max:

			return choice

		else:

			mdp.printError("Invalid Choice. Entries in the interval [" + str(min) \
				+ ", " + str(max) + "] are valid")


def getFloatMinMaxChoice(min : float, max : float, default : float) -> float:

	choice = 0.0

	while (True):

		try:

			if default is None:

				choice = input("Choice [" + str(min) + " -> " + str(max) + "]: ")

			else:

				choice = input("Choice (default: " + str(default) + ") " \

					'[' + str(min) + " -> " + str(max) + "]: ")

				if choice is '':

					return default

			choice = float(choice)

		except KeyboardInterrupt:

			bye()

		except:

			mdp.printError("Please use floating point numbers only")
			continue

		if choice >= min and choice <= max:

			return choice

		else:

			mdp.printError("Invalid Choice. Entries in the interval [" + str(min) \
				+ ", " + str(max) + "] are valid")


def getStringChoice(default : str, allowedOptions : list) -> str:

	while(True):

		try:

			if default is None:

				choice = input("Choice: ")

			else:

				choice = input("Choice (default: " + default + "): ")

		except KeyboardInterrupt:

			bye()

		if choice in allowedOptions:

			return choice

		mdp.printError("Invalid Choice")
