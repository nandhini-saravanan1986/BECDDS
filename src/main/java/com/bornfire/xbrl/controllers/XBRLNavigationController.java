package com.bornfire.xbrl.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.config.PasswordEncryption;
import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.AccessAndRoles;
import com.bornfire.xbrl.entities.AccessandRolesRepository;
import com.bornfire.xbrl.entities.AlertEntity;
import com.bornfire.xbrl.entities.AlertManagementEntity;
import com.bornfire.xbrl.entities.AlertManagementRepository;
import com.bornfire.xbrl.entities.AlertRep;
import com.bornfire.xbrl.entities.EcddCorporateEntity;
import com.bornfire.xbrl.entities.EcddCustomerDocumentsEntity;
import com.bornfire.xbrl.entities.KYC_Audit_Rep;
import com.bornfire.xbrl.entities.Kyc_Corprate_Repo;
import com.bornfire.xbrl.entities.Kyc_Repo;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.XBRLReportsMasterRep;
import com.bornfire.xbrl.entities.BRBS.AuditTablePojo;
import com.bornfire.xbrl.entities.BRBS.BRFValidationsRepo;
import com.bornfire.xbrl.entities.BRBS.Charge_Back_Rep;
import com.bornfire.xbrl.entities.BRBS.EcddIndividualProfileRepository;
import com.bornfire.xbrl.entities.BRBS.Ecdd_Individual_Profile_Entity;
import com.bornfire.xbrl.entities.BRBS.Ecdd_customer_transaction;
import com.bornfire.xbrl.entities.BRBS.Ecdd_customer_transaction_repo;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Audit_Rep;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Service_Entity;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Service_Rep;
import com.bornfire.xbrl.services.AccessAndRolesServices;
import com.bornfire.xbrl.services.AlertManagementServices;
import com.bornfire.xbrl.services.EcddUploadDocumentService;
import com.bornfire.xbrl.services.IndividualPdfService;
import com.bornfire.xbrl.services.Kyc_individual_service;
import com.bornfire.xbrl.services.LoginServices;

import net.sf.jasperreports.engine.JRException;

@Controller
@ConfigurationProperties("default")
public class XBRLNavigationController {

	private static final Logger logger = LoggerFactory.getLogger(XBRLNavigationController.class);
	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	LoginServices loginServices;

	@Autowired
	Kyc_individual_service kyc_individual_service;

	@Autowired
	EcddIndividualProfileRepository ecddIndividualProfileRepository;

	@Autowired
	XBRLReportsMasterRep XBRLReportsMasterReps;

	@Autowired
	AlertRep alertRep;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	BRFValidationsRepo brfValidationsRepo;

	@Autowired
	private AlertManagementRepository alertmanagementrepository;

	@Autowired
	AlertManagementServices alertservices;

	@Autowired
	com.bornfire.xbrl.entities.BRBS.AUD_SERVICE_REPO AUD_SERVICE_REPO;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	MANUAL_Audit_Rep mANUAL_Audit_Rep;

	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;

	@Autowired
	Charge_Back_Rep charge_Back_Rep;

	@Autowired
	Ecdd_customer_transaction_repo Ecdd_customer_transaction_repo;

	@Autowired
	AccessAndRolesServices AccessRoleService;

	@Autowired
	AccessandRolesRepository accessandrolesrepository;

	private String auditRefNo;

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	@RequestMapping("/custom-error")
	public String handleError(HttpServletRequest request, Model model) {
		Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

		// Ignore Thymeleaf exceptions by returning a simple message or redirecting
		// elsewhere
		if (exception != null) {
			if (exception instanceof org.thymeleaf.exceptions.TemplateInputException
					|| exception instanceof org.thymeleaf.exceptions.TemplateProcessingException) {
				// For example: return a simple page or ignore it silently
				model.addAttribute("status", statusCode);
				model.addAttribute("message", "A template processing error occurred.");
				return "simple-error"; // Or any other simple error page without details
			}
		}

		model.addAttribute("status", statusCode);
		model.addAttribute("message", errorMessage);

		return "error"; // Your normal error.html template
	}

