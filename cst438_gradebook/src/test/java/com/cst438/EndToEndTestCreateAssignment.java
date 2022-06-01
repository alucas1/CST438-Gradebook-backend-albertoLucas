package com.cst438;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

/*
 * Tests the following user story:
 *  as an Instructor I can create a
 *  new assignment for a course that I teach
 */

@SpringBootTest
public class EndToEndTestCreateAssignment {

	public static final String GECKO_DRIVER_FILE_LOCATION = "D:/Downloads/geckodriver.exe";
	
	public static final String URL = "http://localhost:3000";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_ASSIGNMENT_DUE_DATE = "2022-05-06";
	public static final int TEST_COURSE_ID = 99999;
	public static final int SLEEP_DURATION = 1000; // 1 second.

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {

		// Database setup:  
		// create course		
		Course c = new Course();
		c.setCourse_id(TEST_COURSE_ID);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);
		courseRepository.save(c);
		
		// Used to delete test assignment during cleanup
		Assignment a = null;
		
		// initialize the WebDriver
		System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_FILE_LOCATION);
		WebDriver driver = new FirefoxDriver();
		
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {
			// Goes to page URL
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);		
			
			// Click "add assignment" button 
			driver.findElement(By.xpath("//button[@id='AddAssig']")).click();
			Thread.sleep(SLEEP_DURATION);
		    
		    // Clear name field
		    driver.findElement(By.id("name")).clear();
		    
		    // Add Assignment name
		    driver.findElement(By.id("name")).sendKeys(TEST_ASSIGNMENT_NAME);
		    
		    // Add course ID
		    driver.findElement(By.id("course-id")).sendKeys(Integer.toString(TEST_COURSE_ID));
		    
		    // Add due date
		    driver.findElement(By.id("date")).sendKeys(TEST_ASSIGNMENT_DUE_DATE);
		    
		    // Click "submit" button
		    driver.findElement(By.xpath("//button[@id='SubmitAssig']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Click "back" button
		    driver.findElement(By.xpath("//button[@id='Back']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Go through list of assignments and find new assignment
			List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='assignmentName']/div"));
			boolean found = false;
			for (WebElement we : elements) {
				System.out.println(we.getText()); // for debug
				if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
					System.out.println("found on frontend");
					found = true;
					break;
				}
			}
			// Verify that new assignment is in assignment list
			assertTrue(found, "Unable to locate TEST ASSIGNMENT in list of assignments on frontend.");
		    
			// verify that assignment has been added to database
			List<Assignment> assignmentList = assignmentRepository.findNeedGradingByEmail(TEST_INSTRUCTOR_EMAIL);
			found = false;
			for (Assignment assignment : assignmentList) {
				System.out.println(assignment.getName()); // for debug
				if (assignment.getName().equals(TEST_ASSIGNMENT_NAME)) {
					System.out.println("found");
					found = true;
					a = assignment;
					break;
				}
			}
			// Verify that new assignment is in assignment list
			assertTrue(found, "Unable to locate TEST ASSIGNMENT in list of assignments from database.");

		} catch (Exception ex) {
			throw ex;
		} finally {

			// Cleanup database
			if(a != null)
				assignmentRepository.delete(a);
			courseRepository.delete(c);

			driver.quit();
		}

	}
}