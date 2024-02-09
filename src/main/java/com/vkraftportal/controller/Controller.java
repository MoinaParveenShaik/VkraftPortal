package com.vkraftportal.controller;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.vkraftportal.model.AppliedCandidateInformation;
import com.vkraftportal.model.CreateJob;
import com.vkraftportal.model.EmployeeTimesheet;
import com.vkraftportal.model.HumanResource;
import com.vkraftportal.model.ReferredCandidateInformation;
import com.vkraftportal.model.RegisterCandidate;
import com.vkraftportal.model.RegisterEmployee;
import com.vkraftportal.model.Timesheet;
import com.vkraftportal.services.Services;

@org.springframework.stereotype.Controller

@RestController
public class Controller extends RouteBuilder {

	@Autowired
	Services services;

	@Override
	public void configure() throws Exception {
		restConfiguration().component("servlet").port(8082).enableCORS(true).host("localhost")
				.bindingMode(RestBindingMode.json);
		onException(Exception.class).handled(true).log("Exception occurred: ${exception.message}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setBody(simple("Internal Server Error: ${exception.message}"));

// ------------------------------------------------1. Register Employee HR Portal Timesheet----------------------------------------------------------------

		rest().post("/registerEmployee").type(RegisterEmployee.class).to("direct:processRegisterEmployee");
		from("direct:processRegisterEmployee").log("RegisterEmployee : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				RegisterEmployee employee = exchange.getIn().getBody(RegisterEmployee.class);
				boolean employeeExists = services.employeeExists(employee);
				if (employeeExists) {
					exchange.getMessage().setBody(
							"User already exists for " + employee.getEmployeeNumber() + " or " + employee.getEmail());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					String randomPassword = services.generateAndSetRandomPassword();
					employee.setPassword(randomPassword);
					services.saveEmployee(employee);
					services.saveEmployeeTimesheet(employee.getEmployeeName(), employee.getEmployeeNumber(),
							employee.getEmail());
					String recipientEmail = employee.getEmail();
					String generatedPassword = randomPassword;
					String emailBody = services.getEmailBody(recipientEmail, generatedPassword,
							employee.getEmployeeName());
					String subject = services.subjectForEmployeeRegistration(employee.getEmail());
					exchange.getMessage().setBody(emailBody);
					exchange.getMessage().setHeader("emailSubject", subject);
					exchange.getMessage().setHeader("recipientEmail", recipientEmail);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
					ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
					producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
							exchange.getMessage().getHeaders());
				}
			}
		});

		from("direct:sendMail").setHeader("Subject", simple("${header.emailSubject}")).process(exchange -> {
			String recipientEmail = exchange.getMessage().getHeader("recipientEmail", String.class);
			String emailBody = exchange.getIn().getBody(String.class);
			exchange.getMessage().setBody(emailBody);
		}).toD("smtps://smtp.gmail.com:465?username=vaibhavilandge97@gmail.com&password=bjdo uoqc vmhc hwef&to=${header.recipientEmail}");

// ----------------------------------------------------2. Save Timesheet Employee Portal---------------------------------------------------

		rest().post("/saveTimesheet").type(Timesheet.class).to("direct:processTimesheet");
		from("direct:processTimesheet").log("Timesheet : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Timesheet timesheetDataToSave = exchange.getIn().getBody(Timesheet.class);
				System.out.println("data: " + timesheetDataToSave);
				if (services.timesheetExists(timesheetDataToSave)) {
					exchange.getMessage()
							.setBody("Timesheet already exists for " + timesheetDataToSave.getMonth() + " month");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					timesheetDataToSave.setStatus("submitted");
					String month = timesheetDataToSave.getMonth();
					String year = timesheetDataToSave.getYear();
					services.getEmployeeTimeseetData(timesheetDataToSave.getEmployeeNumber(), month, year);
					services.saveTimesheet(timesheetDataToSave);
					exchange.getMessage()
							.setBody("Timesheet is saved for " + timesheetDataToSave.getMonth() + " month");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
				}
			}
		}).end();

