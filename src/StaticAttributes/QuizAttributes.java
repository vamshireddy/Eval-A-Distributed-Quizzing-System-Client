package StaticAttributes;

import java.util.ArrayList;

import com.example.peerbased.Leader;

public class QuizAttributes {
	public static byte noOfOnlineStudents;
	public static byte noOfLeaders;
	public static byte sizeOfGroup;
	public static byte noOfRounds;
	public static String subject;
	public static String studentID;
	public static String studentName;
	public static ArrayList<Leader> selectedLeaders;
	static
	{
		noOfOnlineStudents = -1;
		noOfLeaders  = -1;
		sizeOfGroup = -1;
		noOfRounds = -1;
		subject = "";
		studentID = "";
		selectedLeaders = null;
	}
}
