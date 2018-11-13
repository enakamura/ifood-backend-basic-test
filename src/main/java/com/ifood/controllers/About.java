package com.ifood.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ifood.model.weatherService.Status;

@Controller
public class About {

	@GetMapping("/about")
	public ResponseEntity<Object> aboutService() {
		return new ResponseEntity<Object>(new Status("UP"), HttpStatus.OK);
	}

}