// ------------------------------------------------------3. Update Timesheets Employee Portal---------------------------------------------------------

		rest().put("/updateTimesheet").param().name("employeeNumber").type(RestParamType.query).endParam().param()
				.name("month").type(RestParamType.query).endParam().param().name("year").type(RestParamType.query)
				.endParam().param().name("clientName").type(RestParamType.query).endParam().param()
				.name("assignmentName").type(RestParamType.query).endParam().param().name("holidaysInput")
				.type(RestParamType.query).endParam().to("direct:updateTimeSheet");
		from("direct:updateTimeSheet").process(exchange -> {
			String employeeNumber = exchange.getIn().getHeader("employeeNumber", String.class);
			String month = exchange.getIn().getHeader("month", String.class);
			String year = exchange.getIn().getHeader("year", String.class);
			String clientName = exchange.getIn().getHeader("clientName", String.class);
			String assignmentName = exchange.getIn().getHeader("assignmentName", String.class);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			Timesheet existingEntity = services.findByEmployeeNumberAndMonthAndYear(employeeNumber, month, year);
			if (existingEntity != null) {
				if (clientName != null) {
					existingEntity.setClientName(clientName);
				}
				if (assignmentName != null) {
					existingEntity.setAssignmentName(assignmentName);
				}
				services.saveTimesheet(existingEntity);
				exchange.getMessage().setBody(existingEntity);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("Error: TimesheetEntity with number '" + employeeNumber + "' not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});

// ---------------------------------------------------------- 4.Delete Timesheet Timesheet------------------------------------------------------

		rest().delete("/deleteTimesheet").param().name("employeeNumber").type(RestParamType.query).endParam().param()
				.name("month").type(RestParamType.query).endParam().param().name("year").type(RestParamType.query)
				.endParam().to("direct:delete");
		from("direct:delete").process(exchange -> {
			String employeeNumber = exchange.getIn().getHeader("employeeNumber", String.class);
			String month = exchange.getIn().getHeader("month", String.class);
			String year = exchange.getIn().getHeader("year", String.class);
			boolean timesheet = services.deleteByEmployeeNumberAndDate(employeeNumber, month, year);
			if (timesheet) {
				exchange.getMessage().setBody("Status: Record for Employee " + employeeNumber + " for Month " + month
						+ " and Year " + year + " Deleted");
			} else {
				exchange.getMessage().setBody("Error: Record not found for Employee " + employeeNumber + " for Month "
						+ month + " and Year " + year);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});

// ---------------------------------------------------------- 5.Delete Employee Timesheet------------------------------------------------------

		rest().get("/deleteEmployeeByNumber").param().name("employeeNumber").type(RestParamType.query).endParam()
				.to("direct:deleteEmployee");
		from("direct:deleteEmployee").process(exchange -> {
			String employeeNumber = exchange.getIn().getHeader("employeeNumber", String.class);
			boolean employee = services.deleteByEmployeeNumber(employeeNumber);
			if (employee) {
				exchange.getMessage().setBody("Status: Record for Employee " + employeeNumber + " Deleted");
			} else {
				exchange.getMessage().setBody("Error: Record not found for Employee " + employeeNumber);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});

// ---------------------------------------------------------6. Get Timesheet HR Portal Timesheet---------------------------------------------------------

		rest().get("/getTimesheet").param().name("employeeNumber").type(RestParamType.query).endParam().param()
				.name("month").type(RestParamType.query).endParam().param().name("year").type(RestParamType.query)
				.endParam().to("direct:processName");
		from("direct:processName").process(exchange -> {
			String number = exchange.getIn().getHeader("employeeNumber", String.class);
			String month = exchange.getIn().getHeader("month", String.class);
			String year = exchange.getIn().getHeader("year", String.class);
			String errorMessage = "";
			if (!errorMessage.isEmpty()) {
				exchange.getMessage().setBody("Error: " + errorMessage.trim());
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			} else {
				Timesheet timeSheetEntity = services.findByEmployeeNumberAndMonthAndYear(number, month, year);
				if (timeSheetEntity != null) {
					exchange.getMessage().setBody(timeSheetEntity);
				} else {
					exchange.getMessage().setBody("Error: TimeSheetEntity with number '" + number + "' not found");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
				}
			}
		});

// -----------------------------------------------7. Get All Employees HR Portal Timesheet----------------------------------------------------

		rest().get("/getAllEmployees").to("direct:getTotalEmployees");

		from("direct:getTotalEmployees").log("Get All Employees request received").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Iterable<RegisterEmployee> allEmployees = services.getAllEmployees();
				exchange.getMessage().setBody(allEmployees);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		}).end();

// -----------------------------------------------8. Verify Employee EmployeeLogin PortalTimesheet----------------------------------------------------------------

		rest().get("/verifyEmployee").param().name("username").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:employee");
		from("direct:employee").process(exchange -> {
			String username = exchange.getIn().getHeader("username", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with username: {} and password: {}", username, password);
			RegisterEmployee employee = services.getEmployeeByUsernameAndPassword(username, password);
			boolean isUserValid = services.validateEmployee(username, password);
			if (isUserValid) {
				exchange.getMessage().setBody(employee);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("User not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

// ------------------------------------------------9. GetApprovedTimesheets HR Portal Timesheet------------------------------------------------------------

		rest().get("/getApprovedTimesheeets").to("direct:approvedTimesheeets");
		from("direct:approvedTimesheeets").process(exchange -> {
			List<Timesheet> approvedEmployees = services.getEmployeesByStatus("approved");
			if (approvedEmployees != null && !approvedEmployees.isEmpty()) {
				exchange.getMessage().setBody(approvedEmployees);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("No employees found with status 'approved'");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});

// ------------------------------------------------------10. Get Pending Employees HR Portal Timesheet-----------------------------------------------------------

		rest().get("/pendingEmployees").param().name("month").type(RestParamType.query).endParam().param().name("year")
				.type(RestParamType.query).endParam().to("direct:getPendingEmployees");
		from("direct:getPendingEmployees").process(exchange -> {
			String month = exchange.getIn().getHeader("month", String.class);
			int year = exchange.getIn().getHeader("year", Integer.class);
			List<EmployeeTimesheet> pendingEmployees = services.getPendingEmployeesTimesheetByMonth(month, year);

			if (pendingEmployees != null && !pendingEmployees.isEmpty()) {
				exchange.getMessage().setBody(pendingEmployees);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("No employees found with status 'pending'");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});

// ----------------------------------------------------------11. Get Employee Count Timesheet HR Portal-------------------------------------------------------------

		rest().get("/getEmployeeCount").to("direct:getEmployee");
		from("direct:getEmployee").process(exchange -> {
			Long employeeCount = services.getCountOfEmployee();
			exchange.getMessage().setBody(employeeCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

// ---------------------------------------------------------12. RegisterCandidate After-Onboarding--------------------------------------------------------

		rest().post("/registerCandidate").type(RegisterCandidate.class).to("direct:processRegisterCandidate");
		from("direct:processRegisterCandidate").log("RegisterCandidate : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				RegisterCandidate candidate = exchange.getIn().getBody(RegisterCandidate.class);
				boolean candidateExists = services.candidateExists(candidate);
				if (candidateExists) {
					exchange.getMessage().setBody("Candidate already exists for " + candidate.getEmail());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					String randomPassword = services.generateAndSetRandomPassword();
					candidate.setPassword(randomPassword);
					services.saveCandidate(candidate);
					String recipientEmail = candidate.getEmail();
					String generatedPassword = randomPassword;
					String emailBody = services.getEmailBody(recipientEmail, generatedPassword);
					exchange.getMessage().setBody(emailBody);
					exchange.getMessage().setHeader("recipientEmail", recipientEmail);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
					ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
					producerTemplate.sendBodyAndHeaders("direct:sendingMail", exchange.getMessage().getBody(),
							exchange.getMessage().getHeaders());
				}
			}
		});

		from("direct:sendingMail").setHeader("Subject", constant("Your Login details for On-Boarding portal"))
				.process(exchange -> {
					String recipientEmail = exchange.getMessage().getHeader("recipientEmail", String.class);
					String emailBody = exchange.getIn().getBody(String.class);
					exchange.getMessage().setBody(emailBody);
				}).recipientList(simple(
						"smtps://smtp.gmail.com:465?username=vaibhavilandge97@gmail.com&password=bjdo uoqc vmhc hwef&to=${header.recipientEmail}"));

// --------------------------------------------------------13. Candidate Login After On-Boarding------------------------------------------------------------

		rest().get("/verifyCandidate").param().name("email").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:Candidate");
		from("direct:Candidate").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with email: {} and password: {}", email, password);
			RegisterCandidate candidate = services.getCandidateByEmail(email, password);
			boolean isCandidateValid = services.verifyCandidate(candidate);
			if (isCandidateValid) {
				exchange.getMessage().setBody(candidate);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("Candidate not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

//--------------------------------------------------14. Save Applied Candidate Information Before-OnBoarding-------------------------------------------------

		rest().post("/saveAppliedCandidateInformation").type(AppliedCandidateInformation.class)
				.to("direct:candidateInfo");
		from("direct:candidateInfo").log("AppliedCandidateInfo : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				AppliedCandidateInformation appliedCandidateInfo = exchange.getIn()
						.getBody(AppliedCandidateInformation.class);
				if (services.appliedCandidateInfoExists(appliedCandidateInfo)) {
					exchange.getMessage()
							.setBody(appliedCandidateInfo.getFullName() + " already applied for this position");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					appliedCandidateInfo.setStatus("applied");
					services.saveAppliedCandidateInfo(appliedCandidateInfo);
					exchange.getMessage()
							.setBody(appliedCandidateInfo.getFullName() + " succesfully applied for this position");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
					String subject = services.selectCandidateInformation(appliedCandidateInfo.getStatus(),
							appliedCandidateInfo.getJobId(), appliedCandidateInfo.getRole());
					String emailBody = services.emailBodyForSelect(appliedCandidateInfo);
					exchange.getMessage().setHeader("emailSubject", subject);
					exchange.getMessage().setBody(emailBody);
					exchange.getMessage().setHeader("recipientEmail", appliedCandidateInfo.getEmail());
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
					ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
					producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
							exchange.getMessage().getHeaders());
				}
			}
		}).end();

//		-----------------------------------------15. Login Before On-Boarding---------------------------------------------------------------------------

		rest().get("/verifyLogin").param().name("email").type(RestParamType.query).endParam().param().name("password")
				.type(RestParamType.query).endParam().to("direct:processLogin");
		from("direct:processLogin").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with email: {} and password: {}", email, password);
			RegisterCandidate registerCandidate = services.getCandidateByEmailAndPassword(email, password);
			boolean isCandidateValid = services.verifyCandidate(registerCandidate);
			if (isCandidateValid) {
				exchange.getMessage().setBody(registerCandidate);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("Candidate not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

//		---------------------------------------------------16. Forgot Password--------------------------------------------------------------------------

		rest().put("/forgetPassword").param().name("email").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:processPassword");
		from("direct:processPassword").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			RegisterCandidate registerCandidate = services.findCandidateByEmail(email);
			if (services.candidateExists(registerCandidate)) {
				registerCandidate.setPassword(password);
				services.saveCandidate(registerCandidate);
				if (!services.isValidPassword(registerCandidate.getPassword())) {
					exchange.getMessage().setBody(
							"Password should have at least one lowercase, one uppercase, one digit, and one special character & minimum length 8");
					return;
				} else {
					exchange.getMessage().setBody(registerCandidate);
					exchange.getMessage().setBody("Password updated succussfully");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
				}
			} else {
				exchange.getMessage().setBody("Please provide valid emailid");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

//		------------------------------------------------------17. Register Job Before On-Boarding---------------------------------------------------------------------------

		rest().post("/registerJob").type(CreateJob.class).to("direct:saveJob");
		from("direct:saveJob").log("Job : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				CreateJob body = exchange.getIn().getBody(CreateJob.class);
				CreateJob jobDetails = services.getJobDetails(body.getJobId());
				if (jobDetails != null) {
					exchange.getMessage().setBody("Job details already exists for job id : " + body.getJobId());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					services.saveJob(body);
					exchange.getMessage().setBody("Job details are saved for : " + body.getJobId());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
				}
			}
		});

//		----------------------------------18. Get All Jobs-----------------------------------

		rest().get("/getAllJobs").to("direct:processJobs");
		from("direct:processJobs").log("Jobs List").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Iterable<CreateJob> allJobs = services.getAllJobs();
				exchange.getMessage().setBody(allJobs);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		});

//		-----------------------------------19. Delete Job Details--------------------------------------------

		rest().get("/deleteJobDetails").param().name("jobId").type(RestParamType.query).endParam()
				.to("direct:deleteJobDetails");
		from("direct:deleteJobDetails").log("deleted Job Details : ${body}").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				String jobId = exchange.getIn().getHeader("jobId", String.class);
				boolean isDeleted = services.deleteJobDetails(jobId);
				if (!isDeleted) {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
					exchange.getMessage().setBody("Job with ID " + jobId + " not found or already deleted.");
				} else {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
					exchange.getMessage().setBody("Job with ID " + jobId + " successfully deleted.");
				}
			}
		});

//		-----------------------------------------------20. Register Candidate Before On-Boarding----------------------------------------------

		rest().post("/saveRegisterCandidate").type(RegisterCandidate.class).to("direct:processRegister");
		from("direct:processRegister").log("RegisterCandidate : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				RegisterCandidate registercandidate = exchange.getIn().getBody(RegisterCandidate.class);
				if (!services.isValidMobileNumber(registercandidate.getMobileNumber())) {
					exchange.getMessage().setBody("Please provide Valid Mobile Number");
					return;
				}
				if (!services.isValidPassword(registercandidate.getPassword())) {
					exchange.getMessage().setBody(
							"Password should have at least one lowercase, one uppercase, one digit, and one special character & minimum length 8");
					return;
				}
				if (services.candidateExists(registercandidate)) {
					exchange.getMessage().setBody("Candidate already exists with " + registercandidate.getEmail());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					services.saveCandidate(registercandidate);
					exchange.getMessage()
							.setBody(registercandidate.getFullname() + " your registration is successful.");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
				}
			}
		}).end();

//		--------------------------------------------21. Applied Candidates List Before On-Boarding--------------------------------------------------------

		rest().get("/listOfAppliedCandidates").to("direct:appliedCandidates");
		from("direct:appliedCandidates").log("Get all applied candidates request received").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Iterable<AppliedCandidateInformation> allAppliedCandidates = services.getAllAppliedCandidates();
				exchange.getMessage().setBody(allAppliedCandidates);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		});

//		------------------------22. After clicking on screening should be moved to Screening Before On-Boarding--------------------------

		rest().post("/selectedForScreening").param().name("email").type(RestParamType.query).endParam()
				.to("direct:selectedForScreeningRound");
		from("direct:selectedForScreeningRound").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			AppliedCandidateInformation candidateInfo = services.findByEmail(email);
			if (candidateInfo != null) {
				String status = exchange.getIn().getHeader("status", String.class);
				String newStatus = (status != null) ? status : "Screening";
				candidateInfo.setStatus(newStatus);
				services.saveAppliedCandidateInfo(candidateInfo);
				exchange.getMessage().setBody(candidateInfo);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
			String subject = services.selectCandidateInformation(candidateInfo.getStatus(), candidateInfo.getJobId(),
					candidateInfo.getRole());
			String emailBody = services.emailBodyForSelect(candidateInfo);
			exchange.getMessage().setHeader("emailSubject", subject);
			exchange.getMessage().setBody(emailBody);
			exchange.getMessage().setHeader("recipientEmail", candidateInfo.getEmail());
			exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
			producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
					exchange.getMessage().getHeaders());

		});

//		--------------------------------------------23. Screening Candidates List Before On-Boarding-------------------------------------

		rest().get("/listOfScreeningCandidates").to("direct:screeningCandidates");
		from("direct:screeningCandidates").log("Get all screening candidates request received")
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						Iterable<AppliedCandidateInformation> allScreeningCandidates = services
								.getAllScreeningCandidates();
						exchange.getMessage().setBody(allScreeningCandidates);
						exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
					}
				});

