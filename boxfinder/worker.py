#!/usr/bin/env python

from __future__ import division
import cv2
import numpy as np
import numpy
import math
import coordinates
import utils
import time

#SHAPEZ
##################
MORPH = 7
CANNY = 250
##################
_width  = 320
_height = 240
_margin = 0.0
##################

corners = np.array(
	[
		[[  		_margin, _margin 			]],
		[[ 			_margin, _height + _margin  ]],
		[[ _width + _margin, _height + _margin  ]],
		[[ _width + _margin, _margin 			]],
	]
)

pts_dst = np.array( corners, np.float32 )
#END SHAPEZ

_fov           = 60
_meterdiff     = 95

#derived variables
_area   = _width * _height
_middle = _width / 2
_single = _width / _fov

#functions
def process(picture):
	blurred = blur(picture)
	border = addborder(blurred, 6, [0,0,0])
	hsv = converttohsv(border)
	cfilter = filterbycolor(hsv)
	masked = cv2.bitwise_and(blurred,blurred,mask=cfilter)
	contours = findinnercontours(masked)
	rectcont = findmaxrectangle(contours)
	if rectcont is not None:
		cX   = findcenter(rectcont)
		diff = finddiff(rectcont)
		coords = getcoords(cX, diff)
		directtorect(picture, cX)
		return masked, coords
	else:
		return masked, coordinates.Coordinates()

def blur(matrice):
	return cv2.blur(matrice,(5,5))

def sharpen(matrice):
	kernel = np.array([[-1,-1,-1], [-1,9,-1], [-1,-1,-1]])
	return cv2.filter2D(matrice, -1, kernel)

def addborder(matrice, size, color):
	for s in range(0,size):
		for x in range(0,_width-s):
			matrice[0+s,x]=color
			matrice[_height-1-s,x]=color
		for y in range(0,240-s):
			matrice[y,0+s]=color
			matrice[y,_width-1-s]=color
	return matrice

def converttohsv(matrice):
	return cv2.cvtColor(matrice, cv2.COLOR_BGR2HSV)

def filterbycolor(matrice):
	return cv2.inRange(matrice, utils.lower, utils.upper)

def medianCanny(matrice, thresh1, thresh2):
	median = numpy.median(matrice)
	matrice = cv2.Canny(matrice, int(thresh1 * median), int(thresh2 * median))
	return matrice

def findinnercontours(matrice):
	blue, green, red = cv2.split(matrice)

	blue_edges = medianCanny(blue, 0.2, 0.3)
	green_edges = medianCanny(green, 0.2, 0.3)
	red_edges = medianCanny(red, 0.2, 0.3)

	edges = blue_edges | green_edges | red_edges
	kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (MORPH, MORPH))
	closed = cv2.morphologyEx(edges, cv2.MORPH_CLOSE, kernel)

#	hierarchy, contours, _ = cv2.findContours(closed, cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
	hierarchy, contours, _ = cv2.findContours(closed, cv2.RETR_CCOMP, cv2.CHAIN_APPROX_SIMPLE)
	return contours

def boundcontours(picture, contours):
	for contour in contours:
		if cv2.contourArea(contour) > _area * 0.05:
			rectangle = cv2.boundingRect(contour)
			x,y,w,h = rectangle
			cv2.rectangle(picture,(x,y),(x+w,y+h),(0,255,0),2)

def findmaxrectangle(contours):
	maxrect = None
	maxarea = _area * 0.03

	for contour in contours:
		area  = cv2.contourArea(contour)
		if area >= maxarea:
			bound = cv2.contourArea(np.int0(cv2.boxPoints(cv2.minAreaRect(contour))))
#			if area >= 0.85 * bound:
			maxrect = contour
			maxarea = area

	return maxrect

def findcenter(contour):
	M  = cv2.moments(contour)
	cX = int(M["m10"] / M["m00"])
	return cX

def finddiff(contour):
	top    = tuple(contour[contour[:, :, 1].argmin()][0])
	bottom = tuple(contour[contour[:, :, 1].argmin()][0])
	return bottom[1] - top[1]

def measureangle(cX):
	return (_middle - cX) / _single

def measuredistance(diff):
	if diff == 0:
		return 0
        return meterdiff / diff

def getcoords(cX, diff):
	coordinates.angle    = measureangle(cX)
	coordinates.distance = measuredistance(diff)
	coordinates.latest   = time.time()
	return coordinates

def directtorect(picture, cX):
	cv2.line(picture, (cX, 0), (cX, 240), (0,255,0), 2)
