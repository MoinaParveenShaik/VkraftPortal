package com.vkraftportal.controller;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.web.bind.annotation.RestController;

import com.vkraftportal.model.AppliedCandidateInformation;
import com.vkraftportal.model.CreateJob;
import com.vkraftportal.model.HumanResource;
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

// ------------------------------------------------Register Employee----------------------------------------------------------------

		rest("/registerEmployee").post().type(RegisterEmployee.class).to("direct:processRegisterEmployee");
		from("direct:processRegisterEmployee").log("RegisterEmployee : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				RegisterEmployee employee = exchange.getIn().getBody(RegisterEmployee.class);
				boolean employeeExists = services.employeeExists(employee);

				if (employeeExists) {
					exchange.getMessage().setBody("User already exists for " + employee.getEmployeeNumber());
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					String randomPassword = services.generateAndSetRandomPassword();
					System.out.println(randomPassword + " generated");
					employee.setPassword(randomPassword);

					services.saveEmployee(employee);
					System.out.println("data " + employee);
					System.out.println("Hello");

					String recipientEmail = employee.getEmail();
					String generatedPassword = randomPassword;
					String emailBody = services.getEmailBody(recipientEmail, generatedPassword,
							employee.getEmployeeName());

					exchange.getMessage().setBody(emailBody);
					exchange.getMessage().setHeader("recipientEmail", recipientEmail);
					exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/plain");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);

					ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate();
					producerTemplate.sendBodyAndHeaders("direct:sendMail", exchange.getMessage().getBody(),
							exchange.getMessage().getHeaders());

				}
			}
		});

		from("direct:sendMail").setHeader("Subject", constant("Your Account Information for Timesheet portal"))
				.process(exchange -> {
					String recipientEmail = exchange.getMessage().getHeader("recipientEmail", String.class);
					String emailBody = exchange.getIn().getBody(String.class); // Get the email body from the message
																				// body
					System.out.println(emailBody);
					System.out.println("Recipient Email: " + recipientEmail); // Log recipient email

					exchange.getMessage().setBody(emailBody);
				}).recipientList(simple(
						"smtps://smtp.gmail.com:465?username=babasahebudamle1007@gmail.com&password=qgge nnbr xjvj tqmn&to=${header.recipientEmail}"));

// ----------------------------------------------------Save Timesheet---------------------------------------------------

		rest().post("/saveTimesheet").type(Timesheet.class).to("direct:processTimesheet");
		from("direct:processTimesheet").log("Timesheet : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				Timesheet timesheetDataToSave = exchange.getIn().getBody(Timesheet.class);
				if (services.timesheetExists(timesheetDataToSave)) {
					exchange.getMessage()
							.setBody("Timesheet already exists for " + timesheetDataToSave.getMonth() + " month");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					timesheetDataToSave.setStatus("pending");
					services.saveTimesheet(timesheetDataToSave);
					System.out.println(timesheetDataToSave);
					exchange.getMessage()
							.setBody("Timesheet is saved for " + timesheetDataToSave.getMonth() + " month");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
				}
			}
		}).end();

// ------------------------------------------------------Update Timesheets---------------------------------------------------------

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
			String holidaysInput = exchange.getIn().getHeader("holidaysInput", String.class);

			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);

			Timesheet existingEntity = services.findByEmployeeNumberAndMonthAndYear(employeeNumber, month, year);
			if (existingEntity != null) {

				if (clientName != null) {
					existingEntity.setClientName(clientName);
				}
				if (assignmentName != null) {
					existingEntity.setAssignmentName(assignmentName);
				}
				if (holidaysInput != null) {
					existingEntity.setHolidaysInput(holidaysInput);
				}

				services.saveTimesheet(existingEntity);
				exchange.getMessage().setBody(existingEntity);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

			} else {
				exchange.getMessage().setBody("Error: TimesheetEntity with number '" + employeeNumber + "' not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}

		});

