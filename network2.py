#!/usr/bin/env python

import socket
import sys
import coordinates
import threading
import utils

class Network:
        def __init__(self, ip, port):
                self.port = port
                self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.server_address = (ip, self.port)
		self.number = 0
                print("Attempting to connect to "+str(ip)+":"+str(self.port))
                self.sock.connect(self.server_address)

	def background(self):
		self.sock.sendall(message)

        def send(self, message):
                def background():
			try:
				length = len(message)
				byto = utils.int_to_bytes(length, 4)
				self.sock.send(buffer(byto))
				self.sock.send(message)
			kkk

		sender = threading.Thread(target=background)
		sender.daemon = True
		sender.start()
