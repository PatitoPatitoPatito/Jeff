#!/usr/bin/env python

import threading
import time
import sys

def getch():
        import sys, tty, termios
        fd = sys.stdin.fileno()
        old_settings = termios.tcgetattr(fd)
        try:
            tty.setraw(sys.stdin.fileno())
            ch = sys.stdin.read(1)
        finally:
            termios.tcsetattr(fd, termios.TCSADRAIN, old_settings)
        return ch

def background():
	if getch() == 'q':
		other_function()
		sys.exit()

def other_function():
	print("disarmed")

threading1 = threading.Thread(target=background)
threading1.daemon = True
threading1.start()

while True:
	time.sleep(1)
	print("type disarm")