//		--------------24. After clicking on Selected should be moved to technicalOne Before On-Boarding--------------------

		rest().post("/selectedForTechnicalOne").param().name("email").type(RestParamType.query).endParam()
				.to("direct:selectedForTechnicalRoundOne");
		from("direct:selectedForTechnicalRoundOne").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			AppliedCandidateInformation candidateInfo = services.findByEmail(email);
			if (candidateInfo != null) {
				String status = exchange.getIn().getHeader("status", String.class);
				String newStatus = (status != null) ? status : "TechnicalRoundOne";
				candidateInfo.setStatus(newStatus);
				services.saveAppliedCandidateInfo(candidateInfo);
				exchange.getMessage().setBody(candidateInfo);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
			String subject = services.selectCandidateInformation(candidateInfo.getStatus(), candidateInfo.getJobId(),
					candidateInfo.getRole());
			String emailBody = services.emailBodyForSelect(candidateInfo);
			exchange.getMessage().setHeader("emailSubject", subject);
			exchange.getMessage().setBody(emailBody);
			exchange.getMessage().setHeader("recipientEmail", candidateInfo.getEmail());
			exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
			producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
					exchange.getMessage().getHeaders());
		});

//		-----------------------25. Technical Round One Candidate List--------------------

		rest().get("/listOfTechnicalRoundOneCandidates").to("direct:technicalRoundOneCandidates");
		from("direct:technicalRoundOneCandidates").log("Get all technical round one candidates request received")
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						Iterable<AppliedCandidateInformation> getAllTechnicalOneCandidates = services
								.getAllTechnicalOneCandidates();
						exchange.getMessage().setBody(getAllTechnicalOneCandidates);
						exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
					}
				});

