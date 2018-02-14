#!/usr/bin/env python

import cv2
import camera

camera = camera.Camera()
while True:
	if camera.latest != -1:
		cv2.imshow("test", camera.tomat())
		cv2.waitKey(27) & 0xFF == ord('q')
