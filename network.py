#!/usr/bin/env python

from networktables import NetworkTables
import coordinates

class Network:

	def __init__(self):
		NetworkTables.initialize(server='roborio-5635-frc.local')
		self.coordinates = NetworkTables.getTable('coordinates')
		print("Connected to RoboRIO via NetworkTables")

	def send(self, coordinates):
		self.coordinates.putNumber('angle', coordinates.angle)
		self.coordinates.putNumber('distance', coordinates.distance)
