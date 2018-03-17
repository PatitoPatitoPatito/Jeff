#!/usr/bin/env python

import RPi.GPIO as GPIO

class IOPIN:
	def IOPIN(self, pin):
		self.pin = pin
		GPIO.setup(self.pin, GPIO.OUT)

	def ON(self):
		GPIO.output(self.pin, GPIO.HIGH)

	def OFF(self):
		GPIO.output(self.pin, GPIO.LOW)
