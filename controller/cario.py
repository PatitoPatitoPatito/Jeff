#!/usr/bin/env python

import RPi.GPIO as GPIO

class CARIO:
	def __init__(self, pin):
		self.pin = pin
		GPIO.cleanup(self.pin)
		GPIO.setup(self.pin, GPIO.OUT)
		GPIO.output(self.pin, GPIO.HIGH)

	def ON(self):
		GPIO.output(self.pin, GPIO.LOW)

	def OFF(self):
		GPIO.output(self.pin, GPIO.HIGH)