// ---------------------------------------------------------- Delete Timesheet------------------------------------------------------

		rest().delete("/deleteEmployee").param().name("employeeNumber").type(RestParamType.query).endParam().param()
				.name("month").type(RestParamType.query).endParam().param().name("year").type(RestParamType.query)
				.endParam().to("direct:delete");
		from("direct:delete").process(exchange -> {
			String employeeNumber = exchange.getIn().getHeader("employeeNumber", String.class);
			String month = exchange.getIn().getHeader("month", String.class);
			String year = exchange.getIn().getHeader("year", String.class);
			System.out.println(employeeNumber + month + year);
			boolean timesheet = services.deleteByEmployeeNumberAndDate(employeeNumber, month, year);
			System.out.println(timesheet);
			if (timesheet) {
				System.out.println("valid");
				exchange.getMessage().setBody("Status: Record for Employee " + employeeNumber + " for Month " + month
						+ " and Year " + year + " Deleted");
			} else {
				exchange.getMessage().setBody("Error: Record not found for Employee " + employeeNumber + " for Month "
						+ month + " and Year " + year);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}

		});

// ---------------------------------------------------------Get Timesheet---------------------------------------------------------

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

// -----------------------------------------------Get All Registered Employees----------------------------------------------------

		rest().get("/getAllEmployees").to("direct:getTotalEmployees");

		from("direct:getTotalEmployees").log("Get All Employees request received").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {

				Iterable<RegisterEmployee> allEmployees = services.getAllEmployees();
				System.out.println(allEmployees);
				exchange.getMessage().setBody(allEmployees);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			}
		}).end();

// -----------------------------------------------------Verify Employee----------------------------------------------------------------

		rest("/verifyEmployee").get().param().name("username").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:employee");
		from("direct:employee").process(exchange -> {
			String username = exchange.getIn().getHeader("username", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with username: {} and password: {}", username, password);
			RegisterEmployee employee = services.getEmployeeByUsernameAndPassword(username, password);
			boolean isUserValid = services.verifyEmployee(employee);
			if (isUserValid) {
				exchange.getMessage().setBody(employee);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("User not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

// --------------------------------------------------GetApprovedTimesheets------------------------------------------------------------

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

// ----------------------------------------------------------RegisterHumanResource-------------------------------------------------------------------

		rest().post("/registerHumanResource").type(HumanResource.class).to("direct:processAdmin");
		from("direct:processAdmin").log("User : ${body}").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				HumanResource humanResource = exchange.getIn().getBody(HumanResource.class);
				if (services.humanResourceExists(humanResource)) {
					exchange.getMessage().setBody("User already exists for " + humanResource.getEmployeeName()
							+ " with " + humanResource.getEmployeeNumber() + " this employeeNumber");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
				} else {
					services.saveHRCredentials(humanResource);
					exchange.getMessage().setBody(humanResource.getEmployeeName() + " your registration successful.");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
				}
			}
		}).end();

// ---------------------------------------------------------Get Pending Employees-----------------------------------------------------------

		rest().get("/pendingEmployees").to("direct:getPendingEmployees");
		from("direct:getPendingEmployees").process(exchange -> {
			List<Timesheet> pendingEmployees = services.getEmployeesByStatus("pending");

			if (pendingEmployees != null && !pendingEmployees.isEmpty()) {
				exchange.getMessage().setBody(pendingEmployees);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("No employees found with status 'pending'");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			}
		});

// -----------------------------------------------------------------VerifyHumanResource-------------------------------------------------------------

		rest("/VerifyHumanResource").post().param().name("username").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:HumanResource");
		from("direct:HumanResource").process(exchange -> {
			String username = exchange.getIn().getHeader("username", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			log.info("Received request with username: {} and password: {}", username, password);
			HumanResource humanResource = services.getHRByUsernameAndPassword(username, password);
			boolean isUserValid = services.verifyHR(humanResource);
			if (isUserValid) {
				exchange.getMessage().setBody(humanResource);
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			} else {
				exchange.getMessage().setBody("User not found");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
		});

// --------------------------------------------------------------Get Employee Count-------------------------------------------------------------

		rest("/getEmployeeCount").get().to("direct:employee");
		from("direct:employee").process(exchange -> {
			Long employeeCount = services.getCountOfEmployee();
			System.out.println(employeeCount);
			exchange.getMessage().setBody(employeeCount);
			exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

		});

// -------------------------------------------------------------------RegisterCandidate--------------------------------------------------------

		rest("/registerCandidate").post().type(RegisterCandidate.class).to("direct:processRegisterCandidate");
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
					System.out.println(randomPassword + " generated");
					candidate.setPassword(randomPassword);

					services.saveCandidate(candidate);
					System.out.println("saved");
					System.out.println("data " + candidate);

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

					System.out.println(emailBody);
					System.out.println("Recipient Email: " + recipientEmail);

					exchange.getMessage().setBody(emailBody);
				}).recipientList(simple(
						"smtps://smtp.gmail.com:465?username=babasahebudamle1007@gmail.com&password=qgge nnbr xjvj tqmn&to=${header.recipientEmail}"));

// ----------------------------------------------------------Candidate Login------------------------------------------------------------

		rest("/verifyCandidate").get().param().name("email").type(RestParamType.query).endParam().param()
				.name("password").type(RestParamType.query).endParam().to("direct:Candidate");
		from("direct:Candidate").process(exchange -> {
			String email = exchange.getIn().getHeader("email", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			System.out.println(email + "   " + password);
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

//-------------------------------------------------------------Save Applied Candidate Information-------------------------------------------------

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
					String resumePath = appliedCandidateInfo.getResume();
					String resumeBase64 = services.convertToBase64(resumePath);
					appliedCandidateInfo.setStatus("applied");
					appliedCandidateInfo.setResume(resumeBase64);
					services.saveAppliedCandidateInfo(appliedCandidateInfo);
					exchange.getMessage()
							.setBody(appliedCandidateInfo.getFullName() + " succesfully applied for this position");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);

				}
			}
		}).end();
		
		
