package utilTest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;

import model.Step;
import util.CourseParser;

public class CourseParserTestWithTestDatafile {

	static JSONObject structFileRootObj = null;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		File file = new File("C:/DharaniCh/SWEN589_Project/datastore/RJ101xNew/VictoriaX-RJ101x-2T2018-course_structure-prod-analytics.json");
		JSONParser jsonParser=new JSONParser();
		structFileRootObj = (JSONObject) jsonParser.parse(new BufferedReader(new FileReader(file)));
	}

	@Test
	public void getCourseNameTest() {
		List<String> assertValue = getAssertValue("getCourseNameTest"); // get assertvalue list related to testcasename
		String courseName = CourseParser.getCourseName(structFileRootObj);
		assertEquals(assertValue.get(0),courseName);  
	}
	
	@Test
	public void getCourseIdTest() {
		List<String> assertValue = getAssertValue("getCourseIdTest");
		String courseId = CourseParser.getCourseId(structFileRootObj);
		assertEquals(assertValue.get(0),courseId);
	}
	
	@Test
	public void mapModuleId2ChapterTest() {
		List<String> assertValue = getAssertValue("mapModuleId2ChapterTest");
		Map<String,Step> moduleId2Chapter = CourseParser.mapModuleId2Chapter(structFileRootObj);
		int number = moduleId2Chapter.keySet().size();
		assertEquals(Integer.parseInt(assertValue.get(0)), number);
	}
	
	@Test
	public void courseDataTest() {
		List<OffsetDateTime>  courseDates = CourseParser.courseDates(structFileRootObj);
		OffsetDateTime start = courseDates.get(0);
		OffsetDateTime end = courseDates.get(1);
		assertEquals(start, OffsetDateTime.parse("2018-05-01T00:00Z"));
		assertEquals(end, OffsetDateTime.parse("2018-06-16T00:00Z"));
	}
	
	private List<String> getAssertValue(String methodName){
		List<String> assertValue = new ArrayList<>();//
		if(!ReadTestDataUtil.testDataMap.isEmpty()) { 
			assertValue = ReadTestDataUtil.testDataMap.get(methodName);
		}
		return assertValue;
	}
	
	

}
