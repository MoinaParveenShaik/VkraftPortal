package com.vkraftportal.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vkraftportal.model.AppliedCandidateInformation;
import com.vkraftportal.model.CreateJob;
import com.vkraftportal.model.HumanResource;
import com.vkraftportal.model.RegisterCandidate;
import com.vkraftportal.model.RegisterEmployee;
import com.vkraftportal.model.Timesheet;
import com.vkraftportal.repositories.AppliedCandidateInformationRepo;
import com.vkraftportal.repositories.CreateJobRepo;
import com.vkraftportal.repositories.HumanResourceRepo;
import com.vkraftportal.repositories.RegisterCandidateRepo;
import com.vkraftportal.repositories.RegisterEmployeeRepo;
import com.vkraftportal.repositories.TimesheetRepo;

@Service
public class Services {

	@Autowired
	RegisterEmployeeRepo employeeRepo;
	@Autowired
	RegisterCandidateRepo candidateRepo;
	@Autowired
	HumanResourceRepo humanResourceRepo;
	@Autowired
	TimesheetRepo timesheetRepo;
	@Autowired
	AppliedCandidateInformationRepo appliedCandidaterepo;
	@Autowired
	CreateJobRepo jobRepo;

	//	---------------------------------------HumanResource Services---------------------------------

	public HumanResource saveHRCredentials(HumanResource humanResource) {

		return humanResourceRepo.save(humanResource);
	}

