package edu.miage.springboot.hello;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = { "${app.dev.frontend.local}" })
public class HelloController {

	@RequestMapping(value = "/message", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> index() {
		return Collections.singletonMap("message", " from Backend");
	}

}
