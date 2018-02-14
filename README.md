# BoxFinder

This project was written for Demacia 5635 team for usage in the FRC 2018 robotics competition, but feel free to reuse whatever you may find useful in this code.

In theory, detects the closest rectangular object that is colored within a pre defined color range, and sends a command to a user defined IP address with the appropriate coordinates


TODO:

- *Define the appropriate HSV range in worker.py for yellow objects*- this will obviously not work if this is not done

- Test on a real robot


In order to make it so that the coordinates are actually sent, lines 29-31, 44 and 47 need to be uncommented. For it to actually work, the server(s) need(s) to accept ports: 42069, 6969, 6666. 42069 for commands, 6969 for video and 6666 for debugging (not implemented yet but can be accessed simply by using dconn.sent("your desired string") in runner.py
