#!/usr/bin/env python

from controller import movement
import time

car = movement.Movement()

'''car.turnLeft()
time.sleep(2)
car.halt()

time.sleep(1)

car.turnRight()
time.sleep(2)
car.halt()

time.sleep(1)
'''
car.moveForwardLeft()
time.sleep(0.5)
car.halt()

time.sleep(5)

car.moveForwardRight()
time.sleep(0.5)
car.halt()
