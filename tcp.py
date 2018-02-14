#!/usr/bin/env python

import socket
import sys
import threading
import utils
import cv2
import time

class Tcp:
	def __init__(self, port):
		self.server_address = ('localhost', port)
#		self.number = 0

	def start(self):
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		#print >>sys.stderr, 'starting up on %s port %s' % server_address
		self.sock.bind(self.server_address)

		self.sock.listen(1)

		self.connection = None

		while True:
			print("Disconnected. Waiting for a connection...")
			self.connection, self.client_address = self.sock.accept()

			try:
				print("Connected to client " + self.client_address[0] + " on port " + str(self.client_address[1]))

				while True:
					self.data = self.connection.recv(65536)
#					self.number += 1
					cv2.imwrite("images/"+str(time.time())+".jpg", utils.strtomat(self.data))
					#print >>sys.stderr, 'Received "%s"' % self.data
#					print("Received " + self.number + " image(s)")
					print("Received image")
					if self.data:
						self.connection.sendall(self.data)
					else:
						break

			except:
				#self.connection.close()
				a=1
#			finally:
#				self.connection.close()

	def halt(self):
		try:
			self.connection.close()
			self.server.daemon = False
		except:
			a=1
