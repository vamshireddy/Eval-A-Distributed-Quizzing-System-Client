Quizing-System-Client
=====================

Client Code for Classroom Quizing System on Android

* ABOUT THE PROJECT
  * This project was Develeloped by me and my team mates for IIT Bombay. It is mainly focussed on student's interaction in the classroom.
  To help teacher's in the classroom evaluating the students regarding the material he/she has taught, we have designed a Networked Application which runs distributedly on the AAKASH Tablets. Students form groups and can ask questions to other groups. These questions will go through the teacher and gets authenticated. The Authenticated questions will be shown to all other people in the classroom and they can start answering. The answers are recorded on the server and marks are alloted accordingly.

* COMPONENTS OF THE APPLICATION
  * We have used an Access point for connecting all the tablets and the Server.
  * Aakash tablets were used as the clients

<<<<<<< HEAD
* Problems faced
  ----> The Packets which are broadcasted by UDP from the server are not reliable. I handled it by sending multiple of them at    a time so that atleast
  one of them could reach its destination.
  ----> I have used an ACK for UDP packets to make it reliable than using raw UDP which lacks reliability. When client sends a    packet, server will ack it
  ----> Acking server broadcast packets by the clients will generate a lot of packets and server needs to wait for all the packets or for a a timeout
=======
* PROBLEMS FACED

  * The Packets which are broadcasted by UDP from the server are not reliable. I handled it by sending multiple of them at a time so that atleast
  one of them could reach its destination.
  * I have used an ACK for UDP packets to make it reliable than using raw UDP which lacks reliability. When client sends a packet, server      will ack it
  * Acking server broadcast packets by the clients will generate a lot of packets and server needs to wait for all the packets or for a a    timeout 
>>>>>>> 95a4d86f66427b684ddcdac260da235795ce9d35
  and send it again. Rather than using this, i have sent it multiple of them at a time.
  
* FLOW OF THE CLIENT CODE
  * Client will initially login into the App
