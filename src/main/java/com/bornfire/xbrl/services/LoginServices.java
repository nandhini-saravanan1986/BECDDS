package com.bornfire.xbrl.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.bornfire.xbrl.config.PasswordEncryption;
import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.AlertEntity;
import com.bornfire.xbrl.entities.AlertRep;
import com.bornfire.xbrl.entities.FinUserProfile;
import com.bornfire.xbrl.entities.FinUserProfileRep;
import com.bornfire.xbrl.entities.KYC_Audit_Entity;
import com.bornfire.xbrl.entities.KYC_Audit_Rep;
import com.bornfire.xbrl.entities.Smsserviceotp;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.XBRLSession;
import com.bornfire.xbrl.entities.BECDDS.AuditServicesEntity;
import com.bornfire.xbrl.entities.BECDDS.AuditServicesRep;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
@ConfigurationProperties("output")
@Transactional
public class LoginServices {

	private static final Logger logger = LoggerFactory.getLogger(LoginServices.class);
	@Autowired
	AlertRep alertRep;

	@Autowired
	AuditServicesRep AuditServicesRep;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	FinUserProfileRep finUserProfileRep;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	DataSource srcdataSource;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	KYC_Audit_Rep KYC_Audit_Rep;

	@Autowired
	private HttpServletRequest req;

	@NotNull
	private String exportpath;

	@Value("${default.password}")
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getExportpath() {
		return exportpath;
	}

	public void setExportpath(String exportpath) {
		this.exportpath = exportpath;
	}

