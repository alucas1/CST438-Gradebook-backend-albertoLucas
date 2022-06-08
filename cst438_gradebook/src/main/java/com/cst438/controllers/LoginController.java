package com.cst438.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;


@Controller
public class LoginController {
	/*
	 * used by React Login front end component to test if user is 
	 * logged in.  
	 *   response 401 indicates user is not logged in
	 *   a redirect response take user to assignment front end page.
	 */
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Value("${frontend.post.login.url.instructor}")
	String redirect_url_instructor;
	
	@Value("${frontend.post.login.url.student}")
	String redirect_url_student;
	
	
	@GetMapping("/user")
	public String user (@AuthenticationPrincipal OAuth2User principal){
		
		String email = principal.getAttribute("email");  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findNeedGradingByEmail(email);
		
		if(assignments != null && !assignments.isEmpty()) {
			return "redirect:" + redirect_url_instructor;
		} else {
			return "redirect:" + redirect_url_student;
		}
		
		// used by front end to display user name.
	}
}
