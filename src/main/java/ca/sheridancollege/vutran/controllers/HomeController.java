package ca.sheridancollege.vutran.controllers;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import ca.sheridancollege.vutran.beans.Exercise;
import ca.sheridancollege.vutran.service.AuthenticationService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class HomeController {
	
	final private String REST_URL = "http://localhost:50000/exercise";
	private AuthenticationService authenticationService;
	
	@GetMapping("/") 
    public String index(Model model, RestTemplate restTemplate) {
        ResponseEntity<ArrayList<Exercise>> responseEntity =
                (ResponseEntity<ArrayList<Exercise>>) authenticationService.standardRequest(restTemplate, REST_URL, HttpMethod.GET, new ArrayList<Exercise>().getClass());
        model.addAttribute("exerciseList", responseEntity.getBody());
        return "index";
    }
	
	@GetMapping("/blankForm")
    public String blankForm(Model model) {
        model.addAttribute("exercise", new Exercise());
        return "blankForm";
    }
	
	@PostMapping("/insertEx")
    public String insert(Model model, @ModelAttribute Exercise ex, RestTemplate restTemplate) {
		authenticationService.postRequest(restTemplate, REST_URL, ex, new Exercise().getClass());
        ResponseEntity<ArrayList<Exercise>> responseEntity =
        (ResponseEntity<ArrayList<Exercise>>) authenticationService.standardRequest(restTemplate, REST_URL, HttpMethod.GET, new ArrayList<Exercise>().getClass());
        model.addAttribute("exerciseList", responseEntity.getBody());
        return "index";
    }
	
	@GetMapping("/delete/{id}") 
    public String delete(Model model, @PathVariable("id") int id, RestTemplate restTemplate) {
		authenticationService.standardRequest(restTemplate, REST_URL + "/" + id, HttpMethod.DELETE, new String().getClass());
        ResponseEntity<ArrayList<Exercise>> responseEntity =
        (ResponseEntity<ArrayList<Exercise>>) authenticationService.standardRequest(restTemplate, REST_URL, HttpMethod.GET, new ArrayList<Exercise>().getClass());
        model.addAttribute("exerciseList", responseEntity.getBody());
        return "index";
    }
	
	@GetMapping("/update/{id}")
    public String updateGet(Model model, @PathVariable("id") int id, RestTemplate restTemplate) {
		ResponseEntity<Exercise> responseEntity = 
	            (ResponseEntity<Exercise>) authenticationService.standardRequest(restTemplate, REST_URL +
	            "/" + id, HttpMethod.GET, Exercise.class);
        model.addAttribute("exercise", responseEntity.getBody());
        return "blankForm";
    }
	
	@PostMapping("/updateEx")
    public String updatePost(Model model, @ModelAttribute Exercise e, RestTemplate restTemplate) {
		authenticationService.putRequest(restTemplate, REST_URL + "/" + e.getId(), e);
        ResponseEntity<ArrayList<Exercise>> responseEntity =
                    (ResponseEntity<ArrayList<Exercise>>) authenticationService.standardRequest(restTemplate, REST_URL, HttpMethod.GET, new ArrayList<Exercise>().getClass());
        model.addAttribute("exerciseList", responseEntity.getBody());
        return "index";
    }
	
}
