package utilTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Step;
import util.CourseParser;

class CourseParserTest {

	static JSONObject structFileRootObj = null;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		File file = new File("Data/TestData/VictoriaX-NZ102x-2T2018-course_structure-prod-analytics.json");
		JSONParser jsonParser=new JSONParser();
		structFileRootObj = (JSONObject) jsonParser.parse(new BufferedReader(new FileReader(file )));
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void getCourseNameTest() {
		String courseName = CourseParser.getCourseName(structFileRootObj);
		assertEquals("New Zealand Landscape as Culture: Maunga (Mountains)",courseName);
	}
	@Test
	void getCourseIdTest() {
		String courseId = CourseParser.getCourseId(structFileRootObj);
		assertEquals("VictoriaX+NZ102x+2T2018",courseId);
	}
	@Test
	void mapModuleId2ChapterTest() {
		Map<String,Step> moduleId2Chapter = CourseParser.mapModuleId2Chapter(structFileRootObj);
		
		int number = moduleId2Chapter.keySet().size();
		assertEquals(6, number);
	}
	
	@Test
	void courseDataTest() {
		List<OffsetDateTime>  courseDates = CourseParser.courseDates(structFileRootObj);
		OffsetDateTime start = courseDates.get(0);
		OffsetDateTime end = courseDates.get(1);
		assertEquals(start, OffsetDateTime.parse("2018-08-15T00:00:00Z"));
		assertEquals(end, OffsetDateTime.parse("2018-09-12T00:00:00Z"));
	}

}
