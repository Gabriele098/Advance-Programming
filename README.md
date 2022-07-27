# Advance-Programming

DESCRIPTION
Implement a Graphical User Interface (GUI) or Command-Line Interface
(CLI) based networked distributed system for a group-based client-server communication, which conforms
with the following requirements. When a new member joins (connects), he/she has to provide following
parameters as an input (i.e., command-line parameters):
1. an ID (please ensure that each member is assigned a unique ID),
2. a port it will listen to, its IP address, and
3. a port and IP address of the server.

If a member is the first one, the member has to be informed about it at start-up. Then this member will
become the coordinator.
The new member(s) will then request the server to know the existing members and will receive everyone's
IDs, IP addresses and ports including the IP address, port and ID of the current group coordinator. After this,
the new member can contact everyone (through server) to let them know that they can add it to the set of
members. In case some of the members do not respond, it will inform server about this, and the server will
inform other members to update their list of existing members. However, if the coordinator does not respond,
then any new member can be a coordinator. The coordinator maintains state of group members by checking
periodically that how many of them are live and informs active members about it so that they can update
their state of members. Everyone should be able to send messages to every other member through server.
Importantly, the module that implements members does not need to accept input from keyboard after they
have got the starting parameters, but they should print out information about the various information shared
to/by the member. Any member can quit by a simple ctrl-C command.


HOW TO RUN
-Run the serverWindow first
-Then the firstWindow
-And for other customers always run the firstWindow
