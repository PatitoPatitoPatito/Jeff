#!/usr/bin/env python

import cv2
import camera
import worker
import time
import coordinates
import threading
import utils
import time
import network
from controller import values as carvalues

camera = camera.Camera()
coordinates = coordinates.Coordinates()

car = carvalues.Values()

pip = '192.168.220.64'
iconn = network.Network(pip, utils.iport)
cconn = network.Network(pip, utils.cport)

def run():
	mat = camera.tomat().copy()
	picture,coords,_ = worker.process(mat)
	if cconn.movement != "":
		if cconn.movement[0] == 'u' and cconn.movement[2] == 'u':
			car.car.stopY()
		if cconn.movement[1] == 'u' and cconn.movement[3] == 'u':
			car.car.stopX()
		if cconn.movement[0] == 'p':
			car.car.moveForward()
		if cconn.movement[1] == 'p':
			car.car.turnRight()
		if cconn.movement[2] == 'p':
			car.car.moveBackward()
		if cconn.movement[3] == 'p':
			car.car.turnLeft()
		if coords:
			iconn.found = True
			if cconn.movement[4] != 'u':
				print("Feeding " + str(coords))
				car.feed(coords)
		else:
			iconn.found = False
#	matstr = utils.mattostr(worker.directtorect(mat,coords.cX))
	matstr = utils.mattostr(picture)
	iconn.send(matstr)


while camera.tomat() is None:
	pass

print("Launched!")

while True:
	iconn.connect()
	cconn.connect()
	cconn.startcheck()
	while cconn.alive and iconn.alive:
		if camera.latest > coordinates.latest:
			run()
print("Shutting down")