//		--------------26. After clicking on Selected should be moved to technicalTwo Before On-Boarding--------------------

		rest().post("/selectedForTechnicalTwo").param().name("email").type(RestParamType.query).endParam()
				.to("direct:selectedForTechnicalRoundTwo");
		from("direct:selectedForTechnicalRoundTwo").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			AppliedCandidateInformation candidateInfo = services.findByEmail(email);
			if (candidateInfo != null) {
				String status = exchange.getIn().getHeader("status", String.class);
				String newStatus = (status != null) ? status : "TechnicalRoundTwo";
				candidateInfo.setStatus(newStatus);
				services.saveAppliedCandidateInfo(candidateInfo);
				exchange.getMessage().setBody(candidateInfo);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
			String subject = services.selectCandidateInformation(candidateInfo.getStatus(), candidateInfo.getJobId(),
					candidateInfo.getRole());
			String emailBody = services.emailBodyForSelect(candidateInfo);
			exchange.getMessage().setHeader("emailSubject", subject);
			exchange.getMessage().setBody(emailBody);
			exchange.getMessage().setHeader("recipientEmail", candidateInfo.getEmail());
			exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
			producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
					exchange.getMessage().getHeaders());
		});

//		-----------------------27. Technical Round Two Candidate List--------------------

		rest().get("/listOfTechnicalRoundTwoCandidates").to("direct:technicalRoundTwoCandidates");
		from("direct:technicalRoundTwoCandidates").log("Get all technical round two candidates request received")
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						Iterable<AppliedCandidateInformation> getAllTechnicalTwoCandidates = services
								.getAllTechnicalTwoCandidates();
						exchange.getMessage().setBody(getAllTechnicalTwoCandidates);
						exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
					}
				});

