#!/usr/bin/env python

import socket
import sys
import threading
import utils

if len(sys.argv) != 2:
	print("No port specified! Aborting")
	print("Usage: " + sys.argv[0] + " PORT")
	print("42069- COMMANDS")
	print("6969 - IMAGES")
	print("6666 - DEBUG")
	exit()
else:
	try:
		port = int(sys.argv[1])
		print("Starting TCP server on port " + str(port))
	except:
		print("Invalid port!")
		exit()

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_address = ('localhost', port)
#print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)

sock.listen(1)

connection = None

def background():
	while True:
		print("Disconnected. Waiting for a connection...")
		connection, client_address = sock.accept()

		try:
			print("Connected to client " + client_address)

			while True:
				data = connection.recv(16)
				print >>sys.stderr, 'Received "%s"' % data
				if data:
					connection.sendall(data)
				else:
					break

		finally:
			connection.close()

runner = threading.Thread(target=background)
runner.daemon = True
runner.start()

while True:
        if utils.getch() == 'q':
                print("HALT")
		try:
			connection.close()
		except:
			a=1
                sys.exit()
