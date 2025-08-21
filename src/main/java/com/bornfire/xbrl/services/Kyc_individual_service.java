package com.bornfire.xbrl.services;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.KYC_Audit_Entity;
import com.bornfire.xbrl.entities.KYC_Audit_Rep;
import com.bornfire.xbrl.entities.Kyc_Repo;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BECDDS.EcddIndividualProfileRepository;
import com.bornfire.xbrl.entities.BECDDS.Ecdd_Individual_Profile_Entity;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Transactional
@Service
public class Kyc_individual_service {
	@Autowired
	Environment env;
	@Autowired
	DataSource srcdataSource;

	@Autowired
	private Kyc_Repo kyc_repo;
	@Autowired
	private HttpSession session;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	KYC_Audit_Rep KYC_Audit_Rep;

	@Autowired
	EcddIndividualProfileRepository ecddIndividualProfileRepository;

	/*
	 * public boolean updateKycData(String srl_no, KYC_I data, HttpServletRequest
	 * req) { Optional<KYC_I> optionalKyc = kyc_repo.findById(srl_no); String userId
	 * = (String) req.getSession().getAttribute("USERID"); String BRANCHCODE =
	 * (String) req.getSession().getAttribute("BRANCHCODE"); LocalDateTime
	 * currentDateTime = LocalDateTime.now();
	 * 
	 * if (optionalKyc.isPresent()) { KYC_I kycEntity = optionalKyc.get();
	 * Set<String> skipFields = new HashSet<>(Arrays.asList("entityFlg",
	 * "modifyFlg", "delFlg", "modifyUser", "verifyUser", "modifyTime",
	 * "verifyTime", "customerId"));
	 * 
	 * Map<String, String> changes = new LinkedHashMap<>();
	 * 
	 * kycEntity.setCustomer_id(data.getCustomer_id());
	 * kycEntity.setAccount_type(data.getAccount_type());
	 * kycEntity.setName(data.getName());
	 * kycEntity.setAccount_number(data.getAccount_number());
	 * kycEntity.setDate_of_birth(data.getDate_of_birth());
	 * kycEntity.setPlace_of_birth(data.getPlace_of_birth());
	 * kycEntity.setNationality(data.getNationality());
	 * kycEntity.setAccount_opening_date(data.getAccount_opening_date());
	 * kycEntity.setCountry_of_citizenship(data.getCountry_of_citizenship());
	 * kycEntity.setCountry_of_current_residency(data.
	 * getCountry_of_current_residency());
	 * kycEntity.setOccupation(data.getOccupation());
	 * kycEntity.setBusiness_activity(data.getBusiness_activity());
	 * kycEntity.setAnnual_income(data.getAnnual_income());
	 * kycEntity.setSource_of_funds(data.getSource_of_funds());
	 * kycEntity.setPurpose_of_account_opening(data.getPurpose_of_account_opening())
	 * ; kycEntity.setTax_registration(data.getTax_registration());
	 * kycEntity.setTax_id_number(data.getTax_id_number());
	 * kycEntity.setPrimary_address(data.getPrimary_address());
	 * kycEntity.setPrimary_address_country(data.getPrimary_address_country());
	 * kycEntity.setPrimary_address_city(data.getPrimary_address_city());
	 * kycEntity.setPrimary_address_po_box(data.getPrimary_address_po_box());
	 * kycEntity.setMobile_number(data.getMobile_number());
	 * kycEntity.setPrimary_telephone(data.getPrimary_telephone());
	 * kycEntity.setSecondary_telephone(data.getSecondary_telephone());
	 * kycEntity.setEmail_id(data.getEmail_id());
	 * kycEntity.setResidential_status_changed(data.getResidential_status_changed())
	 * ; kycEntity.setNew_country_of_residency(data.getNew_country_of_residency());
	 * kycEntity.setNew_city_of_residency(data.getNew_city_of_residency());
	 * kycEntity.setNew_po_box_of_residency(data.getNew_po_box_of_residency());
	 * kycEntity.setAccount_satisfactory(data.getAccount_satisfactory());
	 * kycEntity.setTransaction_commensurate(data.getTransaction_commensurate());
	 * kycEntity.setHigh_value_transactions_observed(data.
	 * getHigh_value_transactions_observed());
	 * kycEntity.setHigh_value_transactions_details1(data.
	 * getHigh_value_transactions_details1());
	 * kycEntity.setSuspicion_observed(data.getSuspicion_observed());
	 * kycEntity.setSuspicion_observed_details(data.getSuspicion_observed_details())
	 * ; kycEntity.setBranch_satisfied_with_transactions(data.
	 * getBranch_satisfied_with_transactions());
	 * kycEntity.setSupporting_document_obtained(data.
	 * getSupporting_document_obtained());
	 * kycEntity.setCurrent_turnover(data.getCurrent_turnover());
	 * kycEntity.setExpected_turnover(data.getExpected_turnover());
	 * kycEntity.setExpected_transaction_types(data.getExpected_transaction_types())
	 * ; kycEntity.setTransaction_frequency(data.getTransaction_frequency());
	 * kycEntity.setExpected_transaction_volume(data.getExpected_transaction_volume(
	 * )); kycEntity.setKnown_countries_of_transaction_1(data.
	 * getKnown_countries_of_transaction_1());
	 * kycEntity.setKnown_countries_of_transaction_2(data.
	 * getKnown_countries_of_transaction_2());
	 * kycEntity.setKnown_countries_of_transaction_3(data.
	 * getKnown_countries_of_transaction_3());
	 * kycEntity.setKnown_countries_of_transaction_4(data.
	 * getKnown_countries_of_transaction_4()); kycEntity.setUae(data.getUae());
	 * kycEntity.setUn(data.getUn()); kycEntity.setOfac(data.getOfac());
	 * kycEntity.setHmt(data.getHmt()); kycEntity.setEu(data.getEu());
	 * kycEntity.setOthers(data.getOthers());
	 * kycEntity.setCbu_check_done(data.getCbu_check_done());
	 * kycEntity.setGoogle_media_search(data.getGoogle_media_search());
	 * kycEntity.setInternal_deny_list_screening(data.
	 * getInternal_deny_list_screening());
	 * kycEntity.setSupporting_document_obtained_2(data.
	 * getSupporting_document_obtained_2()); kycEntity.setIs_pep(data.getIs_pep());
	 * kycEntity.setSenior_management_approval(data.getSenior_management_approval())
	 * ; kycEntity.setForeign_currency_request(data.getForeign_currency_request());
	 * kycEntity.setSenior_management_approval_fc(data.
	 * getSenior_management_approval_fc());
	 * kycEntity.setCustomer_risk(data.getCustomer_risk());
	 * kycEntity.setHigh_risk_reason(data.getHigh_risk_reason());
	 * kycEntity.setFurther_due_diligence(data.getFurther_due_diligence());
	 * kycEntity.setObservations_of_bank_official(data.
	 * getObservations_of_bank_official());
	 * kycEntity.setAccount_opening_officer_signature(data.
	 * getAccount_opening_officer_signature());
	 * kycEntity.setAccount_opening_officer_name(data.
	 * getAccount_opening_officer_name());
	 * kycEntity.setAccount_opening_officer_designation(data.
	 * getAccount_opening_officer_designation());
	 * kycEntity.setAccount_opening_officer_date(data.
	 * getAccount_opening_officer_date());
	 * kycEntity.setBranch_official_signature(data.getBranch_official_designation())
	 * ; kycEntity.setBranch_official_name(data.getBranch_official_name());
	 * kycEntity.setBranch_official_signature(data.getBranch_official_signature());
	 * kycEntity.setBranch_official_name(data.getBranch_official_name());
	 * kycEntity.setBranch_official_designation(data.getBranch_official_designation(
	 * )); kycEntity.setBranch_official_date(data.getBranch_official_date());
	 * kycEntity.setDebit(data.getDebit()); kycEntity.setCredit(data.getCredit());
	 * kycEntity.setSuspicion_observed_1(data.getSuspicion_observed_1());
	 * kycEntity.setCountry_of_citizenship_others(data.
	 * getCountry_of_citizenship_others());
	 * kycEntity.setReason_for_red_flag_1(data.getReason_for_red_flag_1());
	 * kycEntity.setReason_for_red_flag_2(data.getReason_for_red_flag_2());
	 * kycEntity.setJoint_support_document_details(data.
	 * getJoint_support_document_details()); kycEntity.setBranch(data.getBranch());
	 * kycEntity.setLast_ecdd_date(data.getLast_ecdd_date());
	 * kycEntity.setAof_available(data.getAof_available());
	 * kycEntity.setAof_remarks(data.getAof_remarks());
	 * kycEntity.setFatca_crs_available(data.getFatca_crs_available());
	 * kycEntity.setFatca_crs_remarks(data.getFatca_crs_remarks());
	 * kycEntity.setSource_of_funds_available(data.getSource_of_funds_available());
	 * kycEntity.setSource_of_funds_remarks(data.getSource_of_funds_remarks());
	 * kycEntity.setObservations(data.getObservations());
	 * kycEntity.setCurrent_date(data.getCurrent_date());
	 * kycEntity.setJoint_holder1_name(data.getJoint_holder1_name());
	 * kycEntity.setJoint_holder1_address(data.getJoint_holder1_address());
	 * kycEntity.setJoint_holder1_address_country(data.
	 * getJoint_holder1_address_country());
	 * kycEntity.setJoint_holder1_address_city(data.getJoint_holder1_address_city())
	 * ; kycEntity.setJoint_holder1_address_po_box(data.
	 * getJoint_holder1_address_po_box());
	 * kycEntity.setJoint_holder1_mobile(data.getJoint_holder1_mobile());
	 * kycEntity.setJoint_holder1_primary_telephone(data.
	 * getJoint_holder1_primary_telephone());
	 * kycEntity.setJoint_holder1_secondary_telephone(data.
	 * getJoint_holder1_secondary_telephone());
	 * kycEntity.setJoint_holder1_email(data.getJoint_holder1_email());
	 * kycEntity.setJoint_holder1_residential_status_changed(data.
	 * getJoint_holder1_residential_status_changed());
	 * kycEntity.setJoint_holder1_new_country_of_residency(data.
	 * getJoint_holder1_new_country_of_residency());
	 * kycEntity.setJoint_holder1_new_city_of_residency(data.
	 * getJoint_holder1_new_city_of_residency());
	 * kycEntity.setJoint_holder1_new_po_box_of_residency(data.
	 * getJoint_holder1_new_po_box_of_residency());
	 * kycEntity.setJoint_holder2_name(data.getJoint_holder2_name());
	 * kycEntity.setJoint_holder2_address(data.getJoint_holder2_address());
	 * kycEntity.setJoint_holder2_address_country(data.
	 * getJoint_holder2_address_country());
	 * kycEntity.setJoint_holder2_address_city(data.getJoint_holder2_address_city())
	 * ; kycEntity.setJoint_holder2_address_po_box(data.
	 * getJoint_holder2_address_po_box());
	 * kycEntity.setJoint_holder2_mobile(data.getJoint_holder2_mobile());
	 * kycEntity.setJoint_holder2_primary_telephone(data.
	 * getJoint_holder2_primary_telephone());
	 * kycEntity.setJoint_holder2_secondary_telephone(data.
	 * getJoint_holder2_secondary_telephone());
	 * kycEntity.setJoint_holder2_email(data.getJoint_holder2_email());
	 * kycEntity.setJoint_holder2_residential_status_changed(data.
	 * getJoint_holder2_residential_status_changed());
	 * kycEntity.setJoint_holder2_new_city_of_residency(data.
	 * getJoint_holder2_new_city_of_residency());
	 * kycEntity.setJoint_holder2_new_country_of_residency(data.
	 * getJoint_holder2_new_country_of_residency());
	 * kycEntity.setJoint_holder2_new_po_box_of_residency(data.
	 * getJoint_holder2_new_po_box_of_residency());
	 * kycEntity.setPrimary_account_holder_nationality(data.
	 * getPrimary_account_holder_nationality());
	 * kycEntity.setJoint_account_holder_1_nationality(data.
	 * getJoint_account_holder_1_nationality());
	 * kycEntity.setJoint_account_holder_2_nationality(data.
	 * getJoint_account_holder_2_nationality());
	 * kycEntity.setPrimary_account_holder_passport(data.
	 * getPrimary_account_holder_passport());
	 * kycEntity.setJoint_account_holder_1_passport(data.
	 * getJoint_account_holder_1_passport());
	 * kycEntity.setJoint_account_holder_2_passport(data.
	 * getJoint_account_holder_2_passport());
	 * kycEntity.setPrimary_account_holder_visa_eid(data.
	 * getPrimary_account_holder_visa_eid());
	 * kycEntity.setJoint_account_holder_1_visa_eid(data.
	 * getJoint_account_holder_1_visa_eid());
	 * kycEntity.setJoint_account_holder_2_visa_eid(data.
	 * getJoint_account_holder_2_visa_eid());
	 * kycEntity.setPrimary_account_holder_valid_residence(data.
	 * getPrimary_account_holder_valid_residence());
	 * kycEntity.setJoint_account_holder_1_valid_residence(data.
	 * getJoint_account_holder_1_valid_residence());
	 * kycEntity.setJoint_account_holder_2_valid_residence(data.
	 * getJoint_account_holder_2_valid_residence());
	 * kycEntity.setPrimary_account_holder_proof_source_income(
	 * data.getPrimary_account_holder_proof_source_income());
	 * kycEntity.setJoint_account_holder_1_proof_source_income(
	 * data.getJoint_account_holder_1_proof_source_income());
	 * kycEntity.setJoint_account_holder_2_proof_source_income(
	 * data.getJoint_account_holder_2_proof_source_income());
	 * kycEntity.setLinked_account1_name(data.getLinked_account1_name());
	 * kycEntity.setLinked_account1_number(data.getLinked_account1_number());
	 * kycEntity.setLinked_account1_type(data.getLinked_account1_type());
	 * kycEntity.setLinked_account1_opening_date(data.
	 * getLinked_account1_opening_date());
	 * kycEntity.setLinked_account1_currency(data.getLinked_account1_currency());
	 * kycEntity.setLinked_account1_status(data.getLinked_account1_status());
	 * kycEntity.setLinked_account2_name(data.getLinked_account2_name());
	 * kycEntity.setLinked_account2_number(data.getLinked_account2_number());
	 * kycEntity.setLinked_account2_type(data.getLinked_account2_type());
	 * kycEntity.setLinked_account2_opening_date(data.
	 * getLinked_account2_opening_date());
	 * kycEntity.setLinked_account2_currency(data.getLinked_account2_currency());
	 * kycEntity.setLinked_account2_status(data.getLinked_account2_status());
	 * kycEntity.setLinked_account3_name(data.getLinked_account3_name());
	 * kycEntity.setLinked_account3_number(data.getLinked_account3_number());
	 * kycEntity.setLinked_account3_type(data.getLinked_account3_type());
	 * kycEntity.setLinked_account3_opening_date(data.
	 * getLinked_account3_opening_date());
	 * kycEntity.setLinked_account3_currency(data.getLinked_account3_currency());
	 * kycEntity.setLinked_account3_status(data.getLinked_account3_status());
	 * kycEntity.setLinked_account4_name(data.getLinked_account4_name());
	 * kycEntity.setLinked_account4_number(data.getLinked_account4_number());
	 * kycEntity.setLinked_account4_type(data.getLinked_account4_type());
	 * kycEntity.setLinked_account4_opening_date(data.
	 * getLinked_account4_opening_date());
	 * kycEntity.setLinked_account4_currency(data.getLinked_account4_currency());
	 * kycEntity.setLinked_account4_status(data.getLinked_account4_status());
	 * kycEntity.setHigh_value_transactions_details2(data.
	 * getHigh_value_transactions_details2());
	 * kycEntity.setHigh_value_transactions_details3(data.
	 * getHigh_value_transactions_details3());
	 * kycEntity.setHigh_value_transactions_details4(data.
	 * getHigh_value_transactions_details4());
	 * kycEntity.setHigh_value_transactions_observed(data.
	 * getHigh_value_transactions_observed());
	 * kycEntity.setOther_expected_countries_1(data.getOther_expected_countries_1())
	 * ;
	 * kycEntity.setOther_expected_countries_2(data.getOther_expected_countries_2())
	 * ;
	 * kycEntity.setOther_expected_countries_3(data.getOther_expected_countries_3())
	 * ;
	 * kycEntity.setOther_expected_countries_4(data.getOther_expected_countries_4())
	 * ; kycEntity.setJoint_uae(data.getJoint_uae());
	 * kycEntity.setJoint_un(data.getJoint_un());
	 * kycEntity.setJoint_ofac(data.getJoint_ofac());
	 * kycEntity.setJoint_hmt(data.getJoint_hmt());
	 * kycEntity.setJoint_eu(data.getJoint_eu());
	 * kycEntity.setJoint_others(data.getJoint_others());
	 * kycEntity.setJoint_cbu_check_done(data.getJoint_cbu_check_done());
	 * kycEntity.setJoint_google_media_search(data.getJoint_google_media_search());
	 * kycEntity.setJoint_internal_deny_list_screening(data.
	 * getJoint_internal_deny_list_screening());
	 * kycEntity.setJoint_suspicion_observed(data.getJoint_suspicion_observed());
	 * kycEntity.setJoint_supporting_document_obtained(data.
	 * getJoint_supporting_document_obtained());
	 * kycEntity.setSupporting_document_obtained(data.
	 * getSupporting_document_obtained()); kycEntity.setModify_flg("Y");
	 * kycEntity.setEntity_flg("N"); kycEntity.setModify_user(userId);
	 * kycEntity.setKnown_countries_of_transaction_5(data.
	 * getKnown_countries_of_transaction_5());
	 * kycEntity.setKnown_countries_of_transaction_6(data.
	 * getKnown_countries_of_transaction_6());
	 * kycEntity.setKnown_countries_of_transaction_7(data.
	 * getKnown_countries_of_transaction_7());
	 * kycEntity.setKnown_countries_of_transaction_8(data.
	 * getKnown_countries_of_transaction_8());
	 * 
	 * kycEntity.setOther_expected_countries_5(data.getOther_expected_countries_5())
	 * ;
	 * kycEntity.setOther_expected_countries_6(data.getOther_expected_countries_6())
	 * ;
	 * kycEntity.setOther_expected_countries_7(data.getOther_expected_countries_7())
	 * ;
	 * kycEntity.setOther_expected_countries_8(data.getOther_expected_countries_8())
	 * ;
	 * 
	 * kycEntity.setAddinfo_primaryaddress(data.getAddinfo_primaryaddress());
	 * kycEntity.setJoint_holder1_primaryaddress(data.
	 * getJoint_holder1_primaryaddress());
	 * kycEntity.setJoint_holder2_primaryaddress(data.
	 * getJoint_holder2_primaryaddress());
	 * kycEntity.setPrimary_dow_jones(data.getPrimary_dow_jones());
	 * kycEntity.setJoint_dow_jones(data.getJoint_dow_jones());
	 * 
	 * kycEntity.setModify_time(Date.from(currentDateTime.atZone(ZoneId.
	 * systemDefault()).toInstant())); kycEntity.setBranch(BRANCHCODE);
	 * kyc_repo.save(kycEntity); String auditID = sequence.generateRequestUUId();
	 * String user1 = (String) req.getSession().getAttribute("USERID"); String
	 * username = (String) req.getSession().getAttribute("USERNAME");
	 * 
	 * KYC_Audit_Entity audit = new KYC_Audit_Entity(); Date currentDate = new
	 * Date(); audit.setAudit_date(currentDate); audit.setEntry_time(currentDate);
	 * audit.setEntry_user(user1); audit.setEntry_user_name(username);
	 * audit.setFunc_code("MODIFIED"); audit.setAudit_table("KYC_indidual");
	 * audit.setAudit_screen("MODIFY"); audit.setEvent_id(user1);
	 * audit.setEvent_name(username);
	 * audit.setModi_details("Modified Successfully"); StringBuilder changeDetails =
	 * new StringBuilder(); changes.forEach((field, value) ->
	 * changeDetails.append(field).append(": ").append(value).append("||| "));
	 * audit.setChange_details(changeDetails.toString());
	 * 
	 * audit.setReport_id(data.getCustomer_id()); audit.setAudit_ref_no(auditID);
	 * 
	 * KYC_Audit_Rep.save(audit);
	 * 
	 * return true; } else { return false; } }
	 */

