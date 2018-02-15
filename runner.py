#!/usr/bin/env python

import cv2
import camera
import worker
import time
import coordinates
import network
import sys
import threading
import utils
import time

if len(sys.argv) != 3:
	print("=====ATTENTION=====")
	print("Usage: " + sys.argv[0] + " ROBOTIP DRIVERIP")
	print("No IP addresses specified! Defaulting to 10.56.35.2 and 10.56.35.189")
	print("=====ATTENTION=====")
	rip = '10.56.35.2'
	dip = '10.56.35.189'
else:
	rip = sys.argv[1]
	dip = sys.argv[2]

#utils.getchthread()
print("Launched!")

camera = camera.Camera()

#cconn       = network.Network(rip, utils.cport)
#iconn       = network.Network(dip, utils.iport)
#dconn       = network.Network(rip, utils.dport)

coordinates = coordinates.Coordinates()

def run():
	mat = camera.tomat()
	hsv,coordinates = worker.process(mat)
	lineThickness = 2
	cv2.line(hsv, (coordinates.distance, 0), (coordinates.distance, 240), (0,255,0), lineThickness)
	#cv2.imshow('mat', mat)
	#cv2.waitKey(27) & 0xFF == ord('q')
#	cconn.send(str(coordinates))
	print(str(coordinates))
	coordinates.latest = time.time()
	matstr = utils.mattostr(hsv)
#	iconn.send(matstr)

def background():
	while True:
		#try:
			if camera.latest != -1 and camera.latest > coordinates.latest:
				run()
		#except:
		#	print("Error occurred")
		#	break

runner = threading.Thread(target=background)
runner.daemon = True
runner.start()

while True:
	if utils.getch() == 'q':
		print("\nHALT")
		sys.exit()
