package com.wipro.medicalbillingsystem.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wipro.medicalbillingsystem.dto.AuthRequest;
import com.wipro.medicalbillingsystem.dto.PatientsDTO;
import com.wipro.medicalbillingsystem.entities.Patients;
import com.wipro.medicalbillingsystem.exceptions.PatientIllegalArgumentsException;
import com.wipro.medicalbillingsystem.exceptions.PatientNotFoundException;
import com.wipro.medicalbillingsystem.service.AuthJWTService;
import com.wipro.medicalbillingsystem.service.IInsurancePlansService;
import com.wipro.medicalbillingsystem.service.IPatientsService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/patients")
public class PatientsRestController {

	Logger logger = LoggerFactory.getLogger(PatientsRestController.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private AuthJWTService jwtService;

	@Autowired
	private IInsurancePlansService planService;

	@Autowired
	private IPatientsService service;

	@PostMapping("/add/new")
	public Patients insertPatients(@RequestBody PatientsDTO patientDTO) {

		Patients patient = service.addPatients(patientDTO);

		if (patient.getPatientName() == null || patient.getPatientGender() == null) {
			logger.error("Patient has entered wrong data!!!");
			throw new PatientIllegalArgumentsException(HttpStatus.BAD_REQUEST, "You have entered Invalid values.");
		}
		return patient;
	}

	@PutMapping("/update/patient/{patientId}")
	@PreAuthorize("hasAuthority('PATIENTS')")
	public Patients updatePatient(@RequestBody PatientsDTO patientDTO,@PathVariable long patientId) {
		return service.updatepatients(patientDTO);

	}
	
	

	@DeleteMapping("/delete/patient/{patientId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String deletePatients(@PathVariable int patientId) {

		System.out.println(patientId);
		service.deletePatients(patientId);
		return "Successfully Deleted patient with id: " + patientId;
	}

	@GetMapping("/getbyname/{patientName}")
	@PreAuthorize("hasAuthority('PROVIDER')")
	public PatientsDTO getByPatientName(@PathVariable String patientName) {
		PatientsDTO patientdto = service.getPatientByName(patientName);
		if (patientdto.getPatientName() == null) {
			logger.error("Patient with name " + patientName + " is not registered with us!!!!");
			throw new PatientNotFoundException(HttpStatus.NOT_FOUND,
					"Patient with name " + patientName + " does not exist");
		}
		return patientdto;
	}
	
	@GetMapping("/getbypatientname/{patientName}")
	@PreAuthorize("hasAuthority('COMPANY')")
	public PatientsDTO getallByPatientName(@PathVariable String patientName) {
		PatientsDTO patientdto = service.getPatientByName(patientName);
		if (patientdto.getPatientName() == null) {
			logger.error("Patient with name " + patientName + " is not registered with us!!!!");
			throw new PatientNotFoundException(HttpStatus.NOT_FOUND,
					"Patient with name " + patientName + " does not exist");
		}
		return patientdto;
	}

	@GetMapping("/get/allpatients")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<Patients> getAllPatients() {
		return service.getAllPatients();
	}
	
	@GetMapping("/getpatients")
	@PreAuthorize("hasAuthority('PROVIDER')")
	public List<Patients> getPatientForInvoice() {
		return service.getAllPatients();
	}

	

	@PostMapping("/authenticate")
	public String authenticateAndGenerateToken(@RequestBody AuthRequest authReq) {

		Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword()));

		String Token = null;
		if (authenticate.isAuthenticated()) {
			Token = jwtService.generateToken(authReq.getUsername());
			logger.info("JWT Token successfully generated!!!");
		}

		else {
			logger.info("Not Found USERNAME!!!!");
			throw new UsernameNotFoundException("UserName Not Found!!!! ");
		}
		return Token;

	}
}
