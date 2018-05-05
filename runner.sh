#!/bin/bash

while true
do
	python runner.py
	python controller/stopcar.py
	sleep 1
done
