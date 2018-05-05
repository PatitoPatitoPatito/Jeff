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

pip = '192.168.220.82'
iconn = network.Network(pip, utils.iport)
cconn = network.Network(pip, utils.cport)
dconn = network.Network(pip, utils.dport)

def run():
	mat = camera.tomat().copy()
	#picture = mat.copy()
	picture,coords,_ = worker.process(mat, dconn.dinput)
	#print(cconn.movement)
	if cconn.movement != "":
		if cconn.movement[4] == 'u':
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
		elif cconn.movement[4] == 'p':
			if coords:
				iconn.found = True
				print("Feeding " + str(coords))
				car.feed(coords)
			else:
				car.halt()
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
	dconn.connect()
	cconn.startcheck()
	dconn.startcheck()
	while cconn.alive and iconn.alive and dconn.alive:
		if camera.latest > coordinates.latest:
			run()
	break
print("Shutting down")
