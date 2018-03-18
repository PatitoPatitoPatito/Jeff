#!/usr/bin/env python

import RPi.GPIO as GPIO
import time

GROUND = 14
#IOPIN_MAIN = 11
IOPIN_SWITCH_FORWARD  = 11
IOPIN_SWITCH_BACKWARD = 13

GPIO.setmode(GPIO.BOARD)
GPIO.cleanup(IOPIN_SWITCH_FORWARD)
GPIO.cleanup(IOPIN_SWITCH_BACKWARD)
GPIO.setup(IOPIN_SWITCH_FORWARD, GPIO.OUT)
GPIO.setup(IOPIN_SWITCH_BACKWARD, GPIO.OUT)

while True:
	GPIO.output(IOPIN_SWITCH_FORWARD, GPIO.LOW)
	print("on 11")
	time.sleep(0.5)
	GPIO.output(IOPIN_SWITCH_FORWARD, GPIO.HIGH)
	print("off 11")
	time.sleep(8)
	GPIO.output(IOPIN_SWITCH_BACKWARD, GPIO.LOW)
	print("on 13")
	time.sleep(0.5)
	GPIO.output(IOPIN_SWITCH_BACKWARD, GPIO.HIGH)
	print("off 13")
	time.sleep(8)
