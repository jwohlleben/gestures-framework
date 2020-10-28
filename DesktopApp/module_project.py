#!/usr/bin/python3

import module_display as mdp
import module_xml as xml
import module_data as mda


def gaussSum(n : int) -> int:

	return int((n * (n + 1)) / 2)


def run():

	errorOcurred = False
	warningOccured = False

	mdp.printWithSeperator("Check Project")
	print()
	mdp.printInfo("Checking project.xml...")

	xmlFileAvailable = xml.init()
	description = ""
	classes = []

	if xmlFileAvailable:

		mdp.printCheck("File OK")

		name = xml.getName()

		if name is not None:

			mdp.printCheck("Name found")

		else:

			errorOcurred = True
			mdp.printCross("Name not found")

		description = xml.getDescription()

		if description is not None:

			mdp.printCheck("Description found")

		else:

			errorOcurred = True
			mdp.printCross("Description not found")

		classes = xml.getAllClasses()

		if classes is not None:

			mdp.printCheck("All classes found")

		else:

			errorOcurred = True
			mdp.printCross("Classes are defective")

		uniqueIds = True
		idSum= 0

		for element in classes:

			idSum+= element["id"]

		if idSum is not gaussSum(len(classes)):

			uniqueIds = False

		if uniqueIds:

			mdp.printCheck("Class-IDs are correct")

		else:

			errorOcurred = True
			mdp.printCross("Class-IDs have the wrong format")

	else:

		errorOcurred = True
		mdp.printCross("File damaged or not available")

	mdp.printInfo("Done checking project.xml.")
	print()
	mdp.printInfo("Start of Filedump")

	if not errorOcurred:

		print("Name:", name)
		print("Description:", description)
		print("Classes:", classes)
	
	else:

		mdp.printCross("Can't create Filedump. Please fix XML-Error first")

	mdp.printInfo("End of Filedump")
	print()

	mdp.printInfo("Checking CSV-Files...")

	if not errorOcurred:

		if mda.init():

			mdp.printCheck("Data successfully loaded")

			keyIdMissmatch = False

			for c in classes:

				if c["id"] not in mda.getFiles().keys():

					keyIdMissmatch = True
					break

			if not keyIdMissmatch:

				mdp.printCheck("Class-IDs match recorded data")

			else:

				warningOccured = True
				mdp.printWarning("Class-IDs don't match recorded data")

		else:

			errorOcurred = True
			mdp.printCross("Data could not be loaded")

	else:

		mdp.printCross("Can't check CSV-Files. Please fix XML-Error first")

	mdp.printInfo("Done checking CSV-Files")

	print()

	if not errorOcurred and not warningOccured:

		print("Everything's fine. Now you're ready to go!")

	else:

		if errorOcurred:

			print("Something is wrong. Please read the manual for further information.")

		else:

			print("Warning: Undesirable behavior may occur. Please read the manual for further information")
