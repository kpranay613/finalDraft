package com.wipro.medicalbillingsystem.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.wipro.medicalbillingsystem.entities.AdminMedical;
import com.wipro.medicalbillingsystem.entities.HealthcareProvider;
import com.wipro.medicalbillingsystem.entities.InsuranceCompany;
import com.wipro.medicalbillingsystem.entities.Patients;
import com.wipro.medicalbillingsystem.repository.AdminMedicalRepository;
import com.wipro.medicalbillingsystem.repository.HealthcareProviderRepository;
import com.wipro.medicalbillingsystem.repository.InsuranceCompanyRepository;
import com.wipro.medicalbillingsystem.repository.PatientRepository;

@Component

public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private HealthcareProviderRepository providerRepository;
	
	@Autowired
	private InsuranceCompanyRepository companyRepository;
	
	@Autowired
	private AdminMedicalRepository adminRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Patients> patient=patientRepository.findByPatientName(username);
		Optional<HealthcareProvider> provider=providerRepository.findByProviderName(username);
		Optional<InsuranceCompany> company=companyRepository.findByCompanyName(username);
		Optional<AdminMedical> adminInfo=adminRepository.findByAdminName(username);
		
		if(patient.isPresent())
		{
			
	        return patient.map(PatientInfoPatientDetails::new)
	                .orElseThrow(() -> new UsernameNotFoundException("Patient not found " + username));

		}
		if(provider.isPresent())
		{
	        return provider.map(ProviderInfoProviderDetails::new)
	                .orElseThrow(() -> new UsernameNotFoundException("Provider not found " + username));
			
		}
		if(company.isPresent())
		{

	        return company.map(CompanyInfoCompanyDetails::new)
	                .orElseThrow(() -> new UsernameNotFoundException("Company not found " + username));
		}
		if(adminInfo.isPresent())
		{

	        return adminInfo.map(AdminInfoAdminDetails::new)
	                .orElseThrow(() -> new UsernameNotFoundException("Admin not found " + username));
		}
		throw new UsernameNotFoundException("UserName Not Found :"+username);
	}

}
