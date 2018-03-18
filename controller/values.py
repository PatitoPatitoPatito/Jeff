#!/usr/bin/env python

import movement
import threading
import time
import coordinates

class Values:
	def __init__(self):
		self._lastdata  = -100
		self._lastacted = -200
		self._angle     = -100
		self._distance  = -100

		self.car = movement.Movement()

		self.targetThread = None
		self._startthread()

	def _goToTarget(self):
		while True:
			if self._lastdata > self._lastacted and self._distance > 1:
				if self._angle < -1:
					self.car.moveForwardLeft()
				elif self._angle > 1:
					self.car.moveForwardRight()
				else:
					self.car.moveForward()
				self._lastacted = time.time()
			elif time.time() > self._lastacted+0.2:
				self.car.halt()

	def _startthread(self):
		self.targetThread = threading.Thread(target=self._goToTarget)
		self.targetThread.daemon = True
		self.targetThread.start()

	def feed(self, coords):
		self._angle    = coords.angle
		self._distance = coords.distance
		self._lastdata = time.time()
