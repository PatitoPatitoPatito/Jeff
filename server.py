#!/usr/bin/env python

import sys
import tcp
import utils
import threading

#cconn, iconn, dconn = None, None, None

def cbackground():
	cconn = tcp.Tcp(42069)
	cconn.start()

def ibackground():
	iconn = tcp.Tcp(6969)
	iconn.start()

def dbackground():
	dconn = tcp.Tcp(6666)
	dconn.start()

cserver = threading.Thread(target=cbackground)
cserver.daemon = True
cserver.start()

iserver = threading.Thread(target=ibackground)
iserver.daemon = True
iserver.start()

dserver = threading.Thread(target=dbackground)
dserver.daemon = True
dserver.start()

while True:
        if utils.getch() == 'q':
		cconn.halt()
		iconn.halt()
		dconn.halt()
		print("Stopped all servers, quitting")
		sys.exit()
