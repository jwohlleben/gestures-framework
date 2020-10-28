#!/usr/bin/python3

import module_display as mdp
import module_data as data
import module_input as mip
import module_xml as xml
import module_datetime as mdt
from os import path


def run():

	mdp.printWithSeperator("MLM Assistant")
	print()
	
	print("Please make sure that all requirements are installed " \
		+ "and the project is error-free,\nby executing options " \
		+ "1 and 2 in the main menu. Continue?\n")

	choice = mip.getYNChoice(None)

	if not choice:

		return

	print("\nIf you are not sure which option to use, " + \
		"simply press ENTER to use the default setting.")
	mdp.pause()
	print()


	mdp.printInfo("Initializing tensorflow...")
	import tensorflow as tf
	tf.keras.backend.clear_session()
	print()

	ACTIVATIONS = {1: tf.keras.activations.elu, 2: tf.keras.activations.exponential, \
	 	3: tf.keras.activations.hard_sigmoid, 4: tf.keras.activations.linear, \
	 	5: tf.keras.activations.relu, 6: tf.keras.activations.selu, 7: tf.keras.activations.sigmoid, \
	 	8: tf.keras.activations.softmax, 9: tf.keras.activations.softplus, \
	 	10: tf.keras.activations.softsign, 11: tf.keras.activations.tanh }

	OPTIMIZERS = {1: tf.keras.optimizers.SGD, 2: tf.keras.optimizers.RMSprop, 3: tf.keras.optimizers.Adagrad, \
		4: tf.keras.optimizers.Adadelta, 5: tf.keras.optimizers.Adam, 6: tf.keras.optimizers.Adamax, \
		7: tf.keras.optimizers.Nadam}

	LOSSES = {1: tf.keras.losses.BinaryCrossentropy, 2: tf.keras.losses.CategoricalCrossentropy, \
		3: tf.keras.losses.MeanAbsoluteError, 4: tf.keras.losses.MeanSquaredError, \
		5: tf.keras.losses.MeanSquaredLogarithmicError, 6: tf.keras.losses.SparseCategoricalCrossentropy}

	mdp.printInfo("Initializing data...")

	if not data.init() or not xml.init():

		mdp.printError("Could not initialize data")
		return

	print("\nPlease choose the framelength")

	data.setFrameLength(mip.getIntMinMaxChoice(8, data.getMaxFrameLength(),data.getMaxFrameLength()))

	print("\nPlease choose the split ratio")
	splitRatio = mip.getFloatMinMaxChoice(0.05, 0.95, 0.8)

	print("\nHow many training epochs would you like to use?")
	epochs = mip.getIntMinMaxChoice(1, 100, 10)

	print("\nHow many hidden layers would you like to use?")
	layers = mip.getIntMinMaxChoice(0, 100, 1)
	print()

	mdp.printInfo("Creating model...\n")
	model = tf.keras.Sequential()

	neuronList = [6 * data.getFrameLength()]

	for i in range(layers + 1):

		print("Choose the number of neurons for layer " + str(i + 2) + \
			' / ' + str(layers + 2))

		if i is not layers:

			neuronList.append(mip.getIntMinMaxChoice(1, 6 * data.getFrameLength(), \
				6 * data.getFrameLength()))

		else:

			neuronList.append(mip.getIntMinMaxChoice(1, 6 * data.getFrameLength(), \
				len(xml.getAllClasses())))

	print("\nActivation Functions:")

	activationList = []

	for key in ACTIVATIONS:

		print('(' + str(key) + ') ' + ACTIVATIONS[key].__name__)

	print()

	for i in range(layers + 2):

		print("Choose activation function for layer " + str(i + 1) + \
			' / ' + str(layers + 2))

		if i is not layers + 1:
			
			activationList.append(mip.getIntMinMaxChoice(1, 11, 5))

		else:

			activationList.append(mip.getIntMinMaxChoice(1, 11, 7))

	print("\nOptimzers:")

	for key in OPTIMIZERS:

		print('(' + str(key) + ') ' + OPTIMIZERS[key].__name__)

	print("\nChoose optimizer")

	optimizer = mip.getIntMinMaxChoice(1, 7, 5)

	print("\nLoss functions:")

	for key in LOSSES:

		print('(' + str(key) + ') ' + LOSSES[key].__name__)

	print("\nChoose loss function")

	loss = mip.getIntMinMaxChoice(1, 6, 2)

	print()
	mdp.printInfo("Adding layers...")
	print()

	for i in range(layers + 2):

		model.add(tf.keras.layers.Dense(neuronList[i], \
			activation=ACTIVATIONS[activationList[i]]))

	mdp.printInfo("Building matrix...")	
	ts1 = mdt.currTime()
	data.buildMatrix()
	ts2 = mdt.currTime()
	mdp.printInfo("Done. Took " + mdt.timeDiff(ts1, ts2))
	print()

	mdp.printInfo("Building vectors...")
	ts1 = mdt.currTime()
	(x_train, y_train), (x_test, y_test) = data.getVectorsFromMatrix(splitRatio)
	ts2 = mdt.currTime()
	mdp.printInfo("Done. Took " + mdt.timeDiff(ts1, ts2))

	print()
	mdp.printInfo("Compiling model...")
	model.compile(optimizer=OPTIMIZERS[optimizer](), \
		loss=LOSSES[loss](), metrics=["accuracy"])

	print()
	mdp.printInfo("Training...")

	ts1 = mdt.currTime()
	model.fit(x_train, y_train, epochs=epochs)
	ts2 = mdt.currTime()
	mdp.printInfo("Done. Took " + mdt.timeDiff(ts1, ts2))

	print()
	mdp.printInfo("Evaluating...")

	ts1 = mdt.currTime()
	model.evaluate(x_test, y_test)
	ts2 = mdt.currTime()
	mdp.printInfo("Done. Took " + mdt.timeDiff(ts1, ts2))

	print("\nDo you want to save the model?")

	if mip.getYNChoice(None):

		ts = mdt.timestamp()

		print()
		mdp.printInfo("Saving " + ts + "_model")
		modelPath = path.join(path.curdir, "output", ts + "_model")
		#model.save(modelPath) # Doesnt work anymore...
		tf.saved_model.save(model, modelPath)
		
		mdp.printInfo("Saving " + ts + "_model.xml")
		xml.createModelFile(data.getFrameLength(), neuronList[-1], ts)

		print("\nDo you want to save the model additionally in tf-lite format?")

		if mip.getYNChoice(True):

			print()
			mdp.printInfo("Converting model...")
			converter = tf.lite.TFLiteConverter.from_saved_model(modelPath)
			tflite_model = converter.convert()

			mdp.printInfo("Saving " + ts + "_model.tflite...")
			with open(modelPath + ".tflite", "bw+") as f:
				f.write(tflite_model)

		print()
		mdp.printInfo("Everything has been saved in 'output' directory")
