package com.cst438.controllers;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO, @AuthenticationPrincipal OAuth2User principal) {
		// check that this request is from the course instructor and for a valid course
		String email = principal.getAttribute("email");   // user name (should be instructor's email) 

		//Grab variables from enrollmentDTO
		int id = enrollmentDTO.id;
		String studentEmail = enrollmentDTO.studentEmail;
		String studentName = enrollmentDTO.studentEmail;
		int course_id = enrollmentDTO.course_id;	
		
		//verify that course id and professor are valid
		Course c = courseRepository.findById(course_id).orElse(null);
		if (c == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Course does not exist. " );
		}
		if (!c.getInstructor().equals(email)) {
			throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
		}
		
		//Create new enrollment based on enrollmentDTO
		Enrollment newEnrollment = new Enrollment();
		newEnrollment.setCourse(c);
		newEnrollment.setId(id);
		newEnrollment.setStudentEmail(studentEmail);
		newEnrollment.setStudentName(studentName);
		
		//Save enrollment to database
		enrollmentRepository.save(newEnrollment);
		return null;
	}

}
