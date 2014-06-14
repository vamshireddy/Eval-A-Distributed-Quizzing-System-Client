Quizing-System-Client
=====================

Client Code for Classroom Quizing System on Android

* ABOUT THE PROJECT
  * This project was Develeloped by me and my team mates for IIT Bombay. It is mainly focussed on the student's interaction in the classroom.
  To help the teacher in the classroom evaluating the students about the material he/she taught we have designed an Networked Application which runs distributedly on the AAKASH Tablets
  and interact with the teacher.

* COMPONENTS OF THE APPLICATION
  * We have used an Access point for connecting all the tablets and the Server.
  * Aakash tablets were used as the clients

* PROBLEMS FACED

  * The Packets which are broadcasted by UDP from the server are not reliable. I handled it by sending multiple of them at a time so that atleast
  one of them could reach its destination.
  * I have used an ACK for UDP packets to make it reliable than using raw UDP which lacks reliability. When client sends a packet, server      will ack it
  * Acking server broadcast packets by the clients will generate a lot of packets and server needs to wait for all the packets or for a a    timeout 
  and send it again. Rather than using this, i have sent it multiple of them at a time.
  
* FLOW OF THE CLIENT CODE
  * Client will initially login into the App
