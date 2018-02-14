#!/usr/bin/env python

import time

class Coordinates:

	def __init__(self):
		self.angle    = -1
		self.distance = -1
		self.latest = -1

	def found(self):
                if (self.angle == -1 or self.distance == -1):
                        return False
                return True

	def __str__(self):
		self.latest = time.time()
		#return str(self.angle)
		return str(self.angle)+","+str(self.distance)
