#!/usr/bin/env python

import time

class Coordinates:

	def __init__(self):
		self.angle    = -100
		self.distance = -100
		self.latest   = -100

	def found(self):
                if self.angle == -100:
                        return False
                return True

	def __str__(self):
		return str(self.angle) + ',' + str(self.distance)
