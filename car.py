#!/usr/bin/env python

from boxfinder import values as boxvalues
from controller import values as carvalues
from boxfinder import worker
from network import sender
import cv2
import signal
import sys

bvalues = boxvalues.Values()
cvalues = carvalues.Values()

def signal_handler(signal, frame):
	print("Exiting safely")
	cvalues.car.halt()
	sys.exit(0)
signal.signal(signal.SIGINT, signal_handler)

matrice = None

'''def click_get_hsv(event, x, y, flags, param):
	if event == cv2.EVENT_LBUTTONDOWN:
		hsv = worker.converttohsv(bvalues.picture.copy())
		print("("+str(x)+","+str(y)+"): "+str(hsv[y][x]))

cv2.namedWindow('custom')
cv2.setMouseCallback('custom', click_get_hsv)'''

while bvalues.picture is None or bvalues.custom is None:
	pass

while True:
	if bvalues.coordinates:
		print(bvalues.coordinates)
		cvalues.feed(bvalues.coordinates)
	'''matrice = worker.hidecolor(bvalues.picture.copy(), bvalues.custom)
	cv2.imshow('custom', worker.directtorect(matrice, bvalues.cX))
	cv2.imshow('picture', worker.directtorect(bvalues.picture, bvalues.cX))
        cv2.waitKey(27) & 0xFF == ord('q')'''
signal.pause()
