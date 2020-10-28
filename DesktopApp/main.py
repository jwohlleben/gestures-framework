#!/usr/bin/python3

import module_display as mdp
import module_requirements as mrq
import module_project as mpj
import module_mlma as mlm
import module_input as mip
import module_plot as mpl

from os import path, chdir
from sys import exit


try:

	chdir(path.dirname(path.abspath(__file__)))

except:

	mdp.printError("Couldn't change path. Exiting.")
	exit(1)


while True:

	mdp.clear()
	mdp.printHeader()
	print()
	mdp.printWithSeperator("Main Menu")
	print()
	print("[1] Check Requirements")
	print("[2] Check Project")
	print("[3] Plot Data")
	print("[4] MLM Assistant")
	print("[9] Exit (STRG + C)")
	print()

	choice = mip.getNumericListChoice([1, 2, 3, 4, 9], None)

	if choice is 1:

		mdp.clear()
		mrq.run()
		print()
		mdp.pause()

	elif choice is 2:

		mdp.clear()
		mpj.run()
		print()
		mdp.pause()

	elif choice is 3:

		mdp.clear()
		mpl.run()
		print()
		mdp.pause()

	elif choice is 4:

		mdp.clear()
		mlm.run()
		print()
		mdp.pause()

	elif choice is 9:

		print("\nBye!")
		exit(0)

	else:

		mdp.clear()
		mdp.printError("Please use only available numbers\n")
		mdp.pause()
		continue

mdp.printError("This should not happen... Exiting")
exit(99)