	public String deleteuser(String userId) {
		String msg = "";

		try {
			Optional<UserProfile> user = userProfileRep.findById(userId);

			if (user.isPresent()) {
				// Delete the user
				userProfileRep.deleteById(userId);

				// Get session user details
				String currentUserId = (String) req.getSession().getAttribute("USERID");
				String currentUserName = (String) req.getSession().getAttribute("USERNAME");
				String branchCode = (String) req.getSession().getAttribute("BRANCHCODE");

				// Generate audit ID
				String auditID = sequence.generateRequestUUId();
				Date currentDate = new Date();

				KYC_Audit_Entity audit = new KYC_Audit_Entity();
				audit.setAudit_ref_no(auditID);
				audit.setAudit_date(currentDate);
				audit.setEntry_time(currentDate);
				audit.setEntry_user(currentUserId);
				audit.setEntry_user_name(currentUserName);
				audit.setFunc_code("DELETE");
				audit.setAudit_table("USERPROFILETABLE");
				audit.setAudit_screen("USER_MANAGEMENT");
				audit.setEvent_id(userId);
				audit.setEvent_name(user.get().getUsername());
				audit.setChange_details("User deleted successfully");
				audit.setAuth_user(currentUserId);
				audit.setAuth_user_name(currentUserName);
				audit.setAuth_time(currentDate);

				// MODIFIED HERE
				audit.setRemarks(branchCode);

				// Save audit entity
				KYC_Audit_Rep.save(audit);

				msg = "User deleted successfully";

			} else {
				msg = "Invalid User Id";
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Please contact Administrator";
		}

		return msg;
	}

	public String addUser(UserProfile userProfile, String formmode, String inputUser, String username, String mob,
			String role) {

		String msg = "";

		try {
			Date currentDate = new Date();
			String auditID = sequence.generateRequestUUId();
			String branchCode = (String) req.getSession().getAttribute("BRANCHCODE");

			if (formmode.equalsIgnoreCase("add")) {
				UserProfile up = userProfile;
				try {
					String encryptedPassword = PasswordEncryption.getEncryptedPassword(userProfile.getPassword());

					if ("Active".equalsIgnoreCase(up.getLogin_status())) {
						up.setUser_locked_flg("N");
					} else {
						up.setUser_locked_flg("Y");
					}

					if ("Active".equalsIgnoreCase(up.getUser_status())) {
						up.setDisable_flg("N");
					} else {
						up.setDisable_flg("Y");
					}

					up.setEntity_flg("N");
					up.setEntry_time(currentDate);
					up.setEntry_user(inputUser);
					up.setLogin_flg("N"); // To prompt the user for changing the password at first login
					up.setNo_of_attmp(0);
					up.setLog_in_count("0");
					up.setEmp_name(up.getUsername());

					String localdateval = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
					LocalDate date = LocalDate.parse(localdateval);
					BigDecimal passexpdays = new BigDecimal(up.getPass_exp_days());
					LocalDate date2 = date.plusDays(passexpdays.intValue());
					up.setPass_exp_date(new SimpleDateFormat("yyyy-MM-dd").parse(date2.toString()));
					up.setPassword(encryptedPassword);

					userProfileRep.save(up);

					// ---- Audit for User Creation ----
					KYC_Audit_Entity audit = new KYC_Audit_Entity();
					audit.setAudit_ref_no(auditID);
					audit.setAudit_date(currentDate);
					audit.setEntry_time(currentDate);
					audit.setEntry_user(inputUser);
					audit.setEntry_user_name(username);
					audit.setFunc_code("CREATE");
					audit.setAudit_table("USERPROFILETABLE");
					audit.setAudit_screen("USER_MANAGEMENT");
					audit.setEvent_id(up.getUserid());
					audit.setEvent_name(up.getUsername());
					audit.setChange_details("User created successfully");
					audit.setAuth_user(inputUser);
					audit.setAuth_user_name(username);
					audit.setAuth_time(currentDate);

					// MODIFIED HERE
					audit.setRemarks(branchCode);

					KYC_Audit_Rep.save(audit);

					msg = "User Created Successfully";

				} catch (Exception e) {
					msg = "Kindly check the data/Please contact Administrator";
					e.printStackTrace();
				}

			} else { // ------- Edit case -------
				Optional<UserProfile> up = userProfileRep.findById(userProfile.getUserid());

				if (up.isPresent()) {

					userProfile.setPassword(up.get().getPassword());

					if ("Active".equalsIgnoreCase(userProfile.getLogin_status())) {
						userProfile.setUser_locked_flg("N");
					} else {
						userProfile.setUser_locked_flg("Y");
					}

					if ("Active".equalsIgnoreCase(userProfile.getUser_status())) {
						userProfile.setDisable_flg("N");
					} else {
						userProfile.setDisable_flg("Y");
					}

					if (userProfile.getPass_exp_days().equals(up.get().getPass_exp_days())) {
						userProfile.setPass_exp_date(up.get().getPass_exp_date());
					} else {
						String localdateval = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
						LocalDate date = LocalDate.parse(localdateval);
						BigDecimal passexpdays = new BigDecimal(userProfile.getPass_exp_days());
						LocalDate date2 = date.plusDays(passexpdays.intValue());
						userProfile.setPass_exp_date(new SimpleDateFormat("yyyy-MM-dd").parse(date2.toString()));
					}

					userProfile.setLog_in_count(up.get().getLog_in_count() != null ? up.get().getLog_in_count() : "1");
					userProfile.setEntry_user(up.get().getEntry_user());
					userProfile.setEntry_time(up.get().getEntry_time());
					userProfile.setNo_of_attmp(0);
					userProfile.setEntity_flg("N");
					userProfile.setModify_user(inputUser);
					userProfile.setModify_time(currentDate);

					userProfileRep.save(userProfile);

					// ---- Audit for User Update ----
					KYC_Audit_Entity audit = new KYC_Audit_Entity();
					audit.setAudit_ref_no(auditID);
					audit.setAudit_date(currentDate);
					audit.setEntry_time(currentDate);
					audit.setEntry_user(inputUser);
					audit.setEntry_user_name(username);
					audit.setFunc_code("UPDATE");
					audit.setAudit_table("USERPROFILETABLE");
					audit.setAudit_screen("USER_MANAGEMENT");
					audit.setEvent_id(userProfile.getUserid());
					audit.setEvent_name(userProfile.getUsername());
					audit.setChange_details("User updated successfully");
					audit.setAuth_user(inputUser);
					audit.setAuth_user_name(username);
					audit.setAuth_time(currentDate);

					// MODIFIED HERE
					audit.setRemarks(branchCode);

					KYC_Audit_Rep.save(audit);

					msg = "User Edited Successfully";
				} else {
					msg = "User Not found to edit";
				}
			}
		} catch (Exception e) {
			msg = "Error Occurred. Please contact Administrator";
			e.printStackTrace();
			logger.info(e.getMessage());
		}

		return msg;
	}

	// creating rpt

	public String addalerter(AlertEntity alertEntity, String formmode, String inputUser, String username, String mob,
			String role, String report_srl1) {

		String msg = "";
		Date currentDate = new Date();
		String auditID = sequence.generateRequestUUId();
		String branchCode = (String) req.getSession().getAttribute("BRANCHCODE");

		try {

			if (formmode.equalsIgnoreCase("add")) {
				AlertEntity up = alertEntity;

				// Save alert
				alertRep.save(up);

				// ---- Audit for Alert Creation ----
				KYC_Audit_Entity audit = new KYC_Audit_Entity();
				audit.setAudit_ref_no(auditID);
				audit.setAudit_date(currentDate);
				audit.setEntry_time(currentDate);
				audit.setEntry_user(inputUser);
				audit.setEntry_user_name(username);
				audit.setFunc_code("CREATE");
				audit.setAudit_table("USERPROFILETABLE");
				audit.setAudit_screen("ALERT_MANAGEMENT");
				audit.setEvent_id(up.getReport_srl());
				audit.setEvent_name(up.getReport_desc());
				audit.setChange_details("Alert created successfully");
				audit.setAuth_user(inputUser);
				audit.setAuth_user_name(username);
				audit.setAuth_time(currentDate);

				// MODIFIED HERE
				audit.setRemarks(branchCode);

				KYC_Audit_Rep.save(audit);

				msg = "Added Successfully";
			}

			else if (formmode.equalsIgnoreCase("edit")) {
				Optional<AlertEntity> up = alertRep.findById(report_srl1);

				if (up.isPresent()) {
					AlertEntity entity = up.get();

					// Update fields
					entity.setReport_desc(alertEntity.getReport_desc());
					entity.setUser_id_1(alertEntity.getUser_id_1());
					entity.setUser_id_2(alertEntity.getUser_id_2());
					entity.setUser_id_3(alertEntity.getUser_id_3());
					entity.setUser_name_1(alertEntity.getUser_name_1());
					entity.setUser_name_2(alertEntity.getUser_name_2());
					entity.setUser_name_3(alertEntity.getUser_name_3());
					entity.setEmail_id_1(alertEntity.getEmail_id_1());
					entity.setEmail_id_2(alertEntity.getEmail_id_2());
					entity.setEmail_id_3(alertEntity.getEmail_id_3());
					entity.setMobile_no_1(alertEntity.getMobile_no_1());
					entity.setMobile_no_2(alertEntity.getMobile_no_2());
					entity.setMobile_no_3(alertEntity.getMobile_no_3());
					entity.setAlert_flg_1(alertEntity.getAlert_flg_1());
					entity.setAlert_flg_2(alertEntity.getAlert_flg_2());
					entity.setAlert_flg_3(alertEntity.getAlert_flg_3());

					alertRep.save(entity);

					// ---- Audit for Alert Update ----
					KYC_Audit_Entity audit = new KYC_Audit_Entity();
					audit.setAudit_ref_no(auditID);
					audit.setAudit_date(currentDate);
					audit.setEntry_time(currentDate);
					audit.setEntry_user(inputUser);
					audit.setEntry_user_name(username);
					audit.setFunc_code("UPDATE");
					audit.setAudit_table("USERPROFILETABLE");
					audit.setAudit_screen("ALERT_MANAGEMENT");
					audit.setEvent_id(entity.getReport_srl());
					audit.setEvent_name(entity.getReport_desc());
					audit.setChange_details("Alert updated successfully");
					audit.setAuth_user(inputUser);
					audit.setAuth_user_name(username);
					audit.setAuth_time(currentDate);

					// MODIFIED HERE
					audit.setRemarks(branchCode);

					KYC_Audit_Rep.save(audit);
				}

				msg = "Edited Successfully";
			}

			else if (formmode.equalsIgnoreCase("delete")) {
				Optional<AlertEntity> up = alertRep.findById(report_srl1);

				if (up.isPresent()) {
					alertRep.delete(up.get());

					// ---- Audit for Alert Delete ----
					KYC_Audit_Entity audit = new KYC_Audit_Entity();
					audit.setAudit_ref_no(auditID);
					audit.setAudit_date(currentDate);
					audit.setEntry_time(currentDate);
					audit.setEntry_user(inputUser);
					audit.setEntry_user_name(username);
					audit.setFunc_code("DELETE");
					audit.setAudit_table("USERPROFILETABLE");
					audit.setAudit_screen("ALERT_MANAGEMENT");
					audit.setEvent_id(up.get().getReport_srl());
					audit.setEvent_name(up.get().getReport_desc());
					audit.setChange_details("Alert deleted successfully");
					audit.setAuth_user(inputUser);
					audit.setAuth_user_name(username);
					audit.setAuth_time(currentDate);

					// MODIFIED HERE
					audit.setRemarks(branchCode);

					KYC_Audit_Rep.save(audit);
				}

				msg = "Deleted Successfully";
			}

		} catch (Exception e) {
			msg = "Error Occurred. Please contact Administrator";
			e.printStackTrace();
			logger.info(e.getMessage());
		}

		return msg;
	}

	public List<FinUserProfile> getFinUsersList() {

		Session hs = sessionFactory.getCurrentSession();
		return hs.createQuery("from FinUserProfile ", FinUserProfile.class).getResultList();

	}

	public Iterable<UserProfile> getUsersList(String ROLEIDAC) {
		Iterable<UserProfile> users = Collections.emptyList(); // Default fallback

		if (ROLEIDAC.equals("Superadmin")) {
			users = userProfileRep.findAll();
		} else if (ROLEIDAC.equals("DCD_ADMIN") || ROLEIDAC.equals("DCD_BRANCH")) {
			users = userProfileRep.getUsersListByecdd(); // Filtered list for ECDD roles
		} else if (ROLEIDAC.equals("RBR_ADMIN") || ROLEIDAC.equals("RBR_BRANCH")) {
			users = userProfileRep.getUsersListByrbr(); // Filtered list for RBR roles
		} else if (ROLEIDAC.equals("BRC")) {
			users = userProfileRep.getUsersListBybrc(); // Filtered list for BRC role
		}

		return users;
	}

	public Iterable<AlertEntity> getAlertList() {

		Iterable<AlertEntity> alerters = alertRep.findAll();

		return alerters;

	}

	public List<UserProfile> getUsersListone(String username) {
		Optional<UserProfile> users = userProfileRep.findById(username);
		System.out.println("The domain id is: " + users.get().getDomain_id());
		String[] dataArray = users.get().getDomain_id().split(",");
		List<String> item = new ArrayList<String>();
		List<UserProfile> itemw = new ArrayList<UserProfile>();
		for (String ss : dataArray) {
			item.add(ss);
		}
		// The entity object created for storing the separate domain id values
		UserProfile userProfile = new UserProfile();
		for (int i = 0; i < item.size(); i++) {
			if (i == 0) {
				userProfile.setUserid(item.get(i));
			} else if (i == 1) {
				userProfile.setUsername(item.get(i));
			} else if (i == 2) {
				userProfile.setBranch_name(item.get(i));
			} else if (i == 3) {
				userProfile.setBank_name(item.get(i));
			} else if (i == 4) {
				userProfile.setRole_desc(item.get(i));
			} else if (i == 5) {
				userProfile.setRole_id(item.get(i));
			}
			itemw.add(userProfile);
		}
		return itemw;
	}

	public UserProfile getUser(String id) {
		logger.info(id);
		if (userProfileRep.existsById(id)) {
			UserProfile up = userProfileRep.findById(id).get();
			logger.info(up.getEntity_flg());
			return up;
		} else {
			UserProfile UserProfile = new UserProfile();
			UserProfile.setLogin_low("09:00");
			UserProfile.setLogin_high("19:00");
			return UserProfile;
		}

	};

	public AlertEntity getAlerter(String id) {
		logger.info(id);
		if (alertRep.existsById(id)) {
			AlertEntity up = alertRep.findById(id).get();
			// logger.info(up.getEntity_flg());
			return up;
		} else {
			return new AlertEntity();
		}

	};

	public UserProfile getFinUser(String id) {
		logger.info(id);
		if (finUserProfileRep.existsById(id)) {

			DateFormat dateFormat = new SimpleDateFormat("hh:mm");

			FinUserProfile fup = finUserProfileRep.findById(id).get();
			UserProfile up = new UserProfile();

			up.setUserid(fup.getUserid());
			up.setUsername(fup.getFinGenEmpTb().getEmp_name());
			up.setEmpid(fup.getFinGenEmpTb().getEmp_id());
			up.setEmp_name(fup.getFinGenEmpTb().getEmp_name());
			up.setBranch_code(fup.getFinSolTb().getSdl_id());
			up.setBranch_name(fup.getFinSolTb().getSol_desc());
			up.setBank_code(fup.getFinSolTb().getBank_code());
			up.setBank_name(fup.getFinSolTb().getAbbr_bank_name());
			up.setEmail_id(fup.getFinGenEmpTb().getEmp_email_id());

			up.setInactive_time(fup.getUser_max_inactive_time().toString());
			up.setDisable_start_date(fup.getUser_disabled_from_date());
			up.setDisable_end_date(fup.getUser_disabled_upto_date());
			up.setAcc_exp_date(fup.getUser_acct_expy_date());
			up.setLogin_low(dateFormat.format(fup.getUser_login_time_low()));
			up.setLogin_high(dateFormat.format(fup.getUser_login_time_high()));

			return up;

		} else {

			return new UserProfile();
		}

	}

	public String verifyUser(UserProfile userProfile, String inputUser) {
		String msg = "";
		Date currentDate = new Date();
		String auditID = sequence.generateRequestUUId();
		String branchCode = (String) req.getSession().getAttribute("BRANCHCODE");

		Optional<UserProfile> up = userProfileRep.findById(userProfile.getUserid());

		try {
			if (up.isPresent()) {
				// Preserve existing details
				userProfile.setPassword(up.get().getPassword());
				userProfile.setPass_exp_date(up.get().getPass_exp_date());
				userProfile.setEmp_name(up.get().getEmp_name());
				userProfile.setLog_in_count(up.get().getLog_in_count());
				userProfile.setEntry_user(up.get().getEntry_user());
				userProfile.setEntry_time(up.get().getEntry_time());
				userProfile.setNo_of_attmp(0);
				userProfile.setEntity_flg("Y"); // Verified
				userProfile.setLogin_flg("N");
				userProfile.setAuth_user(inputUser);
				userProfile.setAuth_time(currentDate);

				// Save verified user
				userProfileRep.save(userProfile);

				// ---- Audit for User Verification ----
				KYC_Audit_Entity audit = new KYC_Audit_Entity();
				audit.setAudit_ref_no(auditID);
				audit.setAudit_date(currentDate);
				audit.setEntry_time(currentDate);
				audit.setEntry_user(inputUser);
				audit.setEntry_user_name(userProfile.getUsername()); // Verifying user name
				audit.setFunc_code("VERIFY");
				audit.setAudit_table("USERPROFILETABLE");
				audit.setAudit_screen("USER_MANAGEMENT");
				audit.setEvent_id(userProfile.getUserid()); // Verified user id
				audit.setEvent_name(userProfile.getUsername()); // Verified user name
				audit.setChange_details("User verified successfully");
				audit.setAuth_user(inputUser);
				audit.setAuth_user_name(userProfile.getUsername());
				audit.setAuth_time(currentDate);

				// MODIFIED HERE
				audit.setRemarks(branchCode);

				KYC_Audit_Rep.save(audit);

				msg = "User Verified Successfully";
			} else {
				msg = "User not found";
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
			msg = "Error Occurred. Please contact Administrator";
		}

		return msg;
	}

	public String passwordReset(UserProfile userprofile, String userid) {

		String msg = "";

		try {
			String encryptedPassword = PasswordEncryption.getEncryptedPassword(this.password);

			Optional<UserProfile> up = userProfileRep.findById(userprofile.getUserid());

			if (up.isPresent()) {
				UserProfile user = up.get();
				user.setPassword(encryptedPassword);
				user.setNo_of_attmp(0);
				user.setLogin_flg("N");
				user.setUser_locked_flg("N");
				userProfileRep.save(user);
			}

			msg = "Password Resetted Successfully";

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

			e.printStackTrace();

			msg = "Error Occured. Please contact Administrator";
		}

		return msg;
	}

	public String DefaultpasswordReset(UserProfile userprofile, String userid) {

		String msg = "";

		try {
			String encryptedPassword = PasswordEncryption.getEncryptedPassword("Welcome@123456");

			Optional<UserProfile> up = userProfileRep.findById(userprofile.getUserid());

			if (up.isPresent()) {
				UserProfile user = up.get();
				user.setPassword(encryptedPassword);
				user.setLog_in_count("1");
				user.setNo_of_attmp(0);
				user.setLogin_flg("N");
				user.setUser_locked_flg("N");
				userProfileRep.save(user);
			}

			msg = "Default Password Resetted Successfully";

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

			e.printStackTrace();

			msg = "Error Occured. Please contact Administrator";
		}

		return msg;
	}

	/*
	 * Getting LoginFlg -
	 * 
	 * If loginFlg = 'N' - User should be prompted to change password. else thats
	 * not required.
	 * 
	 * Loginflg ='N' will be updated at the time of new user creation and at the
	 * time of password reset by admin.
	 * 
	 */
	public String checkPasswordChangeReq(String userid) {

		Optional<UserProfile> up = userProfileRep.findById(userid);
		String loginflg = up.get().getLogin_flg();

		return loginflg;
	}

	public int checkAcctexpirty(String userid) {

		Optional<UserProfile> up = userProfileRep.findById(userid);
		Date expDate = up.get().getAcc_exp_date();
		Date currDate = new Date();

		DateTime dt1 = new DateTime(currDate);
		DateTime dt2 = new DateTime(expDate);

		int remaindays = Days.daysBetween(dt1, dt2).getDays();

		logger.info("Account Expired in:" + remaindays);
		return remaindays;
	}

	public int checkpassexpirty(String userid) {

		Optional<UserProfile> up = userProfileRep.findById(userid);
		Date expDate = up.get().getPass_exp_date();
		Date currDate = new Date();

		DateTime dt1 = new DateTime(currDate);
		DateTime dt2 = new DateTime(expDate);

		int remaindays = Days.daysBetween(dt1, dt2).getDays();

		logger.info("Password Expired in:" + remaindays);
		return remaindays;
	}

	public String changePassword(String oldpass, String newpass, String userid) {
		String msg = "";

		Optional<UserProfile> up = userProfileRep.findById(userid);

		try {
			if (up.isPresent()) {
				UserProfile user = up.get();
				if (PasswordEncryption.validatePassword(oldpass, user.getPassword())) {

					if (!PasswordEncryption.validatePassword(newpass, user.getPassword())) {

						String encryptedPassword = PasswordEncryption.getEncryptedPassword(newpass);
						user.setPassword(encryptedPassword);
						user.setLogin_flg("Y");

						LocalDateTime localDateTime = user.getPass_exp_date().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDateTime();
						user.setPass_exp_date(
								Date.from(localDateTime.plusDays(365).atZone(ZoneId.systemDefault()).toInstant()));

						userProfileRep.save(user);
						msg = "Password Changed Successfully";

					} else {

						msg = "New password cannot be Same as Old password";
					}

				} else {
					msg = "Incorrect Old Password!";
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			msg = "Error Occured. Please contact Administrator";
		}
		logger.info(msg);
		return msg;
	};

	public void SessionLogging(String menuname, String menuid, String sessionid, String userid, String ip,
			String status) {
		Session hs = sessionFactory.getCurrentSession();

		try {

			if (menuname.equals("LOGOUT")) {

				hs.createQuery("update XBRLSession set status='IN-ACTIVE' where session_id = ?1")
						.setParameter(1, sessionid).executeUpdate();

			} else {

				hs.save(new XBRLSession(menuname, menuid, sessionid, userid, ip, new Date(), status));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public File getUserLogFile(Date fromdate, Date todate) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

		String path = exportpath;
		String fileName = "USER_LOGS_" + dateFormat.format(new Date()) + ".xlsx";
		File outputFile;

		File jasperFile;

		File folders = new File(path);
		if (!folders.exists()) {
			folders.mkdirs();
		}

		try {
			jasperFile = ResourceUtils.getFile("classpath:static/jasper/USER_LOGS/UserLogs.jasper");
			JasperReport jr = (JasperReport) JRLoader.loadObject(jasperFile);
			HashMap<String, Object> map = new HashMap<String, Object>();

			logger.info("Assigning Parameters for Jasper");
			map.put("FromDate", dateFormat.format(fromdate));
			map.put("ToDate", dateFormat.format(todate));

			path = path + "/" + fileName;
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jp));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));
			exporter.exportReport();
			logger.info("Excel File exported");

		} catch (FileNotFoundException | JRException | SQLException e) {

			e.printStackTrace();
		}

		outputFile = new File(path);

		return outputFile;
	}

	public List<XBRLSession> getUserLog(Date fromdate, Date todate) {

		Session hs = sessionFactory.getCurrentSession();

		List<XBRLSession> ls = hs.createQuery(
				"from XBRLSession where trunc(entry_time,'DD') between ?1 and ?2 and menu in ('LOGIN','LOGOUT') order by entry_time desc ",
				XBRLSession.class).setParameter(1, fromdate).setParameter(2, todate).getResultList();

		return ls;
	}

	public String sendclientotp(String otp, String Roletype, UserProfile UserProfile) {

		logger.info("Start Sending OTP");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		Smsserviceotp Smsserviceotp = new Smsserviceotp();
		Smsserviceotp.setWrapperApiKey("LA6m0");
		Smsserviceotp.setSmssenderid("BOBAlert");
		Smsserviceotp.setSmsmobilenumber(UserProfile.getMob_number());
		Smsserviceotp.setSmstext(
				"BECDDS Login OTP: " + otp + " . Please do not share this with anyone. Valid for one-time use only.");
		Smsserviceotp.setToemail(UserProfile.getEmail_id());
		Smsserviceotp.setEmailsubject("ECDD OTP");
		Smsserviceotp.setEmailtemplateid("BOBAlert");
		Smsserviceotp.setEmailtext(
				"BECDDS Login OTP: " + otp + " . Please do not share this with anyone. Valid for one-time use only.");
		HttpEntity<Smsserviceotp> entity = new HttpEntity<>(Smsserviceotp, httpHeaders);

		// logger.info(entity.toString());
		ResponseEntity<String> response = null;
		try {
			logger.info("Ready to Call URL for OTP");
			response = restTemplate.postForEntity("https://wrap.smshub.live/api/APIWrapper", entity, String.class);
			logger.info(response.toString());

			if (response.getStatusCode() == HttpStatus.OK) {
				logger.info("Send Successfully");
				return "Otpsendsuccessfully";
			} else {
				logger.info("Send Failed");
				return "Something went wrong at server end";
			}

		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				logger.info("Exception -" + ex.getMessage());
				return "Something went wrong at server end";

			} else {
				logger.info("Exception -" + ex.getMessage());
				return "Something went wrong at server end";
			}

		} catch (HttpServerErrorException ex) {
			logger.info("Exception -" + ex.getMessage());
			return "Something went wrong at server end";
		}

	}

}