package com.example.peerbased;
import java.io.Serializable;

/* This packet is used in the authentication phase of the application */
/* Students will login in to the system by entering the username and password, 
 * The details entered will be sent to the server in the form of this class object, serialized into a byte array.
 * The serialized byte array is stored in the data part of the actual packet, with the auth_packet field set to 'true'
 * Receiver will check the flag and deserialize the data into appropriate class object
 */
public class AuthPacket implements Serializable {
	static final long serialVersionUID = 1234L;
	public boolean changePass; // This flag is set when the 'password change' request is sent by the student
	public boolean grantAccess; // This flag is set by the teacher, when the credentials are correct
	public String userID;	// UserName entered by the student on the tablet
	public String password;	// password entered by the student on the tablet
	public String studentName;  
	public byte errorCode;	// This will be used only when the grant access flag is false
	public AuthPacket()
	{
		userID = "";
		studentName = "";
		password = "";
		changePass = false;
		grantAccess = false;
	}
	public AuthPacket(String uID, String pass, boolean changePass, boolean grantAccess)
	{
		this();
		userID = uID;
		password = pass;
		this.changePass = changePass;
		this.grantAccess = grantAccess;
	}
}