//		-----------------------------------------Login Before On-Boarding---------------------------------------------------------------------------
		
		rest("/verifyLogin").get().param().name("email").type(RestParamType.query).endParam().param()
		.name("password").type(RestParamType.query).endParam().to("direct:processLogin");
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
		
//		---------------------------------------------------Forgot Password--------------------------------------------------------------------------
		
		rest("/forgetPassword").put().param().name("email").type(RestParamType.query).endParam().param()
		.name("password").type(RestParamType.query).endParam().to("direct:processPassword");
		from("direct:processPassword").process(exchange ->{
			String email = exchange.getIn().getHeader("email", String.class);
			String password = exchange.getIn().getHeader("password", String.class);
			RegisterCandidate registerCandidate = services.findCandidateByEmail(email);
 
 
			if (services.candidateExists(registerCandidate)) {
				registerCandidate.setPassword(password);
				services.saveCandidate(registerCandidate);
				if (!services.isValidPassword(registerCandidate.getPassword())) {
					exchange.getMessage().setBody("Password should have at least one lowercase, one uppercase, one digit, and one special character & minimum length 8");
					return;
				}
				else {
					exchange.getMessage().setBody(registerCandidate);
					exchange.getMessage().setBody("Password updated succussfully");
					exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);}
			}
			else {
				exchange.getMessage().setBody("Please provide valid emailid");
				exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
			}
 
		});
		
//		------------------------------------------------------Register Job---------------------------------------------------------------------------
		
		rest("/registerJob").post().type(CreateJob.class).to("direct:saveJob");
		from("direct:saveJob").log("Job : ${body}").process(new Processor() {
 
			@Override
			public void process(Exchange exchange) throws Exception {
				CreateJob body = exchange.getIn().getBody(CreateJob.class);
				CreateJob jobDetails = services.getJobDetails(body.getJobId());
				System.out.println(jobDetails);
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

	}
}
