#!/usr/bin/env python

import socket
import sys
import coordinates
import threading
import utils
import time

class Network:
        def __init__(self, ip, port):
                self.port = port
                self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.server_address = (ip, self.port)
		self.number = 0
                print("Attempting to connect to "+str(ip)+":"+str(self.port))
		while True:
			try:
                		self.sock.connect(self.server_address)
				break
			except:
				print("Failed to connect! Retrying in 5s...")
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
			self.sock.send(ba)
			self.sock.send(message)

		sender = threading.Thread(target=background)
		sender.daemon = True
		sender.start()
