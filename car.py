#!/usr/bin/env python

from boxfinder import values as boxvalues
#from controller import values as carvalues

from boxfinder import worker
import cv2

bvalues = boxvalues.Values()
#cvalues = carvalues.Values()

matrice = None

def click_get_hsv(event, x, y, flags, param):
	if event == cv2.EVENT_LBUTTONDOWN:
		tmp = cv2.cvtColor(matrice, cv2.COLOR_BGR2HSV)
		print("("+str(x)+","+str(y)+"): "+str(tmp[y][x]))

cv2.namedWindow('custom')
cv2.setMouseCallback('custom', click_get_hsv)

while bvalues.picture is None or bvalues.custom is None:
	pass

while True:
	#cvalues.feed(bvalues.coordinates.angle)
	if bvalues.coordinates:
		print(bvalues.coordinates)
	matrice = worker.hidecolor(bvalues.picture, bvalues.custom)
	cv2.imshow('custom', worker.directtorect(matrice, bvalues.cX))
	#cv2.imshow('picture', worker.directtorect(bvalues.picture, bvalues.cX))
        cv2.waitKey(27) & 0xFF == ord('q')
