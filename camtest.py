#!/usr/bin/env python

import cv2
import camera
import time

camera  = camera.Camera()
matrice = None

def click_get_hsv(event, x, y, flags, param):
	if event == cv2.EVENT_LBUTTONDOWN:
		print("("+str(x)+","+str(y)+"): "+str(matrice[y][x]))

cv2.namedWindow('test')
cv2.setMouseCallback('test', click_get_hsv)

while True:
	if camera.latest != -1 and camera.latest < time.time():
		matrice = cv2.cvtColor(camera.tomat(), cv2.COLOR_BGR2HSV)
		cv2.imshow('test', matrice)
		cv2.waitKey(27) & 0xFF == ord('q')
