#!/usr/bin/env python

import RPi.GPIO as GPIO
import time

GROUND = 39
IOPIN  = 11

class Movement:
	def Movement(self):
		GPIO.setmode(GPIO.BOARD)
		print("Set mode board")
		GPIO.setup(IOPIN, GPIO.out)

	def _on(self, delay):
		GPIO.output(IOPIN, GPIO.HIGH)
		time.sleep(delay)
		GPIO.output(IOPIN, GPIO.LOW)

while True:
	GPIO.output(12, GPIO.HIGH)
	time.sleep(1)
	print("on")
	GPIO.output(12, GPIO.LOW)
	time.sleep(1)
	print("off")