//		--------------28. After clicking on Selected should be moved to HR Before On-Boarding--------------------

		rest().post("/selectedForHR").param().name("email").type(RestParamType.query).endParam()
				.to("direct:selectedForHRRound");
		from("direct:selectedForHRRound").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			AppliedCandidateInformation candidateInfo = services.findByEmail(email);
			if (candidateInfo != null) {
				String status = exchange.getIn().getHeader("status", String.class);
				String newStatus = (status != null) ? status : "HR";
				candidateInfo.setStatus(newStatus);
				services.saveAppliedCandidateInfo(candidateInfo);
				exchange.getMessage().setBody(candidateInfo);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
			String subject = services.selectCandidateInformation(candidateInfo.getStatus(), candidateInfo.getJobId(),
					candidateInfo.getRole());
			String emailBody = services.emailBodyForSelect(candidateInfo);
			exchange.getMessage().setHeader("emailSubject", subject);
			exchange.getMessage().setBody(emailBody);
			exchange.getMessage().setHeader("recipientEmail", candidateInfo.getEmail());
			exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
			producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
					exchange.getMessage().getHeaders());
		});

//		-----------------------29. HR Candidate List--------------------

		rest().get("/listOfHRRoundCandidates").to("direct:hRRoundCandidates");
		from("direct:hRRoundCandidates").log("Get all HR round candidates request received").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Iterable<AppliedCandidateInformation> getAllHRCandidates = services.getAllHRCandidates();
				exchange.getMessage().setBody(getAllHRCandidates);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		});

