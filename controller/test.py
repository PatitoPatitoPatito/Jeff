#!/usr/bin/env python

#import movement
import cario
import time
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BOARD)
pin = cario.CARIO(22)
pin.ON()
time.sleep(3)
pin.OFF()
