#!/usr/bin/env python

import RPi.GPIO as GPIO
import direction

class CARIO:
	def __init__(self, pin):
		self.pin = pin
		self.on = False
#		GPIO.cleanup(self.pin)
		GPIO.setup(self.pin, GPIO.OUT)
		GPIO.output(self.pin, GPIO.LOW)

	def ON(self):
#		print("Turned pin " + str(self.pin) + " on")
		self.on = True
		GPIO.output(self.pin, GPIO.HIGH)

	def OFF(self):
#		print("Turned pin " + str(self.pin) + "off")
		self.on = False
		GPIO.output(self.pin, GPIO.LOW)
