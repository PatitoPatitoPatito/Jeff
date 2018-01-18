#!/usr/bin/env python

import cv2
import numpy as np
import coordinates

#dynamic variables
lower_yellow = np.array([52,69,40])
upper_yellow = np.array([70,90,80])
#lower_yellow = np.array([30,40,0])
#upper_yellow = np.array([100,100,100])
camera_width = 480
camera_fov   = 60

#derived variables
camera_middle = camera_width / 2
camera_single = camera_width / camera_fov

#functions
def process(picture): #TODO revise processing model
	blurred = blur(picture)
	hsv = converttohsv(blurred)
	cfilter = filterbycolor(hsv)
	return hsv
	#sfilter = filterbyshape(cfilter)
	#keypoints = getkeypoints(sfilter)
	#coordinates = getcoords(keypoints)
	#return coordinates

def blur(matrice):
	return cv2.blur(matrice,(5,5))

def converttohsv(matrice):
	return cv2.cvtColor(matrice, cv2.COLOR_BGR2HSV)

def filterbycolor(matrice):
	return cv2.inRange(matrice, lower_yellow, upper_yellow)

def filterbyshape(matrice):
	#TODO
	return

def getkeypoints(matrice):
	#TODO
	return

def getcoords(keypoints):
	coordinates = Coordinates()
	if (keypoints.size() != 1):
		coordinates.angle    = -1
		coordinates.distance = -1
		return coords
	coordinates.angle    = measureangle(keypoints[0])
	coordinates.distance = measuredistance(keypoints[0])
	return coords

def measureangle(keypoint):
	return (camera_middle - keypoint.pt.x) / camera_single

def measuredistance(keypoint):
	#TODO
	return
