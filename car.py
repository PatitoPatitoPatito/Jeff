#!/usr/bin/env python

from boxfinder import values as boxvalues
#from controller import values as carvalues
import cv2

bvalues = boxvalues.Values()
#cvalues = carvalues.Values()

while True:
	if values.picture is not None and values.custom is not None:
		while True:
			#cvalues.feed(bvalues.coordinates.angle)
			cv2.imshow('picture', bvalues.picture)
			cv2.imshow('custom', bvalues.custom)
		        cv2.waitKey(27) & 0xFF == ord('q')