	public Boolean verified(String Srl_no, HttpServletRequest req) {
		Optional<Ecdd_Individual_Profile_Entity> optionalKyc = ecddIndividualProfileRepository.findById(Srl_no);
		String userId = (String) session.getAttribute("USERID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		LocalDateTime currentDateTime = LocalDateTime.now();

		Optional<UserProfile> Userdetails = userProfileRep.findById(userId);
		if (optionalKyc.isPresent()) {

			Ecdd_Individual_Profile_Entity kycEntity = optionalKyc.get();

			String customerId = kycEntity.getCustomer_id();
			kycEntity.setApproval_date(new Date());
			kycEntity.setApproved_by_name(Userdetails.get().getUsername());
			kycEntity.setApproved_by_ec_no(Userdetails.get().getEmpid());
			kycEntity.setApproved_by_designation(
					Userdetails.get().getDesignation() != null ? Userdetails.get().getDesignation() : "");
			kycEntity.setModify_flg("N");
			kycEntity.setEntity_flg("Y");
			kycEntity.setVerify_user(userId);
			kycEntity.setVerify_time(Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			ecddIndividualProfileRepository.save(kycEntity);
			// Generate audit entry
			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");
			String branchcode = (String) req.getSession().getAttribute("BRANCHCODE");

			// Create and populate audit entity
			KYC_Audit_Entity audit = new KYC_Audit_Entity();
			Date currentDate = new Date();
			audit.setAudit_date(currentDate);
			audit.setEntry_time(currentDate);
			audit.setEntry_user(userId);
			audit.setEntry_user_name(username);
			audit.setAuth_user(user1);
			audit.setAuth_time(currentDate);
			audit.setAuth_user_name(username);
			audit.setFunc_code("Verified");
			audit.setAudit_table("KYC_individual");
			audit.setAudit_screen("Verify");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setRemarks(branchcode); // This now has the correct value
			audit.setReport_id(kycEntity.getCustomer_id());
			audit.setChange_details("Verify Successfully");
			audit.setAudit_ref_no(auditID);

			audit.setReport_id(customerId);

			// Save audit entity
			KYC_Audit_Rep.save(audit);
			return true;
		}
		return false;

	}

	public File GrtPdf(String Cust_Id) throws Exception {
		String path = env.getProperty("output.exportpath");
		String fileName = Cust_Id + ".pdf";
		File outputFile;

		try {
			// Build the output PDF path
			String fullPath = path + fileName;
			System.out.println("Generated file path: " + fullPath);

			// Load and compile the JRXML
			InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/KYC_Individual.jrxml");
			if (jasperFile == null) {
				throw new FileNotFoundException("Jasper template not found at /static/jasper/KYC_Individual.jrxml");
			}

			JasperReport jr = JasperCompileManager.compileReport(jasperFile);

			// Prepare parameters
			HashMap<String, Object> map = new HashMap<>();
			map.put("Customer_ID", Cust_Id);

			// ✅ Correctly resolve the image directory as String
			File imageDir = new ClassPathResource("static/images/").getFile();
			String imageDirPath = imageDir.getAbsolutePath() + File.separator;
			map.put("IMAGE_DIR", imageDirPath);

			System.out.println("Resolved IMAGE_DIR path: " + imageDirPath);

			// Fill and export report
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			JasperExportManager.exportReportToPdfFile(jp, fullPath);

			System.out.println("PDF generated successfully.");
			outputFile = new File(fullPath);

		} catch (Exception e) {
			System.err.println("Error generating PDF: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		return outputFile;
	}

	/*
	 * public boolean updateKycData(String srlno, Ecdd_Individual_Profile_Entity
	 * incomingData, HttpServletRequest req) { // Step 1: Find the existing record
	 * in the database.
	 * 
	 * // Generate audit entry String auditID = sequence.generateRequestUUId();
	 * String user1 = (String) req.getSession().getAttribute("USERID"); String
	 * username = (String) req.getSession().getAttribute("USERNAME");
	 * 
	 * Optional<Ecdd_Individual_Profile_Entity> optionalEntity =
	 * ecddIndividualProfileRepository.findById(srlno);
	 * 
	 * // This is the CORRECTED logic block if (optionalEntity.isPresent()) { // The
	 * record to be updated exists. Let's proceed.
	 * 
	 * // Get session attributes and current time for metadata. String userId =
	 * (String) req.getSession().getAttribute("USERID"); LocalDateTime
	 * currentDateTime = LocalDateTime.now(); Date modifyDate =
	 * Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
	 * 
	 * // Get the actual entity object from the database that we will modify.
	 * Ecdd_Individual_Profile_Entity existingEntity = optionalEntity.get();
	 * 
	 * Set<String> skipFields = new HashSet<>(Arrays.asList("CustomerId",
	 * "EntityFlg", "ModifyFlg", "DelFlg", "ModifyUser", "VerifyUser", "ModifyTime",
	 * "VerifyTime"));
	 * 
	 * Map<String, String> changes = new LinkedHashMap<>();
	 * 
	 * // --- General Account Information ---
	 * existingEntity.setAccount_title(incomingData.getAccount_title());
	 * existingEntity.setEcdd_date(incomingData.getEcdd_date());
	 * existingEntity.setCustomer_id(incomingData.getCustomer_id());
	 * existingEntity.setAssociated_accounts(incomingData.getAssociated_accounts());
	 * existingEntity.setCurrency(incomingData.getCurrency());
	 * existingEntity.setAccount_open_date(incomingData.getAccount_open_date());
	 * existingEntity.setCurrency_approval_yn(incomingData.getCurrency_approval_yn()
	 * );
	 * 
	 * // --- Primary Account Holder ---
	 * existingEntity.setPrimary_holder_name(incomingData.getPrimary_holder_name());
	 * existingEntity.setPrimary_customer_id(incomingData.getPrimary_customer_id());
	 * existingEntity.setPrimary_non_resident_yn(incomingData.
	 * getPrimary_non_resident_yn());
	 * existingEntity.setPrimary_nationality(incomingData.getPrimary_nationality());
	 * existingEntity.setPrimary_mobile_no(incomingData.getPrimary_mobile_no());
	 * existingEntity.setPrimary_email(incomingData.getPrimary_email());
	 * existingEntity.setPrimary_address(incomingData.getPrimary_address());
	 * existingEntity.setPrimary_passport_no(incomingData.getPrimary_passport_no());
	 * existingEntity.setPrimary_passport_exp_date(incomingData.
	 * getPrimary_passport_exp_date());
	 * existingEntity.setPrimary_emirates_id_no(incomingData.
	 * getPrimary_emirates_id_no());
	 * existingEntity.setPrimary_emirates_exp_date(incomingData.
	 * getPrimary_emirates_exp_date());
	 * existingEntity.setPrimary_pep_yn(incomingData.getPrimary_pep_yn());
	 * existingEntity.setPrimary_pep_approval(incomingData.getPrimary_pep_approval()
	 * );
	 * 
	 * // --- Joint Holder 1 ---
	 * existingEntity.setJoint1_name(incomingData.getJoint1_name());
	 * existingEntity.setJoint1_customer_id(incomingData.getJoint1_customer_id());
	 * existingEntity.setJoint1_non_resident_yn(incomingData.
	 * getJoint1_non_resident_yn());
	 * existingEntity.setJoint1_nationality(incomingData.getJoint1_nationality());
	 * existingEntity.setJoint1_mobile_no(incomingData.getJoint1_mobile_no());
	 * existingEntity.setJoint1_email(incomingData.getJoint1_email());
	 * existingEntity.setJoint1_address(incomingData.getJoint1_address());
	 * existingEntity.setJoint1_passport_no(incomingData.getJoint1_passport_no());
	 * existingEntity.setJoint1_passport_exp_date(incomingData.
	 * getJoint1_passport_exp_date());
	 * existingEntity.setJoint1_emirates_id_no(incomingData.getJoint1_emirates_id_no
	 * ()); existingEntity.setJoint1_emirates_exp_date(incomingData.
	 * getJoint1_emirates_exp_date());
	 * existingEntity.setJoint1_pep_yn(incomingData.getJoint1_pep_yn());
	 * existingEntity.setJoint1_pep_approval(incomingData.getJoint1_pep_approval());
	 * 
	 * // --- Joint Holder 2 ---
	 * existingEntity.setJoint2_name(incomingData.getJoint2_name());
	 * existingEntity.setJoint2_customer_id(incomingData.getJoint2_customer_id());
	 * existingEntity.setJoint2_non_resident_yn(incomingData.
	 * getJoint2_non_resident_yn());
	 * existingEntity.setJoint2_nationality(incomingData.getJoint2_nationality());
	 * existingEntity.setJoint2_mobile_no(incomingData.getJoint2_mobile_no());
	 * existingEntity.setJoint2_email(incomingData.getJoint2_email());
	 * existingEntity.setJoint2_address(incomingData.getJoint2_address());
	 * existingEntity.setJoint2_passport_no(incomingData.getJoint2_passport_no());
	 * existingEntity.setJoint2_passport_exp_date(incomingData.
	 * getJoint2_passport_exp_date());
	 * existingEntity.setJoint2_emirates_id_no(incomingData.getJoint2_emirates_id_no
	 * ()); existingEntity.setJoint2_emirates_exp_date(incomingData.
	 * getJoint2_emirates_exp_date());
	 * existingEntity.setJoint2_pep_yn(incomingData.getJoint2_pep_yn());
	 * existingEntity.setJoint2_pep_approval(incomingData.getJoint2_pep_approval());
	 * 
	 * // --- Joint Holder 3 ---
	 * existingEntity.setJoint3_name(incomingData.getJoint3_name());
	 * existingEntity.setJoint3_customer_id(incomingData.getJoint3_customer_id());
	 * existingEntity.setJoint3_non_resident_yn(incomingData.
	 * getJoint3_non_resident_yn());
	 * existingEntity.setJoint3_nationality(incomingData.getJoint3_nationality());
	 * existingEntity.setJoint3_mobile_no(incomingData.getJoint3_mobile_no());
	 * existingEntity.setJoint3_email(incomingData.getJoint3_email());
	 * existingEntity.setJoint3_address(incomingData.getJoint3_address());
	 * existingEntity.setJoint3_passport_no(incomingData.getJoint3_passport_no());
	 * existingEntity.setJoint3_passport_exp_date(incomingData.
	 * getJoint3_passport_exp_date());
	 * existingEntity.setJoint3_emirates_id_no(incomingData.getJoint3_emirates_id_no
	 * ()); existingEntity.setJoint3_emirates_exp_date(incomingData.
	 * getJoint3_emirates_exp_date());
	 * existingEntity.setJoint3_pep_yn(incomingData.getJoint3_pep_yn());
	 * existingEntity.setJoint3_pep_approval(incomingData.getJoint3_pep_approval());
	 * 
	 * // --- Risk, KYC, and Screening for all holders ---
	 * existingEntity.setKyc_valid_yn_primary(incomingData.getKyc_valid_yn_primary()
	 * );
	 * existingEntity.setAnnual_income_primary(incomingData.getAnnual_income_primary
	 * ()); existingEntity.setSource_of_income_primary(incomingData.
	 * getSource_of_income_primary());
	 * existingEntity.setScreen_google_primary(incomingData.getScreen_google_primary
	 * ()); existingEntity.setScreen_dowjones_primary(incomingData.
	 * getScreen_dowjones_primary());
	 * existingEntity.setKyc_valid_yn_joint1(incomingData.getKyc_valid_yn_joint1());
	 * existingEntity.setAnnual_income_joint1(incomingData.getAnnual_income_joint1()
	 * ); existingEntity.setSource_of_income_joint1(incomingData.
	 * getSource_of_income_joint1());
	 * existingEntity.setScreen_google_joint1(incomingData.getScreen_google_joint1()
	 * ); existingEntity.setScreen_dowjones_joint1(incomingData.
	 * getScreen_dowjones_joint1());
	 * existingEntity.setKyc_valid_yn_joint2(incomingData.getKyc_valid_yn_joint2());
	 * existingEntity.setAnnual_income_joint2(incomingData.getAnnual_income_joint2()
	 * ); existingEntity.setSource_of_income_joint2(incomingData.
	 * getSource_of_income_joint2());
	 * existingEntity.setScreen_google_joint2(incomingData.getScreen_google_joint2()
	 * ); existingEntity.setScreen_dowjones_joint2(incomingData.
	 * getScreen_dowjones_joint2());
	 * existingEntity.setKyc_valid_yn_joint3(incomingData.getKyc_valid_yn_joint3());
	 * existingEntity.setAnnual_income_joint3(incomingData.getAnnual_income_joint3()
	 * ); existingEntity.setSource_of_income_joint3(incomingData.
	 * getSource_of_income_joint3());
	 * existingEntity.setScreen_google_joint3(incomingData.getScreen_google_joint3()
	 * ); existingEntity.setScreen_dowjones_joint3(incomingData.
	 * getScreen_dowjones_joint3());
	 * 
	 * // --- Due Diligence & Transaction Monitoring ---
	 * existingEntity.setBranch_remarks(incomingData.getBranch_remarks());
	 * existingEntity.setUnusual_txn_details(incomingData.getUnusual_txn_details());
	 * existingEntity.setSuspicious_activity(incomingData.getSuspicious_activity());
	 * existingEntity.setHigh_value_txn_count(incomingData.getHigh_value_txn_count()
	 * );
	 * existingEntity.setHigh_value_txn_volume(incomingData.getHigh_value_txn_volume
	 * ());
	 * existingEntity.setFrequency_txn_percent(incomingData.getFrequency_txn_percent
	 * ()); existingEntity.setVolume_turnover_percent(incomingData.
	 * getVolume_turnover_percent());
	 * existingEntity.setCash_txn_count(incomingData.getCash_txn_count());
	 * existingEntity.setCash_txn_volume(incomingData.getCash_txn_volume());
	 * existingEntity.setCheque_txn_count(incomingData.getCheque_txn_count());
	 * existingEntity.setCheque_txn_volume(incomingData.getCheque_txn_volume());
	 * existingEntity.setLocal_txn_count(incomingData.getLocal_txn_count());
	 * existingEntity.setLocal_txn_volume(incomingData.getLocal_txn_volume());
	 * existingEntity.setIntl_txn_count(incomingData.getIntl_txn_count());
	 * existingEntity.setIntl_txn_volume(incomingData.getIntl_txn_volume());
	 * existingEntity.setCurr_txn_count(incomingData.getCurr_txn_count());
	 * existingEntity.setCurr_txn_volume(incomingData.getCurr_txn_volume());
	 * existingEntity.setExpected_txn_count(incomingData.getExpected_txn_count());
	 * existingEntity.setExpected_txn_volume(incomingData.getExpected_txn_volume());
	 * existingEntity.setProfile_match_yn(incomingData.getProfile_match_yn());
	 * existingEntity.setProfile_mismatch_remarks(incomingData.
	 * getProfile_mismatch_remarks());
	 * 
	 * // --- Risk Categorization ---
	 * existingEntity.setSystem_risk(incomingData.getSystem_risk());
	 * existingEntity.setCustomer_risk_reason(incomingData.getCustomer_risk_reason()
	 * );
	 * 
	 * // --- Document Availability in DMS ---
	 * existingEntity.setAof_available_yn(incomingData.getAof_available_yn());
	 * existingEntity.setAof_remarks(incomingData.getAof_remarks());
	 * existingEntity.setKyc_doc_available_yn(incomingData.getKyc_doc_available_yn()
	 * ); existingEntity.setKyc_doc_remarks(incomingData.getKyc_doc_remarks());
	 * existingEntity.setSource_of_funds_available_yn(incomingData.
	 * getSource_of_funds_available_yn());
	 * existingEntity.setSource_of_funds_remarks(incomingData.
	 * getSource_of_funds_remarks());
	 * 
	 * // --- Observations & Review ---
	 * 
	 * Optional<UserProfile> Userdetails = userProfileRep.findById(userId);
	 * 
	 * existingEntity.setBranch_observations(incomingData.getBranch_observations());
	 * existingEntity.setReview_date(incomingData.getReview_date());
	 * existingEntity.setBranch_name(incomingData.getBranch_name());
	 * System.out.println(Userdetails.get().getUsername());
	 * System.out.println(Userdetails.get().getEmail_id());
	 * existingEntity.setReviewed_by_name(Userdetails.get().getUsername());
	 * existingEntity.setReviewed_by_ec_no(Userdetails.get().getEmpid());
	 * existingEntity.setReviewed_by_designation(incomingData.
	 * getReviewed_by_designation());
	 * existingEntity.setApproval_date(incomingData.getApproval_date());
	 * existingEntity.setApproved_by_name(incomingData.getApproved_by_name());
	 * existingEntity.setApproved_by_ec_no(incomingData.getApproved_by_ec_no());
	 * existingEntity.setApproved_by_designation(incomingData.
	 * getApproved_by_designation());
	 * 
	 * // --- Finacle & DMS Data Entry ---
	 * existingEntity.setEntry_date(incomingData.getEntry_date());
	 * existingEntity.setEntered_by(incomingData.getEntered_by());
	 * existingEntity.setDoc_uploaded_date(incomingData.getDoc_uploaded_date());
	 * existingEntity.setDoc_uploaded_by(incomingData.getDoc_uploaded_by());
	 * existingEntity.setReport_date(incomingData.getReport_date());
	 * existingEntity.setSrlno(incomingData.getSrlno());
	 * 
	 * // Metadata / system fields existingEntity.setModify_flg("Y");
	 * existingEntity.setEntity_flg("N"); existingEntity.setModify_user(userId);
	 * existingEntity.setModify_time(Date.from(currentDateTime.atZone(ZoneId.
	 * systemDefault()).toInstant())); existingEntity.setAuth_flg("N");
	 * 
	 * ecddIndividualProfileRepository.save(existingEntity);
	 * 
	 * // Create and populate audit entity KYC_Audit_Entity audit = new
	 * KYC_Audit_Entity(); Date currentDate = new Date();
	 * audit.setAudit_date(currentDate); audit.setEntry_time(currentDate);
	 * audit.setEntry_user(user1); audit.setEntry_user_name(username);
	 * audit.setFunc_code("Modified"); audit.setAudit_table("Kyc_Individual");
	 * audit.setAudit_screen("Modify"); audit.setEvent_id(user1);
	 * audit.setEvent_name(username);
	 * audit.setModi_details("Modified Successfully");
	 * 
	 * // Append field changes to the audit details StringBuilder changeDetails =
	 * new StringBuilder(); // changes.forEach((field, value) ->
	 * changeDetails.append(field).append(": // ").append(value).append("||| "));
	 * audit.setChange_details(changeDetails.toString()); // New field in the audit
	 * table for storing changes
	 * 
	 * audit.setAudit_ref_no(auditID);
	 * 
	 * // Save audit entity KYC_Audit_Rep.save(audit); return true; } else {
	 * 
	 * return false; } }
	 * 
	 * public String uploadrelateddocs(MultipartFile Securityfile,String Userid) {
	 * return Userid;
	 * 
	 * }
	 */

	// ... your autowired repositories (ecddIndividualProfileRepository,
	// userProfileRep, KYC_Audit_Rep)
	// ... and sequence generator

	@Transactional
	public boolean updateKycData(String srlno, Ecdd_Individual_Profile_Entity incomingData, HttpServletRequest req) {

		Optional<Ecdd_Individual_Profile_Entity> optionalEntity = ecddIndividualProfileRepository.findById(srlno);
		if (!optionalEntity.isPresent()) {
			return false; // Record not found
		}
		Ecdd_Individual_Profile_Entity existingEntity = optionalEntity.get();

		String sectionId = req.getParameter("sectionId");
		// Check 'auth_flg' from the request as it's more reliable for AJAX calls
		final boolean isPartialSave = !"Y".equals(req.getParameter("auth_flg"));

		final Map<String, String> changes = new LinkedHashMap<>();
		final BeanWrapper src = new BeanWrapperImpl(incomingData);
		final BeanWrapper dest = new BeanWrapperImpl(existingEntity);

		// --- CORE FIX: Precise Update Logic ---
		if (isPartialSave && sectionId != null) {
			// This is a section-by-section save from the 'modify' page
			List<String> fieldsToUpdate = getFieldsForSection(sectionId);

			for (String fieldName : fieldsToUpdate) {
				Object newValue = src.getPropertyValue(fieldName);
				Object oldValue = dest.getPropertyValue(fieldName);

				// Record changes for audit trail
				if (!Objects.equals(oldValue, newValue)) {
					String formattedFieldName = formatFieldName(fieldName);
					String oldValStr = oldValue != null ? String.valueOf(oldValue) : "N/A";
					String newValStr = newValue != null ? String.valueOf(newValue) : ""; // Treat null new value as
																							// empty
					changes.put(formattedFieldName, "OldValue: " + oldValStr + ", NewValue: " + newValStr);
				}

				// Perform the update. This will correctly copy nulls and empty strings for the
				// specified fields.
				dest.setPropertyValue(fieldName, newValue);
			}

		} else {
			// This is the final 'Submit' from the main button.
			// Copy all non-null properties from the incoming data. This preserves existing
			// values
			// for fields that weren't part of the final submission form.
			String[] nullPropertyNames = getNullPropertyNames(incomingData);
			BeanUtils.copyProperties(incomingData, existingEntity, nullPropertyNames);
		}

		// --- Save Audit Trail ---
		if (isPartialSave) {
			createAndSaveAudit(existingEntity, changes, req);
		}

		// --- Set User Info and Metadata Flags ---
		String userId = (String) req.getSession().getAttribute("USERID");

		// This logic seems tied to the approver section, let's keep it.
		if (incomingData.getReviewed_by_designation() != null) {
			userProfileRep.findById(userId).ifPresent(userProfile -> {
				existingEntity.setReview_date(
						incomingData.getReview_date() != null ? incomingData.getReview_date() : new Date());
				existingEntity.setReviewed_by_name(userProfile.getUsername());
				existingEntity.setReviewed_by_ec_no(userProfile.getEmpid());
				existingEntity.setReviewed_by_designation(
						userProfile.getDesignation() != null ? userProfile.getDesignation() : "");
			});
		}

		if (!isPartialSave) {
			existingEntity.setEntity_flg("N");
			existingEntity.setAuth_flg("Y");
			existingEntity.setModify_flg("Y");
		} else {
			existingEntity.setEntity_flg("N");
			existingEntity.setAuth_flg("N");
			existingEntity.setModify_flg("N");
		}
		existingEntity.setModify_user(userId);
		existingEntity.setModify_time(new Date());

		ecddIndividualProfileRepository.save(existingEntity);
		return true;
	}

	private void createAndSaveAudit(Ecdd_Individual_Profile_Entity entity, Map<String, String> changes,
			HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");
		String branchcode = (String) req.getSession().getAttribute("BRANCHCODE");

		KYC_Audit_Entity audit = new KYC_Audit_Entity();
		Date currentDate = new Date();

		audit.setAudit_ref_no(sequence.generateRequestUUId());
		audit.setAudit_date(currentDate);
		audit.setEntry_time(currentDate);
		audit.setEntry_user(userId);
		audit.setEntry_user_name(username);
		audit.setFunc_code("Modified");
		audit.setAudit_table("KYC_individual");
		audit.setAudit_screen("Modify");
		audit.setModi_details("Modified section for SRL No: " + entity.getSrlno());
		audit.setAuth_user(userId);
		audit.setAuth_time(currentDate);
		audit.setAuth_user_name(username);
		audit.setRemarks(branchcode);
		audit.setReport_id(entity.getCustomer_id());

		if (changes.isEmpty()) {
			audit.setChange_details("No data fields were changed (metadata update only).");
		} else {
			StringBuilder changeDetails = new StringBuilder();
			changes.forEach((field, value) -> changeDetails.append(field).append(": ").append(value).append(" ||| "));
			if (changeDetails.length() > 5) {
				changeDetails.setLength(changeDetails.length() - 5);
			}
			audit.setChange_details(changeDetails.toString());
		}

		KYC_Audit_Rep.save(audit);
	}

	/**
	 * Maps a section ID from the front-end to a list of database field names (from
	 * your Entity). IMPORTANT: This list MUST be kept in sync with the input `name`
	 * attributes in your HTML file.
	 */
	private List<String> getFieldsForSection(String sectionId) {
		switch (sectionId) {
		case "s0_main_info":
			return Arrays.asList("account_title", "associated_accounts");
		case "s1_other_details":
			return Arrays.asList("currency", "account_open_date", "currency_approval_yn");
		case "s2_account_holders":
			return Arrays.asList("primary_holder_name", "joint1_name", "joint2_name", "joint3_name",
					"primary_customer_id", "joint1_customer_id", "joint2_customer_id", "joint3_customer_id",
					"primary_non_resident_yn", "joint1_non_resident_yn", "joint2_non_resident_yn",
					"joint3_non_resident_yn", "primary_nationality", "joint1_nationality", "joint2_nationality",
					"joint3_nationality", "primary_mobile_no", "joint1_mobile_no", "joint2_mobile_no",
					"joint3_mobile_no", "primary_email", "joint1_email", "joint2_email", "joint3_email",
					"primary_address", "joint1_address", "joint2_address", "joint3_address", "primary_passport_no",
					"joint1_passport_no", "joint2_passport_no", "joint3_passport_no", "primary_passport_exp_date",
					"joint1_passport_exp_date", "joint2_passport_exp_date", "joint3_passport_exp_date",
					"primary_emirates_id_no", "joint1_emirates_id_no", "joint2_emirates_id_no", "joint3_emirates_id_no",
					"primary_emirates_exp_date", "joint1_emirates_exp_date", "joint2_emirates_exp_date",
					"joint3_emirates_exp_date", "primary_pep_yn", "joint1_pep_yn", "joint2_pep_yn", "joint3_pep_yn",
					"primary_pep_approval", "joint1_pep_approval", "joint2_pep_approval", "joint3_pep_approval");
		case "s3_due_diligence_body":
			return Arrays.asList("screen_google_primary", "screen_dowjones_primary", "branch_remarks");
		case "s4_risk_assessment_body":
			return Arrays.asList("kyc_valid_yn_primary", "annual_income_primary", "source_of_income_primary");
		case "s5_txn_monitoring_body":
			return Arrays.asList("unusual_txn_details", "suspicious_activity", "high_value_txn_count",
					"high_value_txn_volume", "cash_txn_count", "cheque_txn_count", "local_txn_count", "intl_txn_count",
					"curr_txn_count", "expected_txn_count", "cash_txn_volume", "cheque_txn_volume", "local_txn_volume",
					"intl_txn_volume", "curr_txn_volume", "expected_txn_volume", "profile_match_yn",
					"profile_mismatch_remarks");
		case "s6_customer_risk":
			return Arrays.asList("system_risk", "customer_risk_reason");
		case "s7_docs_availability":
			return Arrays.asList("aof_available_yn", "aof_remarks", "kyc_doc_available_yn", "kyc_doc_remarks",
					"source_of_funds_available_yn", "source_of_funds_remarks");
		case "s8_observation":
			return Collections.singletonList("branch_observations");
		case "s9_approver":
			return Arrays.asList("review_date", "reviewed_by_name", "reviewed_by_ec_no", "reviewed_by_designation",
					"branch_name", "approval_date", "approved_by_name", "approved_by_ec_no", "approved_by_designation",
					"head_signature_name");
		case "s10_finacle_upload":
			return Arrays.asList("entry_date", "entered_by", "doc_uploaded_date", "doc_uploaded_by");
		default:
			// Return empty list if sectionId is unknown to prevent accidental updates
			return Collections.emptyList();
		}
	}

	/**
	 * Gets the names of all properties in the source object that are strictly null.
	 * This is safer than the old method because it DOES NOT ignore empty strings
	 * ("").
	 * 
	 * @param source The source object.
	 * @return An array of property names that are null.
	 */
	private String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> nullNames = new HashSet<>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) {
				nullNames.add(pd.getName());
			}
		}
		return nullNames.toArray(new String[0]);
	}

	/**
	 * Helper method to format a camelCase field name into a more readable format
	 * for the audit trail. e.g., "joint1CustomerName" becomes "Joint1 Customer
	 * Name"
	 */
	private String formatFieldName(String camelCaseString) {
		if (camelCaseString == null || camelCaseString.isEmpty()) {
			return "";
		}
		// Add space before capital letters, then capitalize the first letter.
		String result = camelCaseString.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
		return result.substring(0, 1).toUpperCase() + result.substring(1);
	}

}