//		--------------30. After clicking on Selected should be moved to Selected Before On-Boarding--------------------
		rest().post("/candidatesSelectedInAllRounds").param().name("email").type(RestParamType.query).endParam()
				.to("direct:selectedInAllRounds");
		from("direct:selectedInAllRounds").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			AppliedCandidateInformation candidateInfo = services.findByEmail(email);
			if (candidateInfo != null) {
				String status = exchange.getIn().getHeader("status", String.class);
				String newStatus = (status != null) ? status : "Selected";
				candidateInfo.setStatus(newStatus);
				services.saveAppliedCandidateInfo(candidateInfo);
				exchange.getMessage().setBody(candidateInfo);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
			String subject = services.selectCandidateInformation(candidateInfo.getStatus(), candidateInfo.getJobId(),
					candidateInfo.getRole());
			String emailBody = services.emailBodyForSelect(candidateInfo);
			exchange.getMessage().setHeader("emailSubject", subject);
			exchange.getMessage().setBody(emailBody);
			exchange.getMessage().setHeader("recipientEmail", candidateInfo.getEmail());
			exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
			producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
					exchange.getMessage().getHeaders());
		});

//		-----------------------31. Selected Candidate List--------------------

		rest().get("/listOfSelectedCandidates").to("direct:selectedCandidates");
		from("direct:selectedCandidates").log("Get all selected candidates request received").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Iterable<AppliedCandidateInformation> getAllSelectedCandidates = services.getAllSelectedCandidates();
				exchange.getMessage().setBody(getAllSelectedCandidates);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		});

//		--------------32. After clicking reject delete record and send mail------------------

		rest().post("/deleteRecord").param().name("email").type(RestParamType.query).endParam()
				.to("direct:deleteCandidateInformation");
		from("direct:deleteCandidateInformation").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			AppliedCandidateInformation candidateInfo = services.findByEmail(email);
			String subject = services.deleteCandidateInformation(candidateInfo.getEmail(), candidateInfo.getStatus(),
					candidateInfo.getJobId(), candidateInfo.getRole());
			String emailBody = services.emailBodyForDelete(candidateInfo);
			exchange.getMessage().setHeader("emailSubject", subject);
			exchange.getMessage().setBody(emailBody);
			exchange.getMessage().setHeader("recipientEmail", candidateInfo.getEmail());
			exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
			ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
			producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
					exchange.getMessage().getHeaders());
		});

