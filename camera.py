#!/usr/bin/env python

import numpy as np
import cv2
import time

class Camera:

	def __init__(self):
		self.capture = cv2.VideoCapture(0)
		print("Connected to camera")

	def tomat(self):
		return self.capture.read()[1]

	def tofile(self, *args):
		if (len(args) != 1):
			filename = str(time.time())+".jpg"
		else:
			filename = args[0]
		cv2.imwrite("images/"+filename, self.tomat())
		print("Captured to " + filename)

	def release(self):
		self.capture.release()
