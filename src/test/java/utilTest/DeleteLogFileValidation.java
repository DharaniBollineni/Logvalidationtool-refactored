package utilTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

public class DeleteLogFileValidation {
	
	
	@Test
	public void checkLogFileValid() throws FileNotFoundException {
//		File logsDir = new File("C:\\DharaniCh\\SWEN589_Project\\datastore\\RJ101xNew\\logs\\testdir.log");
		File logsDir = new File("C:\\DharaniCh\\SWEN589_Project\\datastore\\RJ101xNew\\logs");
		File[] logFiles = logsDir.listFiles(file -> file.getName().endsWith(".log")); 											// load .log files
		if(logFiles.length == 0 ) {																								// case1:log folder is empty
			System.out.println("Log folder is empty");
			System.exit(0);
		}
		Arrays.stream(logFiles) // iterate collections
		.parallel()  //multithreading feature and This is way more faster that foreach() and stream.forEach(). 
		.forEach(logFile -> {//each file
			if(logFile.getName().endsWith(".log")) {																			//check ends with .log
				if(logFile.isFile()) { 																							// case2: isfile
					try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {// read logfile data
						final JSONParser parser=new JSONParser();
						reader.lines().forEach(line -> { //check each log file
							try {
								JSONObject rootObj = (JSONObject) parser.parse(line);//text line to json object
								String errorMsg = checkError(rootObj);//check validate object
								if(!errorMsg.equals("")) { // not empty
									System.out.println(logFile.getName() + " "+ "Failed1" + errorMsg);
								}
							} catch (org.json.simple.parser.ParseException e) {// test data: catches invalied json data and 	//empty .log file
								System.err.println("here iam "+e.getMessage()); 								
								System.out.println(logFile.getName() +" "+ "Failed2" + e.getMessage());
							}
						});
					}
					catch (IOException x) {
						System.err.format("IOException: %s%n", x);
					}

				}else if(logFile.isDirectory()) {																				// case3 isDirectory **pending

				}
			}	
		});
		
	}

	private String checkError(JSONObject rootObj) {
		JSONObject childObj = (JSONObject) rootObj.get("context");
		String errorMsg = "";//check root with host
		if(!rootObj.get("host").equals("courses.edx.org") && !rootObj.get("host").equals("preview.edx.org") && !rootObj.get("host").equals("mitxpro.mit.edu") && !rootObj.get("host").equals("studio.edx.org")) {
			errorMsg = "Failure" +"Host is wrong";
		}
		if(!childObj.equals(null)) { // check child key user_id, org_id, course_id
//			if(childObj.get("user_id").equals("") || childObj.get("user_id") == null) {
//				errorMsg =errorMsg + "Failure" + "User Id not available";
//			}
//			if(!childObj.get("org_id").equals("VictoriaX")) {
//				errorMsg =errorMsg + "Failure" + "Organization Id is wrong";
//			}
			if(!childObj.get("course_id").equals("course-v1:VictoriaX+RJ101x+2T2018")){// **update with course id
				System.out.println(childObj.get("course_id"));
				errorMsg = errorMsg +"Failure" + "Course Id is not availabe";
			}
		}else {// child is empty
			errorMsg = "Failure" + "User Id not available"+",Organization Id is wrong"+",Course Id is not availabe";
		}
		return errorMsg;
	}

}