//		 ------------------------33. Get Count Of Applied Candidates Before On-Boarding------------------------------

		rest().get("/getAppliedCandidatesCount").to("direct:getAppliedCandidatesCount");
		from("direct:getAppliedCandidatesCount").process(exchange -> {
			Long appliedCandidateCount = services.getCountOfAppliedCandidate();
			exchange.getMessage().setBody(appliedCandidateCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		 ------------------------34. Get Count Of Screening Candidates Before On-Boarding------------------------------
		rest().get("/getScreeningCandidatesCount").to("direct:getScreeningCandidatesCount");
		from("direct:getScreeningCandidatesCount").process(exchange -> {
			Long screeningCandidateCount = services.getCountOfScreeningCandidate();
			exchange.getMessage().setBody(screeningCandidateCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		 ------------------------35. Get Count Of Technical Round One Before On-Boarding------------------------------
		rest().get("/getTechnicalRoundOneCount").to("direct:getTechnicalRoundOneCount");
		from("direct:getTechnicalRoundOneCount").process(exchange -> {
			Long technicalRoundOneCandidateCount = services.getCountOfTechnicalRoundOne();
			exchange.getMessage().setBody(technicalRoundOneCandidateCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

// 		------------------------36. Get Count Of Technical Round Two Before On-Boarding------------------------------
		rest().get("/getTechnicalRoundTwoCount").to("direct:getTechnicalRoundTwoCount");
		from("direct:getTechnicalRoundTwoCount").process(exchange -> {
			Long technicalRoundTwoCandidateCount = services.getCountOfTechnicalRoundTwo();
			exchange.getMessage().setBody(technicalRoundTwoCandidateCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

// 		------------------------37. Get Count Of HR Round Before On-Boarding------------------------------
		rest().get("/getHRRoundCount").to("direct:getHRRoundCount");
		from("direct:getHRRoundCount").process(exchange -> {
			Long hrRoundCount = services.getCountOfHRRound();
			exchange.getMessage().setBody(hrRoundCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

// 		------------------------38. Get Count Of Selected Before On-Boarding------------------------------
		rest().get("/getSelectedCount").to("direct:getSelectedCount");
		from("direct:getSelectedCount").process(exchange -> {
			Long selectedCount = services.getCountOfSelected();
			exchange.getMessage().setBody(selectedCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		----------------------------------39. Get Job Details-----------------------------------

		rest().get("/getJobDetails").param().name("jobId").type(RestParamType.query).endParam()
				.to("direct:getJobDetails");
		from("direct:getJobDetails").log("jobId : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String jobId = exchange.getIn().getHeader("jobId", String.class);
				CreateJob jobById = services.getJobDetails(jobId);
				if (jobById != null) {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
					exchange.getMessage().setBody(jobById);
				} else {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
					exchange.getMessage().setBody("Job with ID " + jobId + " not found ");
				}
			}
		});

// 		-----------------------------40. verify team lead login---------------------------------

		rest().get("/verifyTeamLead").param().name("username").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:TeamLead");

		from("direct:TeamLead").process(exchange -> {
			String username = exchange.getIn().getHeader("username", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with username: {} and password: {}", username, password);
			HumanResource humanResource = services.getTLByUsernameAndPassword(username, password);
			if (humanResource != null) {
				exchange.getMessage().setBody(humanResource);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("User not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

//		 ----------------------- 41.register HR or TL-------------------
		rest().post("/registerHRrTL").type(HumanResource.class).to("direct:processAdmin");
		from("direct:processAdmin").log("User : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				HumanResource humanResource = exchange.getIn().getBody(HumanResource.class);
				if (services.humanResourceExists(humanResource)) {
					exchange.getMessage().setBody("User already exists for " + humanResource.getEmail() + " with "
							+ humanResource.getPassword() + " this employeeNumber");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					services.saveHRCredentials(humanResource);
					exchange.getMessage().setBody(humanResource.getEmail() + " your registration successful.");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
				}
			}
		}).end();

//		 -------------------42. Verify Human Resource HR Portal-----------------------------------

		rest().get("/verifyHumanResource").param().name("username").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:HR");

		from("direct:HR").process(exchange -> {
			String username = exchange.getIn().getHeader("username", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with username: {} and password: {}", username, password);
			HumanResource humanResource = services.getHRByUsernameAndPassword(username, password);
			if (humanResource != null) {
				exchange.getMessage().setBody(humanResource);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("User not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

//		--------------------------------43. Get Employee By ProjectName---------------------------

		rest().get("/listOfEmployeesForParticularProject").param().name("projectName").type(RestParamType.query)
				.endParam().to("direct:getListOfEmployeesInProject");
		from("direct:getListOfEmployeesInProject").process(exchange -> {
			String projectName = exchange.getIn().getHeader("projectName", String.class);
			List<Timesheet> employeeInfo = services.findByProjectname(projectName);
			exchange.getMessage().setBody(employeeInfo);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------44. Get Count Of Amway------------------------------

		rest().get("/getAmwayCount").to("direct:getAmwayCount");
		from("direct:getAmwayCount").process(exchange -> {
			Long amwayCount = services.getCountOfAmway();
			exchange.getMessage().setBody(amwayCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});
//		------------------------45. Get Count Of Aia------------------------------

		rest().get("/getAiaCount").to("direct:getAiaCount");
		from("direct:getAiaCount").process(exchange -> {
			Long aiaCount = services.getCountOfAia();
			exchange.getMessage().setBody(aiaCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------46. Get Count Of SOA------------------------------

		rest().get("/getSoaCount").to("direct:getSoaCount");
		from("direct:getSoaCount").process(exchange -> {
			Long soaCount = services.getCountOfSoa();
			exchange.getMessage().setBody(soaCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------47. Get Count Of Airflow------------------------------

		rest().get("/getAirflowCount").to("direct:getAirflowCount");
		from("direct:getAirflowCount").process(exchange -> {
			Long airflowCount = services.getCountOfAirflow();
			exchange.getMessage().setBody(airflowCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------48. Get Count Of Communication------------------------------

		rest().get("/getCommunicationCount").to("direct:getCommunicationCount");
		from("direct:getCommunicationCount").process(exchange -> {
			Long communicationCount = services.getCountOfCommunication();
			exchange.getMessage().setBody(communicationCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------49. Get Count Of Azure------------------------------

		rest().get("/getAzureCount").to("direct:getAzureCount");
		from("direct:getAzureCount").process(exchange -> {
			Long azureCount = services.getCountOfAzure();
			exchange.getMessage().setBody(azureCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------50. Get Count Of MQ------------------------------

		rest().get("/getMqCount").to("direct:getMqCount");
		from("direct:getMqCount").process(exchange -> {
			Long mqCount = services.getCountOfMq();
			exchange.getMessage().setBody(mqCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		------------------------51. Get Count Of Horizon------------------------------

		rest().get("/getHorizonCount").to("direct:getHorizonCount");
		from("direct:getHorizonCount").process(exchange -> {
			Long horizonCount = services.getCountOfHorizon();
			exchange.getMessage().setBody(horizonCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

// 		------------------------52. Get Count Of IEPod1------------------------------

		rest().get("/getIePod1Count").to("direct:getIePod1Count");
		from("direct:getIePod1Count").process(exchange -> {
			Long iePod1Count = services.getCountOfIePod1();
			exchange.getMessage().setBody(iePod1Count);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//		 ------------------------53. Get Count Of IEPod3------------------------------

		rest().get("/getIePod3Count").to("direct:getIePod3Count");
		from("direct:getIePod3Count").process(exchange -> {
			Long iePod3Count = services.getCountOfIePod3();
			exchange.getMessage().setBody(iePod3Count);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});

//				--------------------54. Refer a friend-------------------------------

		rest().post("/saveReferredCandidateInformation").type(ReferredCandidateInformation.class)
				.to("direct:referredCandidateInfo");
		from("direct:referredCandidateInfo").log("ReferredCandidateInfo : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				ReferredCandidateInformation referredCandidate = exchange.getIn()
						.getBody(ReferredCandidateInformation.class);
				if (services.referredCandidateInfoExists(referredCandidate)) {
					exchange.getMessage().setBody(referredCandidate.getFullName()
							+ " Already referred for the position " + referredCandidate.getPosition());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					services.saveReferredCandidateInfo(referredCandidate);
					exchange.getMessage()
							.setBody(referredCandidate.getFullName() + "Received the information successfully");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
				}
			}
		});

//				--------------------55. Get Referred Candidates List-----------------------------

		rest().get("/listOfReferredCandidates").to("direct:referredCandidateList");
		from("direct:referredCandidateList").log("ReferredCandidatesList : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Iterable<ReferredCandidateInformation> referredCandidateInfo = services.getListOfReferredCandidates();
				exchange.getMessage().setBody(referredCandidateInfo);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		});

//				------------------------56. Get Count of Referred Candidates--------------------

		rest().get("/referredCandidateCount").to("direct:refCount");
		from("direct:refCount").process(exchange -> {
			Long refCandidateCount = services.countOfReferredCandidate();
			exchange.getMessage().setBody(refCandidateCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		});
		
//		------------------57. remainder for pending employees-----------------
		 
		rest().get("/reminder").param().name("email").type(RestParamType.query).endParam().to("direct:sendReminder");
 
		from("direct:sendReminder").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			EmployeeTimesheet empInfo = services.findByEmployeeEmail(email);
			if (empInfo != null) {
				String subject = services.subjectForTimesheetReminder();
				String emailBody = services.getReminderEmailBody(empInfo);
				if (emailBody != null) {
					exchange.getMessage().setHeader("emailSubject", subject);
					exchange.getMessage().setBody(emailBody);
					exchange.getMessage().setHeader("recipientEmail", empInfo.getEmail());
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
					ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
					producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
							exchange.getMessage().getHeaders());
				} else {
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
				}
			} else {
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});
		
//		----------------------------58. Reset Password for Employee Timesheet-----------------------------------------------
		 
				rest().get("/resetPassword").param().name("email").type(RestParamType.query).endParam().param().name("password")
						.type(RestParamType.query).endParam().to("direct:processResetPassword");
				from("direct:processResetPassword").process(exchange -> {
					String email = exchange.getIn().getHeader("email", String.class);
					String password = exchange.getIn().getHeader("password", String.class);
					RegisterEmployee registerEmployee = services.findEmployeeByEmail(email);
					if (services.employeeExists(registerEmployee)) {
						registerEmployee.setPassword(password);
						services.saveEmployee(registerEmployee);
						if (!services.isValidPassword(registerEmployee.getPassword())) {
							exchange.getMessage().setBody(
									"Password should have at least one lowercase, one uppercase, one digit, and one special character & minimum length 8");
							return;
						} else {
							exchange.getMessage().setBody(registerEmployee);
							exchange.getMessage().setBody("Password updated succussfully");
							exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
						}
					} else {
						exchange.getMessage().setBody("Please provide valid emailid");
						exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
					}
				});
				
//				------------------59. remainder with month and year for pending employees-----------------
				 
				rest().get("/reminderWithMonthYear").param().name("email").type(RestParamType.query).endParam().param().name("month")
						.type(RestParamType.query).endParam().param().name("year").type(RestParamType.query).endParam()
						.to("direct:sendingReminder");
				from("direct:sendingReminder").process(exchange -> {
					String email = exchange.getIn().getHeader("email", String.class);
					String month = exchange.getIn().getHeader("month", String.class);
					Integer year = exchange.getIn().getHeader("year", Integer.class);
					EmployeeTimesheet empInfo = services.findByEmpEmail(email);
					if (empInfo != null && year != null) {
						String subject = services.subjectForTimesheetReminder(month, year);
						String emailBody = services.getReminderEmailBody(empInfo);
						if (subject != null && emailBody != null) {
							exchange.getMessage().setHeader("emailSubject", subject);
							exchange.getMessage().setBody(emailBody);
							exchange.getMessage().setHeader("recipientEmail", empInfo.getEmail());
							exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
							exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
							ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
							producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
									exchange.getMessage().getHeaders());
						} else {
							exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
							exchange.getMessage().setBody("Missing data to generate reminder email.");
						}
					} else {
						exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
						exchange.getMessage().setBody("Employee information or year not found for email: " + email);
					}
				});
	}
}
