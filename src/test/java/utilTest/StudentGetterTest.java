package utilTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import constant.FileConstant;
import model.Student;
import util.FileGetter;
import util.StudentsGetter;

class StudentGetterTest {
	File courseDir = new File("Data/TestData");
	File usernamesToIgnoreFile = null;
	List<File> surveyFiles =  new ArrayList<>(1);
	private  Map<Integer, Student> id2Student = new HashMap<>();
	private  Set<Integer> userIdsToIgnore = new HashSet<>();;
	private  Map<String, Student> uid2Student = new HashMap<>();
//	private static File authuserFile = null;
//	private static File roleFile = null;
//	private static File commentRoleFile = null;
//	private static File enrollFile = null;
//	private static File certificateFile = null;
//	private static File authuserprofileFile = null;
//	private static File userIdMapFile = null;


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
//		authuserFile = FileGetter.getFileEndingWith(courseDir, FileConstant.authuserFile);
//		roleFile = FileGetter.getFileEndingWith(courseDir, FileConstant.accessroleFile);
//		commentRoleFile= FileGetter.getFileEndingWith(courseDir, FileConstant.commentRole);
//		enrollFile = FileGetter.getFileEndingWith(courseDir, FileConstant.enrollFile);
//		certificateFile = FileGetter.getFileEndingWith(courseDir, FileConstant.certificateFile);
//		authuserprofileFile = FileGetter.getFileEndingWith(courseDir, FileConstant.authuserProFile);
//		userIdMapFile = FileGetter.getFileEndingWith(courseDir, FileConstant.userIdMapFile);
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
	void testStudentNum() throws IOException {
		StudentsGetter.getStudents(courseDir, usernamesToIgnoreFile, surveyFiles, id2Student, userIdsToIgnore, uid2Student);
		assertEquals(3,id2Student.keySet().size() );
	}
	
	@Test
	void testVerifiedStudentNum() throws IOException {
		StudentsGetter.getStudents(courseDir, usernamesToIgnoreFile, surveyFiles, id2Student, userIdsToIgnore, uid2Student);
	

		List<Student> verifiedStudents = new ArrayList<>();
		id2Student.forEach((id, student)->{
			if(student.isPaying()) {
				 verifiedStudents.add(student);
			}
		});
		
		assertEquals(2, verifiedStudents.size() );
	}
	
	@Test
	void testCertificatedStudentNum() throws IOException {
		StudentsGetter.getStudents(courseDir, usernamesToIgnoreFile, surveyFiles, id2Student, userIdsToIgnore, uid2Student);
	

		List<Student> certificatedStudents = new ArrayList<>();
		id2Student.forEach((id, student)->{
			if(student.isCertificated()) {
				certificatedStudents.add(student);
			}
		});
		assertEquals(1, certificatedStudents.size() );
	}

}