	public boolean humanResourceExists(HumanResource humanResource) {

		if (humanResourceRepo.findByEmployeeNameAndEmployeeNumber(humanResource.getEmployeeName(),
				humanResource.getEmployeeNumber()) != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateHR(String username, String password) {
		HumanResource admin = humanResourceRepo.findByEmail(username);
		return admin != null && admin.getPassword().equals(password);
	}

	public HumanResource getHRByUsernameAndPassword(String email, String password) {
		return humanResourceRepo.findByEmailAndPassword(email, password);
	}

	//	------------------------------------------------------RegisterCandidate Services---------------------------------

	public boolean candidateExists(RegisterCandidate registerCandidate) {
		if (registerCandidate != null && registerCandidate.getEmail() != null) {
			return candidateRepo.findByEmail(registerCandidate.getEmail()) != null;
		} else {
			return false;
		}
	}

	public String generateAndSetRandomPassword() {
		SecureRandom secureRandom = new SecureRandom();
		int size = 10;
		byte[] randomBytes = new byte[size];
		secureRandom.nextBytes(randomBytes);

		String generatedPassword = Base64.getEncoder().encodeToString(randomBytes).substring(0, size);

		System.out.println(generatedPassword);
		return generatedPassword;
	}

	public RegisterCandidate saveCandidate(RegisterCandidate candidate) {
		return candidateRepo.save(candidate);
	}

	public String getEmailBody(String recipientEmail, String generatedPassword) {
		return "Hi, Greetings for The Day..! " + ",\n\n"
				+ "Welcome to our On-Boarding portal..! Your Login details are as follows:\n\n" + "UserName: "
				+ recipientEmail + "\n" + "Password: " + generatedPassword + "\n\n"
				+ "Please Login with the above credentials and complete the further process. If you have any queries or need assistance, feel free to contact us.\n\n"
				+ "Thank you!\n\n" + "Best regards,\n"
				+ "HR Team, \nVkraft Software Services Pvt Ltd\nwww.vkraftsoftware.com\nwww.kraftsoftwaresolution.com";
	}

	public RegisterCandidate getCandidateByEmail(String email,String password) {

		return candidateRepo.findByEmailAndPassword(email, password);
	}

	public boolean validateCandidate(String email, String password) {
		RegisterCandidate Candidate = candidateRepo.findByEmail(email);
		return Candidate != null && Candidate.getPassword().equals(password);
	}

	public boolean verifyCandidate(RegisterCandidate candidate) {

		return validateCandidate(candidate.getEmail(), candidate.getPassword());
	}

	public RegisterCandidate findCandidateByEmail(String email) {
		return candidateRepo.findByEmail(email);
	}

	public RegisterCandidate getCandidateByEmailAndPassword(String email, String password) {
		return candidateRepo.findByEmailAndPassword(email, password);
	}

	public boolean isValidPassword(String password) {

		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={};':\"\\|,.<>?/]).{8,}$";
		return password.matches(regex);
	}

	public boolean isValidMobileNumber(String mobileNumber) {

		return mobileNumber.matches("\\d{10}");
	}

	public Iterable<AppliedCandidateInformation> getAllAppliedCandidates() {
		Iterable<AppliedCandidateInformation> findAll = appliedCandidaterepo.findAll();
		return findAll;
	}

	public String getEmailBodyForTechOne(String recipientEmail, String fullname, String role) {
		return "Dear Candidate " + fullname + ",\n\n" + "We're eager to move forward with your application for the "
				+ role + "position at Vkraft Software Services."
				+ " Kindly acknowledge this email to confirm your attendance. \n\n"
				+ "Looking forward to the interview!. If you have any questions or need assistance, feel free to contact us.\n\n"
				+ "Thank you!\n\n" + "Best regards,\n"
				+ "HR Team, \nVkraft Software Services Pvt Ltd\nwww.vkraftsoftware.com\nwww.kraftsoftwaresolution.com";
	}

	//	--------------------------------------RegisterEmployee Services-----------------------------------------

	public RegisterEmployee saveEmployee(RegisterEmployee employee) {
		return employeeRepo.save(employee);
	}

	public boolean employeeExists(RegisterEmployee employee) {

		if (employeeRepo.findByEmployeeNumber(employee.getEmployeeNumber()) != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateEmployee(String username, String password) {
		RegisterEmployee employee = employeeRepo.findByEmail(username);
		return employee != null && employee.getPassword().equals(password);
	}

	public RegisterEmployee getEmployeeByUsernameAndPassword(String email, String password) {
		return employeeRepo.findByEmailAndPassword(email, password);
	}

	public String getEmailBody(String recipientEmail, String generatedPassword, String employeeName) {
		return "Hi " + employeeName + ",\n\n"
				+ "Welcome to our Timesheet portal..! Your account details are as follows:\n\n" + "Email: "
				+ recipientEmail + "\n" + "Password: " + generatedPassword + "\n\n"
				+ "Please keep this information secure. If you have any questions or need assistance, feel free to contact us.\n\n"
				+ "Thank you!\n\n" + "Best regards,\n"
				+ "HR Team, \nVkraft Software Services Pvt Ltd\nwww.vkraftsoftware.com\nwww.kraftsoftwaresolution.com";
	}

	public Long getCountOfEmployee() {
		return employeeRepo.count();
	}

	public Iterable<RegisterEmployee> getAllEmployees() {
		Iterable<RegisterEmployee> findAll = employeeRepo.findAll();

		return findAll;
	}

	//	------------------------------------------Timesheet Services-----------------------------------

	public void saveTimesheet(Timesheet timesheet) {
		timesheetRepo.save(timesheet);
	}

	public Timesheet findByEmpName(String name) {
		Timesheet findByEmployeeName = timesheetRepo.findByEmployeeName(name);
		return findByEmployeeName;
	}

	public Timesheet findByEmpNumber(String empNumber) {
		Timesheet entity = timesheetRepo.findByEmployeeNumber(empNumber);
		return entity;
	}

	public boolean deleteByEmployeeNumberAndDate(String empNumber, String month, String year) {
		Timesheet findByEmployeeNumber = timesheetRepo.findByEmployeeNumberAndMonthAndYear(empNumber, month, year);
		if (findByEmployeeNumber != null) {
			timesheetRepo.deleteByEmployeeNumberAndMonthAndYear(empNumber, month, year);
			return true;
		} else {
			return false;
		}
	}

	public Timesheet findByEmployeeNumberAndMonthAndYear(String employeeNumber, String month, String year) {
		return timesheetRepo.findByEmployeeNumberAndMonthAndYear(employeeNumber, month, year);
	}

	public boolean timesheetExists(Timesheet timesheetDataToSave) {

		if (timesheetRepo.findByEmployeeNumberAndMonthAndYear(timesheetDataToSave.getEmployeeNumber(),
				timesheetDataToSave.getMonth(), timesheetDataToSave.getYear()) != null) {
			return true;
		} else {
			return false;
		}
	}

	public List<Timesheet> getApprovedTimesheet(String status, String month, String year) {
		return timesheetRepo.findByStatusAndMonthAndYear(status, month, year);
	}

	public List<Timesheet> getEmployeesByStatus(String string) {
		if (string.equals("pending")) {
			System.out.println("in pending ");
			List<Timesheet> findByStatus = timesheetRepo.findByStatus(string);
			return findByStatus;
		} else if (string.equals("approved")) {
			System.out.println("in approved ");
			List<Timesheet> findByStatus = timesheetRepo.findByStatus(string);
			return findByStatus;
		} else
			return null;
	}

	public Iterable<Timesheet> getTotalEmployees() {
		Iterable<Timesheet> findAll = timesheetRepo.findAll();
		System.out.println(findAll + "Sss");
		return findAll;
	}

	//	----------------------------------AppliedCandidateInformationServices----------------------------------

	public AppliedCandidateInformation saveAppliedCandidateInfo(AppliedCandidateInformation appliedCandidateInfo) {
		return appliedCandidaterepo.save(appliedCandidateInfo);
	}

	//	public boolean appliedCandidateInfoExists(AppliedCandidateInformation appliedCandidateInfo) {
	//
	//		if (appliedCandidaterepo.findByJobIdAndFullName(appliedCandidateInfo.getJobId(),
	//				appliedCandidateInfo.getFullName()) != null) {
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}

	public boolean appliedCandidateInfoExists(AppliedCandidateInformation appliedCandidateInfo) {

		if (appliedCandidaterepo.findByJobIdAndEmail(appliedCandidateInfo.getJobId(),
				appliedCandidateInfo.getEmail()) != null) {
			return true;
		} else {
			return false;
		}
	}

	public String convertToBase64(String filePath) throws IOException {
		byte[] fileContent = Files.readAllBytes(new File(filePath).toPath());
		return Base64.getEncoder().encodeToString(fileContent);
	}

	public AppliedCandidateInformation updateAppliedCandidateInfo(AppliedCandidateInformation body) {

		AppliedCandidateInformation updatedCandidate = body;
		appliedCandidaterepo.delete(body);
		AppliedCandidateInformation saved = appliedCandidaterepo.save(updatedCandidate);
		return saved;
	}

	public String getScreeningEmailBody(AppliedCandidateInformation body) {
		return "Hi "+ body.getFullName()  + ",\n\n"
				+ "Congratulations! You have been selected for the Screening round.\n\n"
				+ "Please be prepared for the upcoming screening process. If you have any questions or need further information, feel free to contact us.\n\n"
				+ "Thank you for your interest and best of luck!\n\n" + "Best regards,\n" + "HR Team,\n"
				+ "Vkraft Software Services Pvt Ltd\n" + "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
	}

	public String getTechnicalRoundEmailBody(AppliedCandidateInformation body) {
		return "Hi " + body.getFullName() + ",\n\n"
				+ "Congratulations! You have successfully cleared the Technical round.\n\n"
				+ "Please be prepared for the upcoming Technical interview. If you have any questions or need further information, feel free to contact us.\n\n"
				+ "Thank you for your continued interest and best of luck!\n\n" + "Best regards,\n" + "HR Team,\n"
				+ "Vkraft Software Services Pvt Ltd\n" + "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
	}

	public String getRegretScreeningEmailBody(AppliedCandidateInformation body) {
		return "Hi " + body.getFullName() + ",\n\n"
				+"Thank you for your interest. We regret to inform you that at this time, your application will not be further considered for this role." 
				+"We sincerely appreciate your interest in Vkraft, and we encourage you to keep an eye on our career opportunities. Your skills and experience may be an excellent fit for future openings, and we would welcome the opportunity to consider your application again."
				+ ".\n\n"
				+ "Thank you for considering us as your potential employer, and we wish you success in your job search. If you have any questions or would like feedback on your application, please do not hesitate to reach out.\n\n"+
				"We value your understanding and hope you find the perfect match for your career goals in the near future."
				+ "Best regards,\n" + "HR Team,\n"
				+ "Vkraft Software Services Pvt Ltd\n" + "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
	}

	public AppliedCandidateInformation findByEmail(String email) {
		AppliedCandidateInformation candidateEmail = appliedCandidaterepo.findByEmail(email);
		System.out.println(candidateEmail + "found");
		return candidateEmail;
	}




	public void deleteCandidateFromAppliedCandidateInformation(String email) {

		appliedCandidaterepo.deleteByEmail(email);
	}

	public Iterable<AppliedCandidateInformation> getAllScreeningCandidates() {
		String str = "screening";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	//	---------------------------------------CreateJob------------------------------------------

	public CreateJob getJobDetails(String jobId) {
		CreateJob findByJobId = jobRepo.findByJobId(jobId);
		return findByJobId;
	}

	public void saveJob(CreateJob job) {
		jobRepo.save(job);
	}

	public boolean updateAppliedCandidateStatusByEmail(String email) {
		AppliedCandidateInformation candidate=appliedCandidaterepo.findByEmail(email);
		if(candidate!=null) {
			candidate.setStatus("screening");
			return true;
		}
		else{
			return false;
		}
	}

	public Iterable<CreateJob> getAllJobs() {
		Iterable<CreateJob> findAll = jobRepo.findAll();
		System.out.println(findAll);
		return findAll;
	}



}
