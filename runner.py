#!/usr/bin/env python

import cv2
import camera
import worker
import network

def sigterm_handler(signal, frame):
	print('Killing...')
	camera.release()
	sys.exit(0)
signal.signal(signal.SIGTERM, sigterm_handler)

print("Launched!")

network     = network.Network()
camera      = camera.Camera()
coordinates = coordinates.Coordinates()

def run():
	picture = camera.tomat()
	coordinates = worker.process(picture)
	network.send(coordinates)

while 1==1:
	run()
