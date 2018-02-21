#!/usr/bin/env python

import socket
import sys
import coordinates
import threading
import utils
import time

class Network:
        def __init__(self, ip, port):
                self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.server_address = (ip, port)
		self.alive = False

	def connect(self):
                print("Attempting to connect to "+str(self.server_address[0])+":"+str(self.server_address[1]))
		while True:
			try:
				print("Successfully connected!")
                		self.sock.connect(self.server_address)
				self.alive = True
				break
			except:
				if self.server_address[0] == '10.56.35.2':
					dest = "roboRIO"
				else:
					dest = "DriverStation"
				print("Failed to connect! Retrying reconnection to " + dest + " in 5s...")
				time.sleep(5)


        def send(self, message):
                def background():
			length = len(message)
			byto = utils.int_to_bytes(length, 4)
			ba = bytearray()
			ba.append(byto[0])
                        ba.append(byto[1])
                        ba.append(byto[2])
                        ba.append(byto[3])
			try:
				self.sock.send(ba)
				self.sock.send(message)
			except:
				print("Connection seems to have died!")
				self.alive = False

		sender = threading.Thread(target=background)
		sender.daemon = True
		sender.start()

	def halt(self):
		self.sock.close()
