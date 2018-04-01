#!/usr/bin/env python

from __future__ import division
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

		self._needtostop = False
	#	self.targetThread = None
	#	self._startthread()

	def _goToTarget(self):
		#print("Going to target with angle " + str(self._angle))
		if self._lastdata > self._lastacted and self._distance > 1:
			#dontactbelowangle = 2 / self._distance
			if self._angle < -4.5:
				self.car.moveForwardLeftFor(0.15)
				self._needtostop = True
				print("Going forward-left with angle " + str(self._angle))
			elif self._angle > 4.5:
				self.car.moveForwardRightFor(0.15)
				self._needtostop = True
				print("Going forward-right with angle " + str(self._angle))
			else:
				self.car.moveForward()
				self._needtostop = True
				print("Going forward with angle " + str(self._angle))
			self._lastacted = time.time()
		elif time.time() > self._lastacted+0.2:
			self.car.halt()
			if self._needtostop:
				self.car.moveBackwardFor(0.2)
				self._needtostop = False

	def feed(self, coords):
		self._angle    = coords.angle
		self._distance = coords.distance
		self._lastdata = time.time()
		self._goToTarget()

	def halt(self):
		self.car.halt()
