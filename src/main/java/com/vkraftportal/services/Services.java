package com.vkraftportal.services;

import java.security.SecureRandom;
import java.time.Year;
import java.util.Base64;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vkraftportal.model.AppliedCandidateInformation;
import com.vkraftportal.model.CreateJob;
import com.vkraftportal.model.EmployeeTimesheet;
import com.vkraftportal.model.HumanResource;
import com.vkraftportal.model.ReferredCandidateInformation;
import com.vkraftportal.model.RegisterCandidate;
import com.vkraftportal.model.RegisterEmployee;
import com.vkraftportal.model.Timesheet;
import com.vkraftportal.repositories.AppliedCandidateInformationRepo;
import com.vkraftportal.repositories.CreateJobRepo;
import com.vkraftportal.repositories.EmployeeTimesheetRepo;
import com.vkraftportal.repositories.HumanResourceRepo;
import com.vkraftportal.repositories.ReferredCandidateRepo;
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
	@Autowired
	EmployeeTimesheetRepo employeeTimesheetRepo;
	@Autowired
	ReferredCandidateRepo referredCandidaterepo;
//	---------------------------------------HumanResource Services---------------------------------

	public HumanResource getHRByUsernameAndPassword(String email, String password) {

		if ("hr123@gmail.com".equals(email) && "hr321@Vkraft".equals(password)) {

			return humanResourceRepo.findByEmailAndPassword(email, password);

		} else {
			return null;
		}
	}

	public HumanResource getTLByUsernameAndPassword(String email, String password) {

		if ("tl123@gmail.com".equals(email) && "tl321@Vkraft".equals(password)) {

			return humanResourceRepo.findByEmailAndPassword(email, password);

		} else {
			return null;
		}
	}

	public boolean validateHR(String username, String password) {
		HumanResource admin = humanResourceRepo.findByEmail(username);
		return admin != null && admin.getPassword().equals(password);
	}

	public HumanResource saveHRCredentials(HumanResource humanResource) {

		return humanResourceRepo.save(humanResource);
	}

	public boolean humanResourceExists(HumanResource humanResource) {

		if (humanResourceRepo.findByEmailAndPassword(humanResource.getEmail(), humanResource.getPassword()) != null) {
			return true;
		} else {
			return false;
		}
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

	public RegisterCandidate getCandidateByEmail(String email, String password) {
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

//	--------------------------------------RegisterEmployee Services-----------------------------------------

	public RegisterEmployee saveEmployee(RegisterEmployee employee) {
		return employeeRepo.save(employee);
	}

	public boolean employeeExists(RegisterEmployee employee) {
		RegisterEmployee existingEmployee = employeeRepo.findByEmployeeNumberOrEmail(employee.getEmployeeNumber(),
				employee.getEmail());
		return existingEmployee != null;
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

	public String subjectForEmployeeRegistration(String email) {
		employeeRepo.findByEmail(email);
		String subject;
		return subject = "Your Account information For Timesheet portal";
	}

	public boolean deleteByEmployeeNumber(String empNumber) {
		RegisterEmployee findByEmployeeNumber = employeeRepo.findByEmployeeNumber(empNumber);
		if (findByEmployeeNumber != null) {
			employeeRepo.deleteByEmployeeNumber(empNumber);
			return true;
		} else {
			return false;
		}
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
			List<Timesheet> findByStatus = timesheetRepo.findByStatus(string);
			return findByStatus;
		} else if (string.equals("approved")) {
			List<Timesheet> findByStatus = timesheetRepo.findByStatus(string);
			return findByStatus;
		} else
			return null;
	}

	public Iterable<Timesheet> getTotalEmployees() {
		Iterable<Timesheet> findAll = timesheetRepo.findAll();
		return findAll;
	}

	public List<Timesheet> findByProjectname(String projectName) {
		List<Timesheet> employeeProject = null;
		if ("SOA".equals(projectName) || "Airflow".equals(projectName) || "Communication API".equals(projectName)
				|| "Azure Redis Cache UI".equals(projectName) || "MQ".equals(projectName)
				|| "Horizon".equals(projectName) || "IE Pod1".equals(projectName) || "IE Pod3".equals(projectName)) {
			employeeProject = timesheetRepo.findByProjectName(projectName);
		}
		return employeeProject;
	}

	public Long getCountOfAmway() {
		String clientName = "Amway";
		Iterable<Timesheet> timesheet = timesheetRepo.findByClientName(clientName);
		long count = StreamSupport.stream(timesheet.spliterator(), false).count();
		return count;
	}

	public Long getCountOfSoa() {
		String projectName = "SOA";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfAirflow() {
		String projectName = "Airflow";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfAia() {
		String clientName = "AIA";
		Iterable<Timesheet> timesheet = timesheetRepo.findByClientName(clientName);
		long count = StreamSupport.stream(timesheet.spliterator(), false).count();
		return count;
	}

	public Long getCountOfCommunication() {
		String projectName = "Communication API";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfAzure() {
		String projectName = "Azure Redis Cache UI";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfMq() {
		String projectName = "MQ";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfHorizon() {
		String projectName = "Horizon";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfIePod1() {
		String projectName = "IE Pod1";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public Long getCountOfIePod3() {
		String projectName = "IE Pod3";
		Iterable<Timesheet> timesheets = timesheetRepo.findByProjectName(projectName);
		long count = StreamSupport.stream(timesheets.spliterator(), false).count();
		return count;
	}

	public void saveEmployeeTimesheet(EmployeeTimesheet employeeTimesheet) {
		employeeTimesheetRepo.save(employeeTimesheet);
	}

	public void getEmployeeTimeseetData(String employeeNumber, String month, String year) {
		EmployeeTimesheet byEmployeeNumber = employeeTimesheetRepo.findByEmployeeNumberAndYear(employeeNumber, year);
		int yrInt = Integer.parseInt(year);
		if (byEmployeeNumber != null) {
			if (month.equals("January")) {
				byEmployeeNumber.setJanuary("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("February")) {
				byEmployeeNumber.setFebruary("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("March")) {
				byEmployeeNumber.setMarch("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("April")) {
				byEmployeeNumber.setApril("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("May")) {
				byEmployeeNumber.setMay("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("June")) {
				byEmployeeNumber.setJune("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("July")) {
				byEmployeeNumber.setJuly("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("August")) {
				byEmployeeNumber.setAugust("submitted");
			}
			if (month.equals("September")) {
				byEmployeeNumber.setSeptember("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("October")) {
				byEmployeeNumber.setOctober("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("November")) {
				byEmployeeNumber.setMay("submitted");
				byEmployeeNumber.setYear(yrInt);
			}
			if (month.equals("December")) {
				byEmployeeNumber.setDecember("submitted");
				byEmployeeNumber.setYear(yrInt);
			}

			employeeTimesheetRepo.save(byEmployeeNumber);
		} else {
			EmployeeTimesheet empData = employeeTimesheetRepo.findByEmployeeNumber(employeeNumber);
			EmployeeTimesheet newDataToSave = new EmployeeTimesheet();
			newDataToSave.setEmployeeName(empData.getEmployeeName());
			newDataToSave.setEmployeeNumber(empData.getEmployeeNumber());
			newDataToSave.setEmail(empData.getEmail());
			String status = "pending";
			newDataToSave.setJanuary(status);
			newDataToSave.setFebruary(status);
			newDataToSave.setMarch(status);
			newDataToSave.setApril(status);
			newDataToSave.setMay(status);
			newDataToSave.setJune(status);
			newDataToSave.setJuly(status);
			newDataToSave.setAugust(status);
			newDataToSave.setSeptember(status);
			newDataToSave.setOctober(status);
			newDataToSave.setNovember(status);
			newDataToSave.setDecember(status);
			if (month.equals("January")) {
				newDataToSave.setJanuary("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("February")) {
				newDataToSave.setFebruary("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("March")) {
				newDataToSave.setMarch("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("April")) {
				newDataToSave.setApril("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("May")) {
				newDataToSave.setMay("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("June")) {
				newDataToSave.setJune("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("July")) {
				newDataToSave.setJuly("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("August")) {
				newDataToSave.setAugust("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("September")) {
				newDataToSave.setSeptember("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("October")) {
				newDataToSave.setOctober("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("November")) {
				newDataToSave.setMay("submitted");
				newDataToSave.setYear(yrInt);
			}
			if (month.equals("December")) {
				newDataToSave.setDecember("submitted");
				newDataToSave.setYear(yrInt);
			}
			employeeTimesheetRepo.save(newDataToSave);
		}
	}

	public List<EmployeeTimesheet> getPendingEmployeesTimesheetByMonth(String month, int year) {
		String status = "pending";
		if (month.equals("January")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByJanuaryAndYear(status, year);
			return data;
		}
		if (month.equals("February")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByFebruaryAndYear(status, year);
			return data;
		}
		if (month.equals("March")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByMarchAndYear(status, year);
			return data;
		}
		if (month.equals("April")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByAprilAndYear(status, year);
			return data;
		}
		if (month.equals("May")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByMayAndYear(status, year);
			return data;
		}
		if (month.equals("June")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByJuneAndYear(status, year);
			return data;
		}
		if (month.equals("July")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByJulyAndYear(status, year);
			return data;
		}
		if (month.equals("August")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByAugustAndYear(status, year);
			return data;
		}
		if (month.equals("September")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findBySeptemberAndYear(status, year);
			return data;
		}
		if (month.equals("October")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByOctoberAndYear(status, year);
			return data;
		}
		if (month.equals("November")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByNovemberAndYear(status, year);
			return data;
		}
		if (month.equals("December")) {
			List<EmployeeTimesheet> data = employeeTimesheetRepo.findByDecemberAndYear(status, year);
			return data;
		}
		return null;
	}

	public void saveEmployeeTimesheet(String employeeName, String employeeNumber, String email) {

		String status = "pending";
		EmployeeTimesheet employeeTimesheet = new EmployeeTimesheet();

		employeeTimesheet.setEmployeeName(employeeName);
		employeeTimesheet.setEmployeeNumber(employeeNumber);
		employeeTimesheet.setEmail(email);
		employeeTimesheet.setJanuary(status);
		employeeTimesheet.setFebruary(status);
		employeeTimesheet.setMarch(status);
		employeeTimesheet.setApril(status);
		employeeTimesheet.setMay(status);
		employeeTimesheet.setJune(status);
		employeeTimesheet.setJuly(status);
		employeeTimesheet.setAugust(status);
		employeeTimesheet.setSeptember(status);
		employeeTimesheet.setOctober(status);
		employeeTimesheet.setNovember(status);
		employeeTimesheet.setDecember(status);
		int yr = Year.now().getValue();
		employeeTimesheet.setYear(yr);
		employeeTimesheetRepo.save(employeeTimesheet);
	}

	public EmployeeTimesheet findByEmployeeEmail(String email) {
		EmployeeTimesheet empEmail = employeeTimesheetRepo.findByEmail(email);
		return empEmail;
	}

	public String subjectForTimesheetReminder() {
		String subject = "Reminder for Submission of Timesheet";
		return subject;
	}

	public String getReminderEmailBody(EmployeeTimesheet body) {
		String emailBody = null;
		String employeeName = body.getEmployeeName();
		emailBody = "Dear " + employeeName + ",\n\n" + "Hope this finds you well.\n\n"
				+ "We request you to submit your timesheet as soon as possible.\n\n"
				+ "Thanks & Regards\n" + "HR Team\n" + "www.kraftsoftwaresolution.com";
		return emailBody;
	}
	
	public RegisterEmployee findEmployeeByEmail(String email) {
		return employeeRepo.findByEmail(email);
	}
	public EmployeeTimesheet findByEmpEmail(String email) {
		return employeeTimesheetRepo.findByEmail(email);
	}
	
	public String subjectForTimesheetReminder(String month, int year) {
	    String subject = null;
	    if (month.equals("january")) {
	        subject = "Reminder for Submission of Timesheet for the month of January " + year;
	    } else if (month.equals("february")) {
	        subject = "Reminder for Submission of Timesheet for the month of February " + year;
	    } else if (month.equals("march")) {
	        subject = "Reminder for Submission of Timesheet for the month of March " + year;
	    } else if (month.equals("april")) {
	        subject = "Reminder for Submission of Timesheet for the month of April " + year;
	    } else if (month.equals("may")) {
	        subject = "Reminder for Submission of Timesheet for the month of May " + year;
	    } else if (month.equals("june")) {
	        subject = "Reminder for Submission of Timesheet for the month of June " + year;
	    } else if (month.equals("july")) {
	        subject = "Reminder for Submission of Timesheet for the month of July " + year;
	    } else if (month.equals("august")) {
	        subject = "Reminder for Submission of Timesheet for the month of August " + year;
	    } else if (month.equals("september")) {
	        subject = "Reminder for Submission of Timesheet for the month of September " + year;
	    } else if (month.equals("october")) {
	        subject = "Reminder for Submission of Timesheet for the month of October " + year;
	    } else if (month.equals("november")) {
	        subject = "Reminder for Submission of Timesheet for the month of November " + year;
	    } else if (month.equals("december")) {
	        subject = "Reminder for Submission of Timesheet for the month of December " + year;
	    }
	    return subject;
	}
	
//	----------------------------------AppliedCandidateInformationServices----------------------------------

	public AppliedCandidateInformation saveAppliedCandidateInfo(AppliedCandidateInformation appliedCandidateInfo) {
		return appliedCandidaterepo.save(appliedCandidateInfo);
	}

	public boolean appliedCandidateInfoExists(AppliedCandidateInformation appliedCandidateInfo) {
		if (appliedCandidaterepo.findByJobIdAndEmail(appliedCandidateInfo.getJobId(),
				appliedCandidateInfo.getEmail()) != null) {
			return true;
		} else {
			return false;
		}
	}

	public AppliedCandidateInformation findByEmail(String email) {
		AppliedCandidateInformation candidateEmail = appliedCandidaterepo.findByEmail(email);
		return candidateEmail;
	}

	public Iterable<AppliedCandidateInformation> getAllAppliedCandidates() {
		String str = "applied";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	public Iterable<AppliedCandidateInformation> getAllScreeningCandidates() {
		String str = "Screening";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	public Iterable<AppliedCandidateInformation> getAllTechnicalOneCandidates() {
		String str = "TechnicalRoundOne";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	public Iterable<AppliedCandidateInformation> getAllTechnicalTwoCandidates() {
		String str = "TechnicalRoundTwo";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	public Iterable<AppliedCandidateInformation> getAllHRCandidates() {
		String str = "HR";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	public Iterable<AppliedCandidateInformation> getAllSelectedCandidates() {
		String str = "Selected";
		Iterable<AppliedCandidateInformation> findByStatus = appliedCandidaterepo.findByStatus(str);
		return findByStatus;
	}

	public String deleteCandidateInformation(String email, String status, String jobId, String role) {
		appliedCandidaterepo.deleteByEmail(email);
		appliedCandidaterepo.findByJobIdAndRole(jobId, role);
		String subject = null;
		if ("applied".equals(status)) {
			subject = "Application Status - " + role + "(" + jobId + ")";
		}
		if ("Screening".equals(status)) {
			subject = "Update on Your Application - " + role + "(" + jobId + ")";
		}
		if ("TechnicalRoundOne".equals(status)) {
			subject = "Update on Your Application - " + role + "(" + jobId + ")";
		}
		if ("TechnicalRoundTwo".equals(status)) {
			subject = "Update on Your Application - " + role + "(" + jobId + ")";
		}
		if ("HR".equals(status)) {
			subject = "Update on Your Application - " + role + "(" + jobId + ")";
		}
		return subject;
	}

	public String emailBodyForDelete(AppliedCandidateInformation body) {
		String emailBody = null;
		String fullName = body.getFullName();
		String status = body.getStatus();
		if ("applied".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "Thank you for your interest. We regret to inform you that at this time, your application will not be"
					+ " further considered for this role.We sincerely appreciate your interest in Vkraft,"
					+ " and we encourage you to keep an eye on our career opportunities. Your skills and experience may be"
					+ " an excellent fit for future openings, and we would welcome the opportunity to consider your application again..\n\n"
					+ "Thank you for considering us as your potential employer, and we wish you success in your job search."
					+ " If you have any questions or would like feedback on your application, please do not hesitate to reach out.\n\n"
					+ "We value your understanding and hope you find the perfect match for your career goals in the near future.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("Screening".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "We appreciate the time and effort you invested in the screening process."
					+ " After careful consideration, we regret to inform you that your application did not progress to the next stage."
					+ " We genuinely appreciate your interest in joining our team and we encourage you to keep an eye on our career opportunities. "
					+ "Your skills and experience may be an excellent fit for future openings, and we would welcome the opportunity "
					+ "to consider your application again..\n\n"
					+ "Thank you for considering us as your potential employer, and we wish you success in your job search."
					+ " If you have any questions or would like feedback on your application, please do not hesitate to reach out.\n\n"
					+ "We value your understanding and hope you find the perfect match for your career goals in the near future.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("TechnicalRoundOne".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "Thank you for your participation in the First Technical Interview."
					+ " After careful evaluation and consideration, we regret to inform you that your application did not progress to the next stage."
					+ " We appreciate the effort and time you dedicated to the interview process. Your skills and experience are commendable. "
					+ "Please be assured that this decision does not diminish your accomplishments and capabilities.\n\n "
					+ "We encourage you to continue pursuing your career goals, and we wish you success in all your future endeavors."
					+ " If you have any inquiries or would like constructive feedback, please feel free to reach out.\n\n"
					+ "Thank you for considering VKRAFT Software Services. We value the opportunity to connect with you during the hiring process.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("TechnicalRoundTwo".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "Thank you for your participation in the Second Technical Interview."
					+ " After careful evaluation and consideration, we regret to inform you that your application did not progress to the next stage."
					+ " We appreciate the effort and time you dedicated to the interview process. Your skills and experience are commendable. "
					+ "Please be assured that this decision does not diminish your accomplishments and capabilities.\n\n "
					+ "We encourage you to continue pursuing your career goals, and we wish you success in all your future endeavors."
					+ " If you have any inquiries or would like constructive feedback, please feel free to reach out.\n\n"
					+ "Thank you for considering VKRAFT Software Services. We value the opportunity to connect with you during the hiring process.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("HR".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "Thank you for your participation in the HR Interview which is the final round of interview process."
					+ " After careful evaluation and consideration, we regret to inform you that your application did not progress to the next stage."
					+ " We appreciate the effort and time you dedicated to the interview process. Your skills and experience are commendable. "
					+ "Please be assured that this decision does not diminish your accomplishments and capabilities.\n\n "
					+ "We encourage you to continue pursuing your career goals, and we wish you success in all your future endeavors."
					+ " If you have any inquiries or would like constructive feedback, please feel free to reach out.\n\n"
					+ "Thank you for considering VKRAFT Software Services. We value the opportunity to connect with you during the hiring process.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		return emailBody;
	}

	public String selectCandidateInformation(String status, String jobId, String role) {
		appliedCandidaterepo.findByJobIdAndRole(jobId, role);
		String subject = null;
		if ("applied".equals(status)) {
			subject = "Application for " + role + "(" + jobId + ")" + " is Received Successfully";
		}
		if ("Screening".equals(status)) {
			subject = "Congratulations!  You've passed the Screening Process";
		}
		if ("TechnicalRoundOne".equals(status)) {
			subject = "Congratulations!  You've Been Selected for the First Technical Interview";
		}
		if ("TechnicalRoundTwo".equals(status)) {
			subject = "Congratulations!  You've Been Selected for the Second Technical Interview";
		}
		if ("HR".equals(status)) {
			subject = "Congratulations!  You've Been Selected for the HR Interview";
		}
		if ("Selected".equals(status)) {
			subject = "Congratulations! You've been Selected";
		}
		return subject;
	}

	public String emailBodyForSelect(AppliedCandidateInformation body) {
		String emailBody = null;
		String fullName = body.getFullName();
		String status = body.getStatus();
		String role = body.getRole();
		if ("applied".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "Hope this finds you well. Bravo! You are eligible to be part of our recruitment process. We would like to"
					+ " extend our gratitude for your interest in joining the Vkraft team and for taking the time to submit your application for the"
					+ role + ".\n\n"
					+ " We wanted to inform you that we have successfully received your application. Our team is currently in the process "
					+ "of reviewing all applications thoroughly to identify the candidates who most qualified and experienced for the role "
					+ "and who are a good fit for our company culture. This can take some time, as we strive to ensure a fair and "
					+ "comprehensive assessment of all applicants.\n\n"
					+ "Please rest assured that your application is important to us, and we will carefully consider your qualifications "
					+ "and experience in relation to the position's requirements. If your qualifications meet the requirements of the role,"
					+ " we will be in touch with you to discuss the next steps in the recruitment process.\n\n"
					+ "In the meantime, if you have any questions or wish to provide additional information, please feel free to reach "
					+ "out to our Human Resources team at HR@vkraftsoftware.com\n\n"
					+ "We're excited to hear that you're interested in working at Vkraft! We'll be in touch soon.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("Screening".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "I hope this email finds you well. We are pleased to inform you that you have successfully qualified in the screening "
					+ "round for the " + role + ".\n\n"
					+ "Your application and performance in the screening process have been impressive, and we are excited about the "
					+ "prospect of getting to know you better in the upcoming stages of our selection process.\n\n"
					+ "The next step in our process is the technical interview, where we will delve deeper into your technical skills and expertise."
					+ " We encourage you to prepare thoroughly for this stage, as it will provide an opportunity for us to assess your abilities in "
					+ "more detail.\n\n"
					+ "We will be in touch shortly to schedule the technical interview and to provide you with any additional details you may need. "
					+ "If you have any questions or require further information, please feel free to reach out.\n\n"
					+ "Congratulations once again on your success in the screening round, and we look forward to seeing you shine in the technical interview.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("TechnicalRoundOne".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "I trust this message finds you well. We are delighted to inform you that you have successfully qualified in the first "
					+ "round of technical interviews for the " + role + ".\n\n"
					+ " Your technical proficiency and problem-solving abilities have stood out, and we are impressed with your performance."
					+ " Congratulations on this achievement!\n\n"
					+ "The next step in our evaluation process is the second technical interview, where we will delve deeper into specific "
					+ "aspects of your skill set. We encourage you to continue your preparations and to focus on areas that may be "
					+ "highlighted in this upcoming round.\n\n "
					+ "We will be reaching out shortly to schedule the second technical interview and to provide you with any additional details you may need.\n\n"
					+ "If you have any questions or require further information, please feel free to reach out. Once again, "
					+ "congratulations on your success in the first technical interview, and we look forward to seeing you excel in the next round.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("TechnicalRoundTwo".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "I hope this email finds you in good spirits. It is with great pleasure that we share the exciting news – you have "
					+ "successfully qualified in the second round of technical interviews for the " + role + ".\n\n"
					+ "Your technical expertise, problem-solving skills, and overall performance have truly impressed our team. "
					+ "Congratulations on reaching this significant milestone!\n\n"
					+ "The next phase of our selection process is the HR interview, where we aim to understand more about you as an individual "
					+ "and how your skills align with our team culture. We recommend preparing for questions related to your experiences, "
					+ "work style, and your aspirations.\n\n "
					+ "We will be in touch shortly to schedule the HR interview and provide you with any additional details needed for your preparation.\n\n"
					+ "Should you have any questions or require further information, please don't hesitate to reach out. Once again, "
					+ "congratulations on your success in the second technical interview, and we look forward to getting to know you better in the HR round.\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("HR".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "I trust this email finds you well. It is with great pleasure that we inform you of your successful qualification in the HR interview "
					+ "for the " + role + ".\n\n"
					+ " Your impressive communication skills, professionalism, and the insights you shared during the interview have reinforced our "
					+ "belief in your potential contribution to our team. Congratulations on this significant achievement!\n\n"
					+ "As we move forward in the final stages of our selection process, please be assured that your application is currently "
					+ "under careful consideration. We are thoroughly evaluating all aspects to ensure that we make the most informed decision.\n\n "
					+ "We appreciate your patience during this process and anticipate providing you with the final decision soon. If you have any "
					+ "questions or need further information in the meantime, please feel free to reach out.\n\n"
					+ "Once again, congratulations on your success in the HR interview. We are excited about the prospect of potentially welcoming you "
					+ "to our team.\n\n" + "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		if ("Selected".equals(status)) {
			emailBody = "Dear " + fullName + ",\n\n"
					+ "I am thrilled to officially extend my heartfelt congratulations to you! After a thorough and competitive selection process, "
					+ "we are delighted to offer you the position of " + role + ".\n\n"
					+ "Your exceptional skills, experience, and the impressive way you navigated through each stage of the interview process have "
					+ "truly set you apart. We are confident that your contributions will make a significant impact on our team and organization.\n\n "
					+ "In the coming days, our HR team will be in touch to discuss the formalities, including the issuance of the official offer letter, "
					+ "your start date, compensation details, and any additional information you may need. If you have any immediate questions or concerns, "
					+ "please feel free to reach out.\n\n"
					+ "Once again, congratulations on this well-deserved achievement! We are excited to welcome you to VKRAFT Software Services and "
					+ "look forward to a successful and fulfilling journey together. \n\n" + "Welcome aboard!\n\n"
					+ "Best regards,\n" + "HR Team,\n" + "Vkraft Software Services Pvt Ltd\n"
					+ "www.vkraftsoftware.com\n" + "www.kraftsoftwaresolution.com";
		}
		return emailBody;
	}

	public List<AppliedCandidateInformation> getCountOfSelectedCandidates() {
		String status = "applied";
		List<AppliedCandidateInformation> countByStatus = appliedCandidaterepo.countByStatus(status);
		return countByStatus;
	}

	public Long getCountOfAppliedCandidate() {
		String status = "applied";
		Iterable<AppliedCandidateInformation> candidates = appliedCandidaterepo.findByStatus(status);
		long count = StreamSupport.stream(candidates.spliterator(), false).count();
		return count;
	}

	public Long getCountOfScreeningCandidate() {
		String status = "Screening";
		Iterable<AppliedCandidateInformation> candidates = appliedCandidaterepo.findByStatus(status);
		long count = StreamSupport.stream(candidates.spliterator(), false).count();
		return count;
	}

	public Long getCountOfTechnicalRoundOne() {
		String status = "TechnicalRoundOne";
		Iterable<AppliedCandidateInformation> candidates = appliedCandidaterepo.findByStatus(status);
		long count = StreamSupport.stream(candidates.spliterator(), false).count();
		return count;
	}

	public Long getCountOfTechnicalRoundTwo() {
		String status = "TechnicalRoundTwo";
		Iterable<AppliedCandidateInformation> candidates = appliedCandidaterepo.findByStatus(status);
		long count = StreamSupport.stream(candidates.spliterator(), false).count();
		return count;
	}

	public Long getCountOfHRRound() {
		String status = "HR";
		Iterable<AppliedCandidateInformation> candidates = appliedCandidaterepo.findByStatus(status);
		long count = StreamSupport.stream(candidates.spliterator(), false).count();
		return count;
	}

	public Long getCountOfSelected() {
		String status = "Selected";
		Iterable<AppliedCandidateInformation> candidates = appliedCandidaterepo.findByStatus(status);
		long count = StreamSupport.stream(candidates.spliterator(), false).count();
		return count;
	}

//	---------------------------------------CreateJob------------------------------------------

	public CreateJob getJobDetails(String jobId) {
		CreateJob findByJobId = jobRepo.findByJobId(jobId);
		return findByJobId;
	}

	public void saveJob(CreateJob job) {
		jobRepo.save(job);
	}

	public Iterable<CreateJob> getAllJobs() {
		Iterable<CreateJob> findAll = jobRepo.findAll();
		return findAll;
	}

	public boolean deleteJobDetails(String jobId) {
		CreateJob findByJobId = jobRepo.findByJobId(jobId);
		jobRepo.delete(findByJobId);
		return true;
	}

//	---------------------Referred Candidate Methods---------------------------------------

	public boolean referredCandidateInfoExists(ReferredCandidateInformation referredCandidate) {
		if (referredCandidaterepo.findByFullNameAndEmailAndPosition(referredCandidate.getFullName(),
				referredCandidate.getEmail(), referredCandidate.getPosition()) != null) {
			return true;
		} else {
			return false;
		}
	}

	public ReferredCandidateInformation saveReferredCandidateInfo(ReferredCandidateInformation referredCandidate) {
		return referredCandidaterepo.save(referredCandidate);

	}

	public Iterable<ReferredCandidateInformation> getListOfReferredCandidates() {
		Iterable<ReferredCandidateInformation> referredCandidateList = referredCandidaterepo.findAll();
		return referredCandidateList;
	}

	public Long countOfReferredCandidate() {
		return referredCandidaterepo.count();
	}

}
