package utilTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Student;
import storage.DataManager;
import util.CourseParser;
import util.DiscussionParser;
import util.LogReader;
import util.StudentsGetter;

class LogReaderTest {
	private static File courseDir = new File("Data/TestData");
	private static File usernamesToIgnoreFile = null;
	private static List<File> surveyFiles =  new ArrayList<>(1);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {		
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		DataManager.deleteDataInstance();
		JSONObject structFileRootObj = null;
		File file = new File("Data/TestData/VictoriaX-NZ102x-2T2018-course_structure-prod-analytics.json");
		JSONParser jsonParser=new JSONParser();
		structFileRootObj = (JSONObject) jsonParser.parse(new BufferedReader(new FileReader(file )));
		DataManager.getDataInstance().setCourseName(CourseParser.getCourseName(structFileRootObj));
		DataManager.getDataInstance().setCourseID(CourseParser.getCourseId(structFileRootObj)); 
		List<OffsetDateTime> courseDates = CourseParser.courseDates(structFileRootObj);
		DataManager.getDataInstance().setCourseStart(courseDates.get(0));
		DataManager.getDataInstance().setCourseEnd(courseDates.get(1));
		
		DataManager.getDataInstance().setHashcode2Chapter(CourseParser.mapHashcode2Chapter(structFileRootObj));
		DataManager.getDataInstance().setHashcode2Sequential(CourseParser.mapHashcode2Sequential(structFileRootObj));
		DataManager.getDataInstance().setHashcode2Vertical(CourseParser.mapHashcode2Vertical(structFileRootObj));
		DataManager.getDataInstance().setId2Discussion(DiscussionParser.mapId2Discussion(structFileRootObj));
	

		DataManager.getDataInstance().setAllChapters(new ArrayList<>(DataManager.getDataInstance().getHashcode2Chapter().values()));
		DataManager.getDataInstance().getAllChapters().sort(null);
		
		DataManager.getDataInstance().setAllSequentials(new ArrayList<>(DataManager.getDataInstance().getHashcode2Sequential().values()));
		DataManager.getDataInstance().getAllSequentials().sort(null);
		
		DataManager.getDataInstance().setAllVerticals(new ArrayList<>(DataManager.getDataInstance().getHashcode2Vertical().values()));
		DataManager.getDataInstance().getAllVerticals().sort(null);
		
		DataManager.getDataInstance().setAllDiscussions(new ArrayList<>(DataManager.getDataInstance().getId2Discussion().values()));
		DataManager.getDataInstance().getAllDiscussions().sort(null);

	
		DataManager.getDataInstance().setVerticalsAndDiscussions(new ArrayList<>(DataManager.getDataInstance().getAllVerticals()));
		DataManager.getDataInstance().getVerticalsAndDiscussions().addAll(DataManager.getDataInstance().getAllDiscussion());
		DataManager.getDataInstance().getVerticalsAndDiscussions().sort(null);
		
		DataManager.getDataInstance().setSequentialToVerticalMap(CourseParser.mapSequentialToVertical(structFileRootObj)); // helper structure to analysis the vertical index

		StudentsGetter.getStudents(courseDir, usernamesToIgnoreFile, surveyFiles, DataManager.getDataInstance().getId2Student(),DataManager.getDataInstance().getUserIdsToIgnore(), DataManager.getDataInstance().getUid2Student());
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testStudentVerticalRecords() throws FileNotFoundException {
		String courseId = "VictoriaX+NZ102x+2T2018";
		File logFile = new File("Data/TestData/Logs");
		LogReader.getLogFiles(logFile, courseId);
		int verticalRecordsSize = DataManager.getDataInstance().getId2Student().get(8894502).getVerticalRecords().size();
		System.out.println(verticalRecordsSize);
		assertEquals(4,verticalRecordsSize);
		
	}
	
	@Test
	void testStudentDiscussionRecords() throws FileNotFoundException {
		String courseId = "VictoriaX+NZ102x+2T2018";
		File logFile = new File("Data/TestData/Logs");
		LogReader.getLogFiles(logFile, courseId);
		int discussionlRecordsSize = DataManager.getDataInstance().getId2Student().get(8894502).getDiscussionRecords().size();
		assertEquals(3,discussionlRecordsSize);
	}
	
	@Test
	void testStudentViewedRecords() throws FileNotFoundException {
		String courseId = "VictoriaX+NZ102x+2T2018";
		File logFile = new File("Data/TestData/Logs");
		LogReader.getLogFiles(logFile, courseId);
		int viewedRecordsSize = DataManager.getDataInstance().getId2Student().get(8894502).getVerticalAndDiscussionRecords().size();
		assertEquals(4,viewedRecordsSize);

	}

}
