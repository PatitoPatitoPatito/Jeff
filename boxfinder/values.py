#!/usr/bin/env python

import cv2
import camera
import worker
import time
import coordinates
import threading
import utils
import time

class Values:
	def __init__(self):
		self.picture = None
		self.custom  = None
		self.camera = camera.Camera()
		self.coordinates = coordinates.Coordinates()
		self._startthread()

	def _gather(self):
        	self.picture = self.camera.tomat()
        	self.custom, self.coordinates = worker.process(self.picture)

	def _thread(self):
		print("Started gathering values!")
		while True:
			if self.camera.latest > self.coordinates.latest:
				self._gather()

	def _startthread(self):
		self.gatherer = threading.Thread(target=self._thread)
		self.gatherer.daemon = True
		self.gatherer.start()
