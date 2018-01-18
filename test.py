#!/usr/bin/env python

import cv2
import camera
import worker
import time

camera = camera.Camera()

while 1==1:
	raw_input()
	mat = worker.process(camera.tomat())
	cv2.imwrite("images/"+str(time.time())+".jpg", mat)
	print("Picture taken and filtered")
