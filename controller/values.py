#!/usr/bin/env python

from __future__ import division
import movement
import threading
import time
import coordinates

class Values:
	def __init__(self):
		self._lastdata   = -100
		self._lastacted  = -200
		self._movingfrom = -100
		self._angle      = -100
		self._distance   = -100

		self.car = movement.Movement()

		self._moving = False

	def _goToTarget(self):
		if self._distance > 0.4:
			goodangle = 0.6*self._distance #bigger parameter->turns less
			if self._angle < -goodangle:
				self.car.moveLeftFor(0.2)
			elif self._angle > goodangle:
				self.car.moveRightFor(0.2)
			else:
				self.car.moveForward()
				self._moving = True
				if self._movingfrom == -100:
					self._movingfrom = time.time()
			#self._moving = True
			#if self._movingfrom == -100:
			#	self._movingfrom = time.time()
			self._lastacted = time.time()
		else:
			self.car.halt()
		#elif time.time() > self._lastacted + 0.2:
		#	self.car.halt()
		#	print("stopped")

	def feed(self, coords):
		self._angle    = coords.angle
		self._distance = coords.distance
		self._lastdata = time.time()
		self._goToTarget()

	#def stopif(self):
	#	
			

	def halt(self):
		timeacc = time.time() - self._movingfrom
		print(str(timeacc))
		if self._moving and timeacc > 0.1:
			self.car.moveBackwardFor(0.25*timeacc)
		self.car.halt()
		self._moving = False
		self._movingfrom = -100
