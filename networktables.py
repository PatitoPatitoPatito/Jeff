#!/usr/bin/env python

from networktables import NetworkTables
import coordinates

class Network:

	def __init__(self, ip):
		NetworkTables.initialize(server=ip)
		self.coordinates = NetworkTables.getTable('coordinates')
		print("Connected to RoboRIO via NetworkTables")

	def send(self, coordinates):
		self.coordinates.putNumber('angle', coordinates.angle)
		self.coordinates.putNumber('distance', coordinates.distance)
		print("Sending ("+str(coordinates.angle)+","+str(coordinates.distance)+")")
