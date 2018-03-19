#!/usr/bin/env python

import RPi.GPIO as GPIO
import cario
import time
from enum import Enum

IOPIN_SWITCH_F = 11
IOPIN_SWITCH_B = 13
IOPIN_SWITCH_L = 15
IOPIN_SWITCH_R = 16

class Direction(Enum):
	H = 0
	F = 1
	R = 2
	B = 3
	L = 4
	FL = 5
	FR = 6
	BR = 7
	BL = 8

class Movement:
	def __init__(self):
		GPIO.setmode(GPIO.BOARD)
		self.IOPIN_F = cario.CARIO(IOPIN_SWITCH_F)
		self.IOPIN_B = cario.CARIO(IOPIN_SWITCH_B)
		self.IOPIN_L = cario.CARIO(IOPIN_SWITCH_L)
		self.IOPIN_R = cario.CARIO(IOPIN_SWITCH_R)
		self.halt()
		self.dir = Direction.H

	def stopForward(self):
		self.IOPIN_F.OFF()

	def stopBackward(self):
		self.IOPIN_B.OFF()

	def stopLeft(self):
		self.IOPIN_L.OFF()

	def stopRight(self):
		self.IOPIN_R.OFF()

	def halt(self):
		self.stopForward()
		self.stopBackward()
		self.stopLeft()
		self.stopRight()

	def moveForward(self):
		self.halt()
		self.IOPIN_F.ON()
		self.dir = Direction.F

	def moveBackward(self):
		self.halt()
		self.IOPIN_B.ON()
		self.dir = Direction.B

	def turnLeft(self):
		self.IOPIN_L.ON()

	def turnRight(self):
		self.IOPIN_R.ON()

	def moveForwardLeft(self):
		self.moveForward()
		self.turnLeft()
		self.dir = Direction.FL

	def moveBackwardLeft(self):
		self.moveBackward()
		self.turnLeft()
		self.dir = Direction.BL

	def moveForwardRight(self):
		self.moveForward()
		self.turnRight()
		self.dir = Direction.FR

	def moveBackwardRight(self):
		self.moveBackward()
		self.turnRight()
		self.dir = Direction.BR

	def moveForwardFor(self, delay):
		self.moveForward()
		time.sleep(delay)
		self.halt()

	def moveBackwardFor(self, delay):
		self.moveBackward()
		time.sleep(delay)
		self.halt()

	def moveForwardLeftFor(self, delay):
		self.moveForwardLeft()
		time.sleep(delay)
		self.halt()

	def moveForwardRightFor(self, delay):
		self.moveForwardRight()
		time.sleep(delay)
		self.halt()

	def turnLeftFor(self, delay):
		self.turnLeft()
		time.sleep(delay)
		self.halt()

	def turnRightFor(self, delay):
		self.turnRight()
		time.sleep(delay)
		self.halt()