	@GetMapping("/systemotp")
	public String showOtpForm() {
		return "XBRLOtpvalidation.html"; // Thymeleaf or HTML page
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") String userOtp, HttpSession session) {
		String actualOtp = (String) session.getAttribute("otp");
		if (actualOtp != null && actualOtp.equals(userOtp)) {
			session.removeAttribute("otp"); // Clear OTP after success
			return "redirect:/Dashboard";
		}
		return "redirect:login?invalidotp";
	}

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String getdashboard(Model md, HttpServletRequest req) {

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		String userid = (String) req.getSession().getAttribute("USERID");
		String Dashboardpage = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		md.addAttribute("menu", "Dashboard");
		md.addAttribute("checkpassExpiry", loginServices.checkpassexpirty(userid));
		md.addAttribute("checkAcctExpiry", loginServices.checkAcctexpirty(userid));
		md.addAttribute("changepassword", loginServices.checkPasswordChangeReq(userid));

		if (Dashboardpage.equals("DCD_ADMIN") || Dashboardpage.equals("DCD_BRANCH")) {
			int Completed = 0;
			int Pending = 0;
			int Under_review = 0;

			int CorpCompleted = 0;
			int CorpPending = 0;
			int CorpUnder_review = 0;

			int corpbarcompleted = 0;
			int corpbarPending = 0;
			int corpbarincomplete = 0;

			/// Counts fetched for Dashborad page Pending kyc INDIVIDUAL details branch wise
			BigDecimal DubaiPendIndividuals = new BigDecimal("0");
			BigDecimal AbudhabiPendIndividuals = new BigDecimal("0");
			BigDecimal DeiraPendIndividuals = new BigDecimal("0");
			BigDecimal SharjhaPendIndividuals = new BigDecimal("0");
			BigDecimal RasalkhaimaPendIndividuals = new BigDecimal("0");
			BigDecimal SyndPendIndividuals = new BigDecimal("0");

			BigDecimal DubaiPendCorporate = new BigDecimal("0");
			BigDecimal AbudhabiPendCorporate = new BigDecimal("0");
			BigDecimal DeiraPendCorporate = new BigDecimal("0");
			BigDecimal SharjhaPendCorporate = new BigDecimal("0");
			BigDecimal RasalkhaimaPendCorporate = new BigDecimal("0");
			BigDecimal SyndPendCorporate = new BigDecimal("0");
			if (Dashboardpage.equals("DCD_ADMIN")) {
				Completed = ecddIndividualProfileRepository.Getcompletedcount();
				Under_review = ecddIndividualProfileRepository.GetIncompletedcount();
				Pending = ecddIndividualProfileRepository.GetPendingcount();

				CorpCompleted = kyc_corporate_repo.Getcompletedcount();
				CorpUnder_review = kyc_corporate_repo.GetIncompletedcount();
				CorpPending = kyc_corporate_repo.GetPendingcount();
			} else {
				Completed = ecddIndividualProfileRepository.Getbranchwisecompletedcount(BRANCHCODE);
				Under_review = ecddIndividualProfileRepository.GetbranchwiseIncompletedcount(BRANCHCODE);
				Pending = ecddIndividualProfileRepository.GetbranchwisePendingcount(BRANCHCODE);

				CorpCompleted = kyc_corporate_repo.Getbranchwisecompletedcount(BRANCHCODE);
				CorpUnder_review = kyc_corporate_repo.GetbranchwiseIncompletedcount(BRANCHCODE);
				CorpPending = kyc_corporate_repo.GetbranchwisePendingcount(BRANCHCODE);
			}

			corpbarcompleted = CorpCompleted;
			corpbarPending = CorpPending;
			corpbarincomplete = CorpUnder_review;

			List<Object[]> branchwiseIndividual = ecddIndividualProfileRepository.GetbranchPendingcount();

			for (int i = 0; i < branchwiseIndividual.size(); i++) {

				if (branchwiseIndividual.get(i)[0].toString() != null) {
					if (branchwiseIndividual.get(i)[0].toString().equals("9001")) {
						DubaiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9002")) {
						AbudhabiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9003")) {
						DeiraPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9004")) {
						SharjhaPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9008")) {
						SyndPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
				}

			}

			List<Object[]> branchwiseCorporate = kyc_corporate_repo.GetbranchPendingcount();
			for (int i = 0; i < branchwiseCorporate.size(); i++) {

				if (branchwiseCorporate.get(i)[0].toString() != null) {
					if (branchwiseCorporate.get(i)[0].toString().equals("9001")) {
						DubaiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9002")) {
						AbudhabiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9003")) {
						DeiraPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9004")) {
						SharjhaPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9008")) {
						SyndPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
				}

			}
			// Individuals Attribue
			md.addAttribute("DubaiPendIndividuals", DubaiPendIndividuals);
			md.addAttribute("AbudhabiPendIndividuals", AbudhabiPendIndividuals);
			md.addAttribute("DeiraPendIndividuals", DeiraPendIndividuals);
			md.addAttribute("SharjhaPendIndividuals", SharjhaPendIndividuals);
			md.addAttribute("RasalkhaimaPendIndividuals", RasalkhaimaPendIndividuals);
			md.addAttribute("SyndPendIndividuals", SyndPendIndividuals);

			// Corporate Attribute
			md.addAttribute("DubaiPendCorporate", DubaiPendCorporate);
			md.addAttribute("AbudhabiPendCorporate", AbudhabiPendCorporate);
			md.addAttribute("DeiraPendCorporate", DeiraPendCorporate);
			md.addAttribute("SharjhaPendCorporate", SharjhaPendCorporate);
			md.addAttribute("RasalkhaimaPendCorporate", RasalkhaimaPendCorporate);
			md.addAttribute("SyndPendCorporate", SyndPendCorporate);

			md.addAttribute("completed", Completed);
			md.addAttribute("Pending", Pending);
			md.addAttribute("Under_review", Under_review);
			md.addAttribute("Dashboardpage", Dashboardpage);
			md.addAttribute("corpbarcompleted", corpbarcompleted);
			md.addAttribute("corpbarPending", corpbarPending);
			md.addAttribute("corpbarincomplete", corpbarincomplete);

			md.addAttribute("Branch_code", BRANCHCODE);

			System.out.println(Dashboardpage);
		}
		md.addAttribute("menu", "Dashboard");
		return "XBRLDashboard";
	}

	@RequestMapping(value = "Dashboard", method = { RequestMethod.GET, RequestMethod.POST })
	public String dashboard(@RequestParam(name = "frequency", required = false) String frequency, Model md,
			HttpServletRequest req) {

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		String userid = (String) req.getSession().getAttribute("USERID");
		String Dashboardpage = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		System.out.println("Dashboard page is:" + Dashboardpage);
		System.out.println("Branchcode is : " + BRANCHCODE);
		md.addAttribute("menu", "Dashboard");
		md.addAttribute("checkpassExpiry", loginServices.checkpassexpirty(userid));
		md.addAttribute("checkAcctExpiry", loginServices.checkAcctexpirty(userid));
		md.addAttribute("changepassword", loginServices.checkPasswordChangeReq(userid));

		if (Dashboardpage.equals("DCD_ADMIN") || Dashboardpage.equals("DCD_BRANCH")) {
			int Completed = 0;
			int Pending = 0;
			int Under_review = 0;

			int CorpCompleted = 0;
			int CorpPending = 0;
			int CorpUnder_review = 0;

			int corpbarcompleted = 0;
			int corpbarPending = 0;
			int corpbarincomplete = 0;

			/// Counts fetched for Dashborad page Pending kyc INDIVIDUAL details branch wise
			BigDecimal DubaiPendIndividuals = new BigDecimal("0");
			BigDecimal AbudhabiPendIndividuals = new BigDecimal("0");
			BigDecimal DeiraPendIndividuals = new BigDecimal("0");
			BigDecimal SharjhaPendIndividuals = new BigDecimal("0");
			BigDecimal RasalkhaimaPendIndividuals = new BigDecimal("0");
			BigDecimal SyndPendIndividuals = new BigDecimal("0");

			BigDecimal DubaiPendCorporate = new BigDecimal("0");
			BigDecimal AbudhabiPendCorporate = new BigDecimal("0");
			BigDecimal DeiraPendCorporate = new BigDecimal("0");
			BigDecimal SharjhaPendCorporate = new BigDecimal("0");
			BigDecimal RasalkhaimaPendCorporate = new BigDecimal("0");
			BigDecimal SyndPendCorporate = new BigDecimal("0");
			if (Dashboardpage.equals("DCD_ADMIN")) {
				Completed = ecddIndividualProfileRepository.Getcompletedcount();
				Under_review = ecddIndividualProfileRepository.GetIncompletedcount();
				Pending = ecddIndividualProfileRepository.GetPendingcount();

				CorpCompleted = kyc_corporate_repo.Getcompletedcount();
				CorpUnder_review = kyc_corporate_repo.GetIncompletedcount();
				CorpPending = kyc_corporate_repo.GetPendingcount();
			} else {
				Completed = ecddIndividualProfileRepository.Getbranchwisecompletedcount(BRANCHCODE);
				Under_review = ecddIndividualProfileRepository.GetbranchwiseIncompletedcount(BRANCHCODE);
				Pending = ecddIndividualProfileRepository.GetbranchwisePendingcount(BRANCHCODE);

				CorpCompleted = kyc_corporate_repo.Getbranchwisecompletedcount(BRANCHCODE);
				CorpUnder_review = kyc_corporate_repo.GetbranchwiseIncompletedcount(BRANCHCODE);
				CorpPending = kyc_corporate_repo.GetbranchwisePendingcount(BRANCHCODE);
			}

			corpbarcompleted = CorpCompleted;
			corpbarPending = CorpPending;
			corpbarincomplete = CorpUnder_review;

			List<Object[]> branchwiseIndividual = ecddIndividualProfileRepository.GetbranchPendingcount();

			for (int i = 0; i < branchwiseIndividual.size(); i++) {

				if (branchwiseIndividual.get(i)[0].toString() != null) {
					if (branchwiseIndividual.get(i)[0].toString().equals("9001")) {
						DubaiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9002")) {
						AbudhabiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9003")) {
						DeiraPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9004")) {
						SharjhaPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9008")) {
						SyndPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
				}

			}

			List<Object[]> branchwiseCorporate = kyc_corporate_repo.GetbranchPendingcount();
			for (int i = 0; i < branchwiseCorporate.size(); i++) {

				if (branchwiseCorporate.get(i)[0].toString() != null) {
					if (branchwiseCorporate.get(i)[0].toString().equals("9001")) {
						DubaiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9002")) {
						AbudhabiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9003")) {
						DeiraPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9004")) {
						SharjhaPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9008")) {
						SyndPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
				}

			}
			// Individuals Attribue
			md.addAttribute("DubaiPendIndividuals", DubaiPendIndividuals);
			md.addAttribute("AbudhabiPendIndividuals", AbudhabiPendIndividuals);
			md.addAttribute("DeiraPendIndividuals", DeiraPendIndividuals);
			md.addAttribute("SharjhaPendIndividuals", SharjhaPendIndividuals);
			md.addAttribute("RasalkhaimaPendIndividuals", RasalkhaimaPendIndividuals);
			md.addAttribute("SyndPendIndividuals", SyndPendIndividuals);

			// Corporate Attribute
			md.addAttribute("DubaiPendCorporate", DubaiPendCorporate);
			md.addAttribute("AbudhabiPendCorporate", AbudhabiPendCorporate);
			md.addAttribute("DeiraPendCorporate", DeiraPendCorporate);
			md.addAttribute("SharjhaPendCorporate", SharjhaPendCorporate);
			md.addAttribute("RasalkhaimaPendCorporate", RasalkhaimaPendCorporate);
			md.addAttribute("SyndPendCorporate", SyndPendCorporate);

			md.addAttribute("completed", Completed);
			md.addAttribute("Pending", Pending);
			md.addAttribute("Under_review", Under_review);
			md.addAttribute("Dashboardpage", Dashboardpage);
			md.addAttribute("corpbarcompleted", corpbarcompleted);
			md.addAttribute("corpbarPending", corpbarPending);
			md.addAttribute("corpbarincomplete", corpbarincomplete);

			md.addAttribute("Branch_code", BRANCHCODE);

			System.out.println(Dashboardpage);
		}

		md.addAttribute("menu", "Dashboard");
		return "XBRLDashboard";
	}

	@RequestMapping(value = "AccessandRoles", method = { RequestMethod.GET, RequestMethod.POST })
	public String IPSAccessandRoles(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menu", "ACCESS AND ROLES");
			md.addAttribute("menuname", "ACCESS AND ROLES");
			md.addAttribute("formmode", "list");
			md.addAttribute("AccessandRoles", accessandrolesrepository.rulelist());
		} else if (formmode.equals("add")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - ADD");
			md.addAttribute("formmode", "add");
		} else if (formmode.equals("edit")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - EDIT");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		} else if (formmode.equals("view")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - INQUIRY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));

		} else if (formmode.equals("verify")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - VERIFY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));

		} else if (formmode.equals("delete")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - DELETE");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		}

		md.addAttribute("adminflag", "adminflag");
		md.addAttribute("userprofileflag", "userprofileflag");

		return "AccessandRoles";
	}

	@RequestMapping(value = "createAccessRole", method = RequestMethod.POST)
	@ResponseBody
	public String createAccessRoleEn(@RequestParam("formmode") String formmode,
			@RequestParam(value = "adminValue", required = false) String adminValue,
			@RequestParam(value = "BRF_ReportsValue", required = false) String BRF_ReportsValue,
			@RequestParam(value = "Basel_ReportsValue", required = false) String Basel_ReportsValue,
			@RequestParam(value = "ArchivalValue", required = false) String ArchivalValue,
			@RequestParam(value = "Audit_InquiriesValue", required = false) String Audit_InquiriesValue,
			@RequestParam(value = "RBR_ReportsValue", required = false) String RBR_ReportsValue,
			@RequestParam(value = "VAT_LedgerValue", required = false) String VAT_LedgerValue,
			@RequestParam(value = "Invoice_DataValue", required = false) String Invoice_DataValue,
			@RequestParam(value = "ReconciliationValue", required = false) String ReconciliationValue,
			@RequestParam(value = "finalString", required = false) String finalString,

			@ModelAttribute AccessAndRoles alertparam, Model md, HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		String msg = AccessRoleService.addPARAMETER(alertparam, formmode, adminValue, BRF_ReportsValue,
				Basel_ReportsValue, ArchivalValue, Audit_InquiriesValue, RBR_ReportsValue, ReconciliationValue,
				VAT_LedgerValue, Invoice_DataValue, finalString, userid);

		return msg;

	}

	@RequestMapping(value = "resetPassword1", method = { RequestMethod.GET, RequestMethod.POST })
	public String showResetPasswordPage(Model md, HttpServletRequest req) {
		String Passworduser = (String) req.getSession().getAttribute("USERID");
		String Passwordresest = (String) req.getSession().getAttribute("PASSWORDERROR");

		md.addAttribute("Resetuserid", Passworduser);
		md.addAttribute("Resetreason", Passwordresest);
		return "XBRLresetPassword"; // Name of the HTML file (resetPassword.html)
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam String userid, @RequestParam String newPassword)
			throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		Optional<UserProfile> userOptional = userProfileRep.findById(userid);
		String encryptedPassword = PasswordEncryption.getEncryptedPassword(newPassword);
		if (userOptional.isPresent()) {
			UserProfile user = userOptional.get();
			user.setPassword(encryptedPassword); // Encrypt the new password
			String localdateval = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			LocalDate date = LocalDate.parse(localdateval);
			BigDecimal passexpdays = new BigDecimal(user.getPass_exp_days());
			LocalDate date2 = date.plusDays(passexpdays.intValue());
			user.setLog_in_count("1");
			user.setNo_of_attmp(0);
			user.setUser_status("Active");
			user.setUser_status("Active");
			user.setDisable_flg("N");
			user.setUser_locked_flg("N");
			user.setPass_exp_date(new SimpleDateFormat("yyyy-MM-dd").parse(date2.toString()));// Reset the flag
			userProfileRep.save(user);
			return "redirect:login?resetSuccess";
		}

		return "redirect:resetPassword1?error=User not found";
	}

	@GetMapping("/getRoleDetails")
	@ResponseBody
	public AccessAndRoles getRoleDetails(@RequestParam String roleId) {
		System.out.println("role id for fetching is : " + roleId);
		return accessandrolesrepository.findById(roleId).orElse(null);
	}

	@RequestMapping(value = "Userlog", method = RequestMethod.GET)
	public String userlog(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("USERLOG", "M4", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		md.addAttribute("menu", "Userlog");
		md.addAttribute("userlog", loginServices.getUserLog(
				Date.from(localDateTime.plusDays(-5).atZone(ZoneId.systemDefault()).toInstant()), new Date()));

		return "XBRLUserLogs";
	}

	@RequestMapping(value = "Finuserdata", method = RequestMethod.GET)
	public ModelAndView Finuserdata(@RequestParam String userid) {
		ModelAndView mv = new ModelAndView("XBRLUserprofile::finuserapply");
		mv.addObject("formmode", "add");

		mv.addObject("userProfile", loginServices.getFinUser(userid));
		return mv;

	}

	@RequestMapping(value = "createUser", method = RequestMethod.POST)
	@ResponseBody
	public String createUser(@RequestParam("formmode") String formmode, @ModelAttribute UserProfile userprofile,
			Model md, HttpServletRequest rq) {
		String MOB = (String) rq.getSession().getAttribute("MOBILENUMBER");
		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		String msg = loginServices.addUser(userprofile, formmode, userid, username, MOB, ROLE);

		return msg;

	}

	@RequestMapping(value = "deleteuser", method = RequestMethod.POST)
	@ResponseBody
	public String deleteuser(@RequestParam("formmode") String userid, Model md, HttpServletRequest rq) {

		String msg = loginServices.deleteuser(userid);

		return msg;

	}

	@RequestMapping(value = "createAlter", method = RequestMethod.POST)
	@ResponseBody
	public String createAlter(@RequestParam("formmode") String formmode, @RequestParam("report_srl") String report_srl,
			@ModelAttribute AlertEntity alertEntity, Model md, HttpServletRequest rq) {
		String MOB = (String) rq.getSession().getAttribute("MOBILENUMBER");
		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		System.out.println(formmode);
		System.out.println(report_srl);
		String[] a = report_srl.split(",");
		System.out.println(a[0]);
		String report_srl1 = a[0];
		String msg = loginServices.addalerter(alertEntity, formmode, userid, username, MOB, ROLE, report_srl1);

		return msg;

	}

	@RequestMapping(value = "verifyUser", method = RequestMethod.POST)
	@ResponseBody
	public String verifyUser(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.verifyUser(userprofile, userid);

		return msg;

	}

	@RequestMapping(value = "passwordReset", method = RequestMethod.POST)
	@ResponseBody
	public String passwordReset(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.passwordReset(userprofile, userid);

		return msg;

	}

	@RequestMapping(value = "defaultpasswordReset", method = RequestMethod.POST)
	@ResponseBody
	public String DefaultpasswordReset(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.DefaultpasswordReset(userprofile, userid);

		return msg;

	}

	@RequestMapping(value = "changePassword", method = RequestMethod.POST)
	@ResponseBody
	public String changePassword(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
			Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.changePassword(oldpass, newpass, userid);

		return msg;

	}

	@RequestMapping(value = "userLogs/Download", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource UserDownload(HttpServletResponse response, @RequestParam String fromdate,
			@RequestParam String todate) throws IOException, SQLException {
		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;

		try {
			Date fromdate2 = new SimpleDateFormat("dd-MM-yyyy").parse(fromdate);
			Date todate2 = new SimpleDateFormat("dd-MM-yyyy").parse(todate);
			File repfile = loginServices.getUserLogFile(fromdate2, todate2);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "logoutUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String logoutUpdate(HttpServletRequest req) {

		String msg;

		String userid = (String) req.getSession().getAttribute("USERID");

		try {
			logger.info("Updating Logout");
			loginServices.SessionLogging("LOGOUT", "M0", req.getSession().getId(), userid, req.getRemoteAddr(),
					"IN-ACTIVE");
			msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			msg = "failed";
		}
		return msg;
	}

	@RequestMapping(value = "createAlert", method = RequestMethod.POST)
	@ResponseBody
	public String createRule(@RequestParam("formmode") String formmode,
			@ModelAttribute AlertManagementEntity alertparam, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");

		String msg = alertservices.addAlert(alertparam, formmode, userid);

		return msg;

	}

	@RequestMapping(value = "getchanges2", method = RequestMethod.GET)
	@ResponseBody
	public String getchanges2(@RequestParam("audit_ref_no") String auditRefNo) {
		System.out.println("Received audit_ref_no: " + auditRefNo);

		try {
			List<MANUAL_Service_Entity> changes = mANUAL_Service_Rep.getServiceAudiT(auditRefNo);

			if (changes == null || changes.isEmpty()) {
				return ""; // No data found
			}

			StringBuilder sb = new StringBuilder();
			for (MANUAL_Service_Entity entity : changes) {
				sb.append(entity.getField_name()).append(": OldValue: ").append(entity.getOld_value())
						.append(", NewValue: ").append(entity.getNew_value()).append("|||");
			}

			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	@Autowired
	Kyc_Repo kyc_repo;
	@Autowired
	Kyc_Corprate_Repo kyc_corporate_repo;
	@Autowired
	com.bornfire.xbrl.services.Kyc_Corprate_service Kyc_Corprate_service;
	@Autowired
	IndividualPdfService IndividualPdfService;
	@Autowired
	KYC_Audit_Rep KYC_Audit_Rep;

	@RequestMapping(value = "kyc", method = { RequestMethod.GET, RequestMethod.POST })
	public String KYCHome(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String customerRisk, @RequestParam(required = false) Integer age, // 'age'
																												// here
																												// means
																												// pending
																												// days
			Model md, HttpServletRequest req) {

		String ROLEID = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		formmode = (formmode == null) ? "individual" : formmode;

		boolean isBranchRole = "DCD_BRANCH".equals(ROLEID);
		// Check if both filter parameters are present and not empty
		boolean hasFilters = (customerRisk != null && !customerRisk.isEmpty() && age != null);

		if ("corporate".equals(formmode)) {
			List<Object[]> results = isBranchRole
					? (hasFilters ? kyc_corporate_repo.getBranchDynamicValue(customerRisk, age, BRANCHCODE)
							: kyc_corporate_repo.getBranchList(BRANCHCODE))
					: (hasFilters ? kyc_corporate_repo.getDynamicValue(customerRisk, age)
							: kyc_corporate_repo.getList());
			md.addAttribute("kycData", results);
		} else { // Individual case
			List<Object[]> results = isBranchRole
					? (hasFilters
							? ecddIndividualProfileRepository.findFilteredIndividualsByBranch(customerRisk, age,
									BRANCHCODE)
							: ecddIndividualProfileRepository.findAllIndividualsByBranch(BRANCHCODE))
					: (hasFilters ? ecddIndividualProfileRepository.findFilteredIndividuals(customerRisk, age)
							: ecddIndividualProfileRepository.findAllIndividuals());
			md.addAttribute("reportlist", results);
		}

		md.addAttribute("formmode", formmode);
		return "KYC_Home";
	}

	@RequestMapping(value = "/kyc/individual", method = { RequestMethod.GET, RequestMethod.POST })
	public Object kycIndividual(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String custid, @RequestParam(required = false) String srlno,
			@RequestParam(defaultValue = "false") boolean ajax, @ModelAttribute Ecdd_Individual_Profile_Entity data,
			Model model, HttpServletRequest req) throws Exception {

		if (ajax) {
			try {
				boolean success = kyc_individual_service.updateKycData(srlno, data, req);
				if (success) {
					return new ResponseEntity<>("Section saved successfully.", HttpStatus.OK);
				} else {
					return new ResponseEntity<>("Record not found for SRL No: " + srlno, HttpStatus.NOT_FOUND);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>("Error saving data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if ("submit".equals(formmode)) {
			kyc_individual_service.updateKycData(srlno, data, req);
			return "redirect:/kyc/individual?formmode=view&srlno=" + srlno;
		}

		if ("verified".equals(formmode)) {
			kyc_individual_service.verified(custid, req);
		} else if ("download".equals(formmode)) {
			kyc_individual_service.GrtPdf(custid);
		} else if ("view".equals(formmode)) {
			model.addAttribute("formmode", "view");
			Ecdd_Individual_Profile_Entity user_data = ecddIndividualProfileRepository.GetUserBySrlNo(srlno);
			model.addAttribute("user_data", user_data);
		} else if ("modify".equals(formmode)) {
			model.addAttribute("formmode", "modify");
			Ecdd_Individual_Profile_Entity user_data = ecddIndividualProfileRepository.GetUserBySrlNo(srlno);
			model.addAttribute("user_data", user_data);
		} else if ("verify".equals(formmode)) {
			model.addAttribute("formmode", "verify");
			Ecdd_Individual_Profile_Entity user_data = ecddIndividualProfileRepository.GetUserBySrlNo(srlno);
			model.addAttribute("user_data", user_data);
		}

		return "Kyc_individual_ecdd";
	}

	@RequestMapping(value = "/kyc/corporate", method = { RequestMethod.GET, RequestMethod.POST })
	public String kyccorporate(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String uae, @RequestParam(required = false) String custid,
			@RequestParam(required = false) String srl_no, @ModelAttribute EcddCorporateEntity data, Model model,
			HttpServletRequest req, HttpServletResponse response)
			throws FileNotFoundException, JRException, SQLException, Exception {

		System.out.println("KYC Corporate form called");

		String userId = (String) req.getSession().getAttribute("USERID");
		String userName = (String) req.getSession().getAttribute("USERNAME");
		String workClass = (String) req.getSession().getAttribute("WORKCLASS");

		String ajaxParam = req.getParameter("ajax");
		if ("true".equals(ajaxParam)) {
			try {
				Kyc_Corprate_service.updateKycData(srl_no, data, req);

				response.setContentType("application/json");
				response.getWriter().write("{\"status\":\"success\", \"message\":\"Section saved!\"}");
				return null;
			} catch (Exception e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter()
						.write("{\"status\":\"error\", \"message\":\"Save failed: " + e.getMessage() + "\"}");
				return null;
			}
		}

		if ("submit".equalsIgnoreCase(formmode)) {
			Kyc_Corprate_service.updateKycData(srl_no, data, req);
			formmode = "view";
		} else if ("verified".equalsIgnoreCase(formmode)) {
			Kyc_Corprate_service.verified(custid, req);
		} else if ("download".equalsIgnoreCase(formmode)) {
			Kyc_Corprate_service.GrtPdf(srl_no, req);
		}

		List<EcddCorporateEntity> user_data = kyc_corporate_repo.GetUser(srl_no);
		model.addAttribute("userId", userId);
		model.addAttribute("user_data", user_data);
		model.addAttribute("formmode", formmode);

		return "kyc_corporate";
	}

	@PostMapping("/kyc/corporate/verify")
	@ResponseBody
	public String verifyRecord(@RequestParam String custid, HttpServletRequest req) {
		try {
			Kyc_Corprate_service.verified(custid, req);
			return "Verification successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Verification failed";
		}
	}

	@GetMapping("/kyc/Oneyeartran/Download")
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadCustomer(@RequestParam String custid, HttpServletRequest req) {

		List<Ecdd_customer_transaction> transactions = Ecdd_customer_transaction_repo.gettrandetails(custid);
		System.out.println("Enter Ecddv Transaction Download");

		if (transactions.isEmpty()) {
			System.out.println("No Transaction available for this customer");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Transactions");

			// Style for header
			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// Style for normal cells
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);

			// Header row
			String[] headers = { "CUSTOMER ID", "TRAN DATE", "TRAN ID", "TRAN TYPE", "SUB TRAN TYPE",
					"TRANSACTION INDICATOR", "TRANSACTION AMOUNT", "TRAN PARTICULAR" };

			Row header = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// Data rows
			int rowNum = 1;
			for (Ecdd_customer_transaction tx : transactions) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(tx.getCustomer_id());
				row.createCell(1).setCellValue(tx.getTran_date().toString());
				row.createCell(2).setCellValue(tx.getTran_id());
				row.createCell(3).setCellValue(tx.getTran_type());
				row.createCell(4).setCellValue(tx.getSub_tran_type());
				row.createCell(5).setCellValue(tx.getTranaction_indicator());
				row.createCell(6).setCellValue(tx.getTransaction_amount().doubleValue());
				row.createCell(7).setCellValue(tx.getTran_particular());

				// Apply border style to all cells in row
				for (int i = 0; i < 8; i++) {
					row.getCell(i).setCellStyle(borderStyle);
				}
			}

			// Auto-size all columns
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			// Protect the sheet with password (read-only protection)
			sheet.protectSheet("Banktrandetailsbornfire@12345"); // Set your own password here

			// Write to stream
			workbook.write(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			HttpHeaders headersHttp = new HttpHeaders();
			headersHttp.add("Content-Disposition", "attachment; filename=" + custid + "_transactions.xlsx");

			return ResponseEntity.ok().headers(headersHttp).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(in));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("kyc/Reportstatus/Download")
	@ResponseBody
	public ResponseEntity<InputStreamResource> Downlaodkycstatus(HttpServletRequest req) {

		logger.info("Receiving Kyc status download request");

		List<Object[]> completedstatus = ecddIndividualProfileRepository.getstatuscount();
		List<Object[]> pendingstatus = ecddIndividualProfileRepository.getpendingstatuscount();
		List<Object[]> corpcompletedstatus = kyc_corporate_repo.getstatuscount();
		List<Object[]> corppendingstatus = kyc_corporate_repo.getpendingstatuscount();
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("ECDD Report Status");

			// Create title style
			CellStyle titleStyle = workbook.createCellStyle();
			Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 14);
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			// Create header style
			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);

			// Border style for data cells
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);

			// INDIVIDUAL Completed: Rows 0–10, Columns A–D
			Row indivTitleRow = sheet.createRow(0);
			indivTitleRow.setHeightInPoints(20);
			Cell indivBankCell = indivTitleRow.createCell(0);
			indivBankCell.setCellValue("Bank of Baroda");
			indivBankCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

			Row indivSubTitleRow = sheet.createRow(1);
			indivSubTitleRow.setHeightInPoints(18);
			Cell indivSubTitleCell = indivSubTitleRow.createCell(0);
			indivSubTitleCell.setCellValue("Ecdd Individual Completed Status");
			indivSubTitleCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

			Row indivHeaderRow = sheet.createRow(2);
			String[] headers = { "Branch Name", "Low Risk", "Medium Risk", "High Risk" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = indivHeaderRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			int indivRowNum = 3;
			for (Object[] rowData : completedstatus) {
				Row row = sheet.createRow(indivRowNum++);
				for (int col = 0; col < rowData.length; col++) {
					Cell cell = row.createCell(col);
					if (rowData[col] instanceof String) {
						cell.setCellValue((String) rowData[col]);
					} else if (rowData[col] instanceof Number) {
						cell.setCellValue(((Number) rowData[col]).doubleValue());
					}
					cell.setCellStyle(borderStyle);
				}
			}

			Row indivPendingTitle = sheet.createRow(11);
			indivPendingTitle.setHeightInPoints(18);
			Cell pendingTitleCell = indivPendingTitle.createCell(0);
			pendingTitleCell.setCellValue("Ecdd Individual Pending Status");
			pendingTitleCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(11, 11, 0, 3));

			Row indivPendingHeader = sheet.createRow(12);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = indivPendingHeader.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			int indivPendingRowNum = 13;
			for (Object[] rowData : pendingstatus) {
				Row row = sheet.createRow(indivPendingRowNum++);
				for (int col = 0; col < rowData.length; col++) {
					Cell cell = row.createCell(col);
					if (rowData[col] instanceof String) {
						cell.setCellValue((String) rowData[col]);
					} else if (rowData[col] instanceof Number) {
						cell.setCellValue(((Number) rowData[col]).doubleValue());
					}
					cell.setCellStyle(borderStyle);
				}
			}

			Row corpTitleRow = sheet.getRow(0);
			Cell corpBankCell = corpTitleRow.createCell(6); // G
			corpBankCell.setCellValue("Bank of Baroda");
			corpBankCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 9));

			Row corpSubTitleRow = sheet.getRow(1);
			Cell corpSubTitleCell = corpSubTitleRow.createCell(6);
			corpSubTitleCell.setCellValue("Ecdd Corporate Completed Status");
			corpSubTitleCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 9));

			Row corpHeaderRow = sheet.getRow(2);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = corpHeaderRow.createCell(6 + i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			int corpRowNum = 3;
			for (Object[] rowData : corpcompletedstatus) {
				Row row = sheet.getRow(corpRowNum);
				if (row == null)
					row = sheet.createRow(corpRowNum);
				for (int col = 0; col < rowData.length; col++) {
					Cell cell = row.createCell(6 + col);
					if (rowData[col] instanceof String) {
						cell.setCellValue((String) rowData[col]);
					} else if (rowData[col] instanceof Number) {
						cell.setCellValue(((Number) rowData[col]).doubleValue());
					}
					cell.setCellStyle(borderStyle);
				}
				corpRowNum++;
			}

			Row corpPendTitle = sheet.getRow(11);
			Cell corpPendingTitleCell = corpPendTitle.createCell(6);
			corpPendingTitleCell.setCellValue("Ecdd Corporate Pending Status");
			corpPendingTitleCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(11, 11, 6, 9));

			Row corpPendHeader = sheet.getRow(12);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = corpPendHeader.createCell(6 + i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			int corpPendRowNum = 13;
			for (Object[] rowData : corppendingstatus) {
				Row row = sheet.getRow(corpPendRowNum);
				if (row == null)
					row = sheet.createRow(corpPendRowNum);
				for (int col = 0; col < rowData.length; col++) {
					Cell cell = row.createCell(6 + col);
					if (rowData[col] instanceof String) {
						cell.setCellValue((String) rowData[col]);
					} else if (rowData[col] instanceof Number) {
						cell.setCellValue(((Number) rowData[col]).doubleValue());
					}
					cell.setCellStyle(borderStyle);
				}
				corpPendRowNum++;
			}

			// Write to output stream
			workbook.write(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			// If you want to return as ResponseEntity (Spring Boot)
			HttpHeaders headersHttp = new HttpHeaders();
			headersHttp.add("Content-Disposition", "attachment; filename=ecdd_completed_report.xlsx");

			return ResponseEntity.ok().headers(headersHttp).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(in));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/kyc/individual/verify")
	@ResponseBody
	public String verifyindRecord(@RequestParam String custid, HttpServletRequest req) {
		try {
			kyc_individual_service.verified(custid, req);
			return "Verification successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Verification failed";
		}
	}

	@RequestMapping(value = "kyc/corporate/download", method = RequestMethod.GET)

	@ResponseBody
	public ResponseEntity<InputStreamResource> corporateDownload(HttpServletResponse response, HttpServletRequest req,
			@RequestParam(required = false) String srl_no) throws IOException, SQLException {

		try {

			File repfile = Kyc_Corprate_service.GrtPdf(srl_no, req);

			System.out.println("Generated file: " + repfile.getName());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", repfile.getName());

			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(repfile.length()).body(resource);
		} catch (IOException | SQLException | JRException e) {
			logger.error("Error occurred while processing the file download: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@RequestMapping(value = "kyc/individual/downloadfn", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> individualDownload(HttpServletResponse response, HttpServletRequest req,
			@RequestParam(required = false) String srlno) throws Exception {

		try {

			File repfile = IndividualPdfService.generateIndividualPdf(srlno, req);

			System.out.println("Generated file: " + repfile.getName());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", repfile.getName());

			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(repfile.length()).body(resource);
		} catch (IOException | SQLException | JRException e) {
			logger.error("Error occurred while processing the file download: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@RequestMapping(value = "auditlogs", method = RequestMethod.GET)
	public String Auditvalue(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		} else if (formmode.equals("list1")) {
			md.addAttribute("formmode", "list1");
		} else if (formmode.equals("upload1")) {
			md.addAttribute("formmode", "upload1");
		} else if (formmode.equals("upload2")) {
			md.addAttribute("formmode", "upload2");
		} else if (formmode.equals("upload3")) {
			md.addAttribute("formmode", "upload3");
		}

		return "Audittrailskyc";
	}

	@RequestMapping(value = "useractivity", method = { RequestMethod.GET, RequestMethod.POST })
	public String useractivity(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {
		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");

			// Fetch the audit list based on the determined date

			model.addAttribute("AuditList", KYC_Audit_Rep.getauditListLocalvaluesbusiness(fromDateToUse));

		}

		return "AuditTrailValueskyc";
	}

	@RequestMapping(value = "OperationLogs", method = { RequestMethod.GET, RequestMethod.POST })
	public String OperationLogs(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {

		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use
		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");
			model.addAttribute("AuditList", KYC_Audit_Rep.getauditListLocalvaluesbusiness1(fromDateToUse));
		}

		return "BusinessTrailkyc";
	}

	@RequestMapping(value = "getchanges", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String fetchChanges(@RequestParam(required = false) String audit_ref_no) {

		// Fetch data from the database using the repository
		String changeDetails = KYC_Audit_Rep.getchanges(audit_ref_no); // Example of getting data

		// Process the change details to format as required

		return changeDetails; // Return the formatted changes
	}

	@RequestMapping(value = "custprofile", method = RequestMethod.GET)
	public String custprofile(@RequestParam(required = false) String cif_id,
			@RequestParam(required = false) String acct_no, @RequestParam(required = false) String formmode,
			@RequestParam(required = false) String customerType, @RequestParam(required = false) String tranId, // New
			@RequestParam(required = false) String billId, @RequestParam(required = false) String BG_SRL_NUM, // parameter
			@RequestParam(required = false) String DC_ID, Model md, HttpServletRequest req) {

		formmode = (formmode == null) ? "list" : formmode;
		md.addAttribute("formmode", formmode);
		/*
		 * System.out.println("Received cif_id: " + cif_id);
		 * System.out.println("Received natIdCardNum: " + natIdCardNum);
		 */
		/*
		 * if (tranId != null && !tranId.isEmpty()) { md.addAttribute("gettransaction",
		 * charge_Back_Rep.gettransaction(tranId)); } else { // Handle case when tranId
		 * is null or empty md.addAttribute("gettransaction", new ArrayList<>()); //
		 * Send an empty list to avoid errors }
		 */
		if (formmode == "transdetails") {
			System.out.println("hihihihihihi" + charge_Back_Rep.gettransaction(tranId));
			md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));

		}
		switch (formmode) {
		case "list":
			md.addAttribute("getlistcust", charge_Back_Rep.getAllcust());
			break;
		case "Dataquality":
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
			break;
		/*
		 * case "persdetail": md.addAttribute("getpersonal",
		 * charge_Back_Rep.getpersonal(cif_id)); break; case "adrsdetail":
		 * md.addAttribute("getadress", charge_Back_Rep.getadress(cif_id)); break; case
		 * "tradinfinance": md.addAttribute("gettrad", charge_Back_Rep.gettrad(cif_id));
		 * break; case "empdetail": md.addAttribute("getemploye",
		 * charge_Back_Rep.getemploye(cif_id)); break; case "documentdetail":
		 * md.addAttribute("getdocument", charge_Back_Rep.getdocument(cif_id)); break;
		 * case "acctsdetail": md.addAttribute("getaccts2",
		 * charge_Back_Rep.getaccts2(acct_no)); break; case "transdetails":
		 * md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));
		 * break; case "photodetail": md.addAttribute("getpic",
		 * charge_Back_Rep.getpic(cif_id, acct_no)); break; case "JointHolderdetails":
		 * md.addAttribute("getjoint", charge_Back_Rep.getjoint(cif_id)); break; case
		 * "signdetail": md.addAttribute("getsignature",
		 * charge_Back_Rep.getsignature(cif_id)); break; case "associatedetail":
		 * md.addAttribute("getassociate", charge_Back_Rep.getassociate(cif_id)); break;
		 * case "tradflgdetail": //System.out.println("Received BILL_ID: " + billId);
		 * System.out.println( "CIF_ID: " + cif_id + ", BILL_ID: " + billId);
		 * 
		 * md.addAttribute("gettradEflg", charge_Back_Rep.gettradEflg(cif_id, billId));
		 * break;
		 * 
		 * case "tradflgBankGuarantee": //System.out.println("Received BILL_ID: " +
		 * billId); System.out.println( "CIF_ID: " + cif_id + ", BG_SRL_NUM: " +
		 * BG_SRL_NUM);
		 * 
		 * md.addAttribute("getbankflag", charge_Back_Rep.getbankflag(cif_id,
		 * BG_SRL_NUM)); break; case "tradflgLetterOfCredit":
		 * //System.out.println("Received BILL_ID: " + billId); System.out.println(
		 * "CIF_ID: " + cif_id + ", DC_ID: " + DC_ID);
		 * 
		 * md.addAttribute("getLetofcreditS", charge_Back_Rep.getLetofcreditS(cif_id,
		 * DC_ID)); break;
		 */

		default:
			break;
		}

		return "QA_Customer_profile.html";
	}

	@RequestMapping(value = "acctprofile", method = RequestMethod.GET)
	public String acctprofile(@RequestParam(required = false) String cif_id, String acct_no, String formmode,
			@RequestParam(required = false) String customerType1, @RequestParam(required = false) String tranId,
			@RequestParam(required = false) String billId, Model md, HttpServletRequest req) {

		// Default formmode to "list" if null
		formmode = (formmode == null) ? "list" : formmode;
		md.addAttribute("formmode", formmode);

		// If formmode is "trandetail" and tranId is not null
		if ("trandetail".equals(formmode) && tranId != null) {
			System.out.println("Fetching transactions for tranId: " + tranId);
			md.addAttribute("gettransactions", charge_Back_Rep.gettransactions(tranId));
		}

		else if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getlistacct", charge_Back_Rep.getAllacct());
		} else if (formmode.equals("Dataquality")) {
			md.addAttribute("formmode", "Dataquality");
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
		} else if (formmode.equals("persdetail")) {
			md.addAttribute("formmode", "persdetail");
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
		} else if (formmode.equals("adrdetail")) {
			md.addAttribute("formmode", "adrdetail");
			md.addAttribute("getadres", charge_Back_Rep.getadres(cif_id, acct_no));
		} else if (formmode.equals("acctdetail")) {
			md.addAttribute("formmode", "acctdetail");
			/* md.addAttribute("getacct", charge_Back_Rep.getacct(cif_id, acct_no)); */
			md.addAttribute("getaccts1", charge_Back_Rep.getaccts1(acct_no));
		} else if (formmode.equals("trandetail")) {
			md.addAttribute("formmode", "trandetail");
			md.addAttribute("gettransactions", charge_Back_Rep.gettransactions(tranId));
		} else if (formmode.equals("docdetail")) {
			md.addAttribute("formmode", "docdetail");
			md.addAttribute("getdoc", charge_Back_Rep.getdoc(cif_id, acct_no));
		} else if (formmode.equals("tradefinance")) {
			md.addAttribute("formmode", "tradefinance");
			md.addAttribute("gettrade", charge_Back_Rep.gettrade(cif_id, acct_no));
		} else if (formmode.equals("empprofile")) {
			md.addAttribute("formmode", "empprofile");
			md.addAttribute("getemp", charge_Back_Rep.getemp(cif_id, acct_no));
		} else if (formmode.equals("signdetail")) {
			md.addAttribute("formmode", "signdetail");
			md.addAttribute("getsign", charge_Back_Rep.getsign(cif_id, acct_no));
		} else if (formmode.equals("associatedetail")) {
			md.addAttribute("formmode", "associatedetail");
			md.addAttribute("getassociated", charge_Back_Rep.getassociated(cif_id, acct_no));
		} else if (formmode.equals("JointHolderdetails")) {
			md.addAttribute("formmode", "JointHolderdetails");
			md.addAttribute("getjoints", charge_Back_Rep.getjoints(cif_id, acct_no));
		} else if (formmode.equals("photodetails")) {
			md.addAttribute("formmode", "photodetails");
			md.addAttribute("getpics", charge_Back_Rep.getpics(cif_id, acct_no));
		} else if (formmode.equals("tradeflgdetail")) {
			md.addAttribute("formmode", "tradeflgdetail");
			System.out.println("CIF_ID: " + cif_id + ", BILL_ID: " + billId);
			md.addAttribute("gettradEflag", charge_Back_Rep.gettradEflag(cif_id, billId));
		} else if ("corporate".equals(customerType1)) {
			md.addAttribute("formmode", "cifnumber1");
			md.addAttribute("getAll1", charge_Back_Rep.getCorporateCustomers1());
		}

		return "QA_Account_profile.html";
	}

	@RequestMapping(value = "dataQuality", method = RequestMethod.GET)
	public String dataprofile(@RequestParam(required = false) String cif_id,
			@RequestParam(required = false) String acct_no, @RequestParam(required = false) String formmode,
			@RequestParam(required = false) String customerType, @RequestParam(required = false) String tranId, // New
																												// parameter
			Model md, HttpServletRequest req) {

		formmode = (formmode == null) ? "list" : formmode;
		md.addAttribute("formmode", formmode);
		/*
		 * System.out.println("Received cif_id: " + cif_id);
		 * System.out.println("Received natIdCardNum: " + natIdCardNum);
		 */
		if (tranId != null && !tranId.isEmpty()) {
			md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));
		} else {
			// Handle case when tranId is null or empty
			md.addAttribute("gettransaction", new ArrayList<>()); // Send an empty list to avoid errors
		}

		if (formmode == "transdetails") {
			System.out.println("hihihihihihi" + charge_Back_Rep.gettransaction(tranId));
			md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));

		}
		switch (formmode) {
		/*
		 * case "list": md.addAttribute("getlistcust", charge_Back_Rep.getAllcust());
		 * break; case "list": md.addAttribute("getlistcust",
		 * charge_Back_Rep.getAllcust()); break;
		 */
		case "Dataquality":
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
			break;

		case "cifnumber":
			// Fetch and add corporate customers
			List<Object[]> corporateCustomers = charge_Back_Rep.getCorporateCustomers();
			md.addAttribute("getCorporateCustomers", corporateCustomers);
			System.out.println("Corporate Customers: " + corporateCustomers);

			// Fetch and add retail customers
			List<Object[]> retailCustomers = charge_Back_Rep.getRetailCustomers();
			md.addAttribute("getRetailCustomers", retailCustomers);
			System.out.println("Retail Customers: " + retailCustomers);

			// Fetch and add all customers (both corporate and retail)
			List<Object[]> allCustomers = charge_Back_Rep.getAll();
			md.addAttribute("getAll", allCustomers);
			System.out.println("All Customers: " + allCustomers);
			break;

		case "customername":
			md.addAttribute("getName", charge_Back_Rep.getName());
			break;
		case "Dateofbirth":
			md.addAttribute("getcustdob", charge_Back_Rep.getcustdob());
			break;
		case "placeofbirth":
			md.addAttribute("getpob", charge_Back_Rep.getpob());
			break;
		case "PassportExpiry":
			md.addAttribute("getPass", charge_Back_Rep.getPass());
			break;

		case "PassportNo":
			md.addAttribute("getPassno", charge_Back_Rep.getPassno());
			break;
		case "customname":
			md.addAttribute("getName1", charge_Back_Rep.getName1());
			break;

		case "CountryofResidency":
			md.addAttribute("getCountRes", charge_Back_Rep.getCountRes());
			break;
		case "MarkerofEmployed":
			md.addAttribute("getMrkEmp", charge_Back_Rep.getMrkEmp());
			break;
		case "EmployerName":
			md.addAttribute("getEmpname", charge_Back_Rep.getEmpname());
			break;

		case "Residencyaddress":
			md.addAttribute("getResadd", charge_Back_Rep.getResadd());
			break;
		case "Poboxpostalcode":
			md.addAttribute("getpostal", charge_Back_Rep.getpostal());
			break;
		case "Customerriskrating":
			md.addAttribute("getriskrate", charge_Back_Rep.getriskrate());
			break;
		case "Monthlysalary":
			md.addAttribute("getmonth", charge_Back_Rep.getmonth());
			break;
		case "Addmonthsalary":
			md.addAttribute("getAddmonth", charge_Back_Rep.getAddmonth());
			break;
		case "Natinality1":
			md.addAttribute("getnation", charge_Back_Rep.getnation());
			break;
		case "Natinality2":
			md.addAttribute("getnation2", charge_Back_Rep.getnation2());
			break;

		case "DualNatinality":
			md.addAttribute("getDualnation", charge_Back_Rep.getDualnation());
			break;
		case "KYCReviewdate":
			md.addAttribute("getkyc", charge_Back_Rep.getkyc());
			break;
		case "TotalAnnualIncome":
			md.addAttribute("getTotalincome", charge_Back_Rep.getTotalincome());
			break;
		case "SalaryTransferredBank":
			md.addAttribute("getkyc", charge_Back_Rep.getkyc());
			break;
		case "EmiratesID":
			md.addAttribute("getEmid", charge_Back_Rep.getEmid());
			break;
		case "RelatedPartiesFlag":
			md.addAttribute("getEmid", charge_Back_Rep.getEmid());
			break;
		case "EmiratesExpDate":
			md.addAttribute("getEmiExpDate", charge_Back_Rep.getEmiExpDate());
			break;
		case "ResidenceMarker":
			md.addAttribute("getResidmark", charge_Back_Rep.getResidmark());
			break;
		case "CustNameMismatch":
			md.addAttribute("getcustName", charge_Back_Rep.getcustName());
			break;
		case "GENDER":
			md.addAttribute("getGEN", charge_Back_Rep.getGEN());
			break;
		case "Email":
			md.addAttribute("getEmail", charge_Back_Rep.getEmail());
			break;
		case "Birthday":
			md.addAttribute("getBirth", charge_Back_Rep.getBirth());
			break;
		case "CountryTaxResidence":
			md.addAttribute("getCountrytax", charge_Back_Rep.getCountrytax());
			break;
		case "Shortname":
			md.addAttribute("getShortname", charge_Back_Rep.getShortname());
			break;
		case "LoanDetails":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "CreditRating":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "Phone":
			md.addAttribute("getphone", charge_Back_Rep.getphone());
			break;
		case "TaxCompliance":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "RealEstate":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "pep":
			md.addAttribute("getnation2", charge_Back_Rep.getnation2());
			break;
		/*
		 * case "TotalAnnualIncome": md.addAttribute("getkyc",
		 * charge_Back_Rep.getkyc()); break;
		 */
		/*
		 * case "customername": List<Object[]> names = charge_Back_Rep.getname();
		 * md.addAttribute("getname", names); names.forEach(name ->
		 * System.out.println(Arrays.toString(name))); // Log each row break;
		 */

		default:
			break;
		}

		return "DataQuality.html";
	}

	@PostMapping("/kyc/indivdual/verify")
	@ResponseBody
	public String verifyRecord1(@RequestParam String custid, HttpServletRequest req) {
		try {
			kyc_individual_service.verified(custid, req);
			return "Verification successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Verification failed";
		}
	}

	@Autowired
	private EcddUploadDocumentService documentService;

	@PostMapping("/kyc/individual/upload-document")
	public ResponseEntity<String> uploadDocuments(@RequestParam("files") MultipartFile[] files,
			@RequestParam("srl_no") String srlNo, @RequestParam("customer_id") String customerId,
			@RequestParam("customer_type") String customerType, HttpSession session) {
		if (files.length == 0 || (files.length == 1 && files[0].isEmpty())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select at least one file to upload.");
		}

		try {
			String uploadedBy = (String) session.getAttribute("USERNAME");
			if (uploadedBy == null || uploadedBy.isEmpty()) {
				uploadedBy = "SYSTEM"; // Fallback
			}
			documentService.saveDocuments(files, srlNo, customerId, customerType, uploadedBy);

			return ResponseEntity.ok("Documents uploaded successfully.");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not upload files: " + e.getMessage());
		}
	}

	@GetMapping("/kyc/individual/list-documents")
	@ResponseBody // Sends data as JSON
	public List<EcddCustomerDocumentsEntity> listDocuments(@RequestParam("customerId") String customerId) {
		return documentService.getDocumentList(customerId);
	}

	@GetMapping("/kyc/individual/download-doc/{docId}")
	public ResponseEntity<byte[]> downloadDocument(@PathVariable Long docId) {
		try {
			EcddCustomerDocumentsEntity doc = documentService.getDocumentForDownload(docId);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getMimeType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocumentName() + "\"")
					.body(doc.getDocumentContent());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PostMapping("/kyc/corporate/upload-document")
	public ResponseEntity<String> uploadcorpDocuments(@RequestParam("files") MultipartFile[] files,
			@RequestParam("srl_no") String srlNo, @RequestParam("customer_id") String customerId,
			@RequestParam("customer_type") String customerType, HttpSession session) {
		if (files.length == 0 || (files.length == 1 && files[0].isEmpty())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select at least one file to upload.");
		}
		try {
			String uploadedBy = (String) session.getAttribute("USERNAME");
			if (uploadedBy == null || uploadedBy.isEmpty())
				uploadedBy = "SYSTEM";

			documentService.saveDocuments(files, srlNo, customerId, customerType, uploadedBy);
			return ResponseEntity.ok("Documents uploaded successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not upload files: " + e.getMessage());
		}
	}

	@GetMapping("/kyc/corporate/list-documents")
	@ResponseBody
	public List<EcddCustomerDocumentsEntity> listcorpDocuments(@RequestParam("customerId") String customerId) {
		return documentService.getDocumentList(customerId);
	}

	@GetMapping("/kyc/corporate/download-doc/{docId}")
	public ResponseEntity<byte[]> downloadcorpDocument(@PathVariable Long docId) {
		try {
			EcddCustomerDocumentsEntity doc = documentService.getDocumentForDownload(docId);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getMimeType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocumentName() + "\"")
					.body(doc.getDocumentContent());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	public List<AuditTablePojo> getauditListLocalvaluesbusiness(Date fromDateToUse) {
		List<MANUAL_Service_Entity> auditList = mANUAL_Service_Rep.getauditListLocalvaluesbusiness(fromDateToUse);
		List<AuditTablePojo> auditPojoList = new ArrayList<>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		for (MANUAL_Service_Entity ipsAudit : auditList) {
			boolean isUpdated = false;

			// Check if an entry with the same ID and stage3 remarks already exists
			for (AuditTablePojo existingPojo : auditPojoList) {
				String auditRefNo = existingPojo.getAudit_ref_no();
				String remarks = existingPojo.getRemarks();
				String ipsAuditno = ipsAudit.getAudit_ref_no();

				if (auditRefNo != null && ipsAuditno != null && auditRefNo.equals(ipsAuditno) && remarks != null
						&& ("Login Successfully".equals(remarks) || "Logout Successfully".equals(remarks))) {

					// Update existing entry's data
					existingPojo.setAudit_table(ipsAudit.getAudit_table());
					existingPojo.setFunc_code(ipsAudit.getFunc_code());
					existingPojo.setEntry_user(ipsAudit.getEntry_user());
					existingPojo.setEntry_time(ipsAudit.getEntry_time());
					existingPojo.setAuth_user(ipsAudit.getAuth_user());
					existingPojo.setAuth_time(ipsAudit.getAuth_time());
					existingPojo.setRemarks(ipsAudit.getRemarks());

					List<String> fieldName = new ArrayList<>();
					List<String> oldvalue = new ArrayList<>();
					List<String> newvalue = new ArrayList<>();

					// Populate lists excluding "FIELD 4"
					String[] oldValues = ipsAudit.getOld_value().split("\\|\\|");
					String[] newValues = ipsAudit.getNew_value().split("\\|\\|");
					String[] fields = ipsAudit.getField_name().split("\\|\\|");

					for (int i = 0; i < fields.length; i++) {
						if (!"FIELD 4".equals(fields[i])) {
							fieldName.add(fields[i]);

							// Format the old and new values if they are date strings
							String oldFormatted = formatDate(oldValues[i], dateFormat);
							String newFormatted = formatDate(newValues[i], dateFormat);

							oldvalue.add(oldFormatted);
							newvalue.add(newFormatted);
						}
					}

					existingPojo.setField_name(fieldName);
					existingPojo.setOld_value(oldvalue);
					existingPojo.setNew_value(newvalue);

					isUpdated = true;
					break;
				}
			}

			// Create a new entry if no existing entry was updated
			if (!isUpdated) {
				AuditTablePojo auditTablePojo = new AuditTablePojo();
				auditTablePojo.setAudit_table(ipsAudit.getAudit_table());
				auditTablePojo.setFunc_code(ipsAudit.getFunc_code());
				auditTablePojo.setEntry_user(ipsAudit.getEntry_user());
				auditTablePojo.setEntry_time(ipsAudit.getEntry_time());
				auditTablePojo.setAuth_user(ipsAudit.getAuth_user());
				auditTablePojo.setAuth_time(ipsAudit.getAuth_time());
				auditTablePojo.setRemarks(ipsAudit.getRemarks());

				List<String> fieldName = new ArrayList<>();
				List<String> oldvalue = new ArrayList<>();
				List<String> newvalue = new ArrayList<>();

				if (ipsAudit != null && ipsAudit.getModi_details() != null) {
					String[] oldValues = ipsAudit.getOld_value().split("\\|\\|");
					String[] newValues = ipsAudit.getNew_value().split("\\|\\|");
					String[] fields = ipsAudit.getField_name().split("\\|\\|");

					for (int i = 0; i < fields.length; i++) {
						if (!"FIELD 4".equals(fields[i])) {
							fieldName.add(fields[i]);

							// Format the old and new values if they are date strings
							String oldFormatted = formatDate(oldValues[i], dateFormat);
							String newFormatted = formatDate(newValues[i], dateFormat);

							oldvalue.add(oldFormatted);
							newvalue.add(newFormatted);
						}
					}
				} else {
					System.out.println("No modification details available");
				}

				auditTablePojo.setField_name(fieldName);
				auditTablePojo.setOld_value(oldvalue);
				auditTablePojo.setNew_value(newvalue);
				auditPojoList.add(auditTablePojo);
			}
		}

		return auditPojoList;
	}

	// Helper method to format date values as 'DD-MM-YYYY'
	private String formatDate(String value, SimpleDateFormat dateFormat) {
		try {
			// Assuming the value is in a valid date format that SimpleDateFormat can parse
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value); // Adjust this pattern based on
																						// your date format
			return dateFormat.format(date); // Return formatted date as 'DD-MM-YYYY'
		} catch (Exception e) {
			// If parsing fails, return the original value
			return value;
		}
	}

	@RequestMapping(value = "Generateloginotp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String Generateloginotp(@RequestParam("Userid") String Userid) {
		String msg = "success";
		System.out.println(msg);
		return msg;
	}
}
