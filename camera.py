#!/usr/bin/env python

import numpy as np
import cv2
import time
import threading
import utils

fov=60

class Camera:
	def __init__(self):
		while True:
			try:
				self.capture = cv2.VideoCapture(utils.camera)
				self.capture.set(3, 320)
				self.capture.set(4, 240)
				self.capture.set(cv2.CAP_PROP_FPS, 12)
				self.latest = -1
				self.mat = None
				self._startthread()
				if not self.works():
					raise Exception("Camera is unusable")
				print("Started camera service")
				break
			except:
				print("Failed to connect to camera! Retrying in 2s...")
				time.sleep(2)

	def _start(self):
		while True:
			self.mat    = self.capture.read()[1]
			self.latest = time.time()

	def _startthread(self):
		camera = threading.Thread(target=self._start)
		camera.daemon = True
		camera.start()

	def tomat(self):
		return self.mat

	def works(self):
		if self.capture is None or not self.capture.isOpened():
			return False
		return True

	def release(self):
		self.capture.release()
