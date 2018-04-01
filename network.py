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
                self.server_address = (ip, port)
		self.alive = False
		self.found = False
		self.attempt = 0
		self.latest = -100
		self.movement = ""

	def connect(self):
                print("Attempting to connect to "+str(self.server_address[0])+":"+str(self.server_address[1]))
		while True:
			self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.sock.settimeout(3)
			try:
                		self.sock.connect(self.server_address)
				#rec = self.sock.recv(8)
				#rec can be anything
				#the intended use is having it convey parameters
				#obviously it will only be sent once at start
				'''print(str(int(byte(rec[0]))))
				if rec[0] == byte(0xFA):
					self.debug = false
				elif rec[0] == byte(0xFB):
					self.debug = true
				else:
					raise Exception("Invalid parameter")
				if rec[1] == byte(0xFA):
					self.dummy = false
				elif rec[1] == byte(0xFB):
					self.dummy = true
				else:
					raise Exception("Invalid parameter")
				print("Received parameters")'''
				print("Successfully connected")
				self.alive = True
				break
			except:
				print("Failed to connect! Retrying reconnection to " + self.server_address[0] + ":" + str(self.server_address[1]) + " in 1s...")
				time.sleep(1)

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
				passed = time.time() - self.latest
				if passed < 0.03:
					time.sleep(0.03 - passed)
				self.sock.send(ba)
				self.sock.send(message)
				#if random.randint(1,10) != 6:
				self.sock.send('FE'.decode('hex'))
				self.sock.send('FE'.decode('hex'))
				self.sock.send('FE'.decode('hex'))
				self.sock.send('FE'.decode('hex'))
				if self.found:
					self.sock.send('FF'.decode('hex'))
				else:
					self.sock.send('FE'.decode('hex'))
				rec = self.sock.recv(8)[2:]
				if "ack" not in rec:
					raise Exception("Did not receive reply")
				#print("got ack")
			except Exception as e:
				print("Connection seems to have died!")
				print(str(e))
				self.alive = False

		sender = threading.Thread(target=background)
		sender.daemon = True
		sender.start()

	def _checker(self):
		while True:
			state = ""
			while True:
				char = self.sock.recv(1)
				if ord(char) != 0:
					state = state + char
				else:
					break
			self.movement = state[1:-2]

	def startcheck(self):
		checker = threading.Thread(target=self._checker)
		checker.daemon = True
		checker.start()

	def halt(self):
		self.sock.close()
