package com.bornfire.xbrl.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.XBRLSession;
import com.bornfire.xbrl.entities.BECCDS.Charge_Back_Rep;
import com.bornfire.xbrl.entities.BECCDS.MANUAL_Service_Rep;
import com.bornfire.xbrl.services.AlertManagementServices;
import com.bornfire.xbrl.services.CallStoredProcedure;
import com.bornfire.xbrl.services.Kyc_Corprate_service;
import com.bornfire.xbrl.services.Kyc_individual_service;
import com.bornfire.xbrl.services.LoginServices;

@RestController
@Component
public class XBRLRestController {

	private static final Logger logger = LoggerFactory.getLogger(XBRLRestController.class);

	@Autowired
	AlertManagementServices alertManagementServices;

	@Autowired
	LoginServices loginServices;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	Charge_Back_Rep charge_Back_Rep;

	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;

	@Autowired
	Kyc_individual_service Kyc_individual_service;

	@Autowired
	Kyc_Corprate_service Kyc_Corprate_service;

	@PersistenceContext
	private EntityManager entityManager;

	private final CallStoredProcedure callStoredProcedure = new CallStoredProcedure();

	@RequestMapping(value = "userlogList", method = RequestMethod.GET)
	public List<XBRLSession> userLogList(@RequestParam String fromdate, @RequestParam String todate) {

		Date fromdate2 = null;
		Date todate2 = null;

		try {
			fromdate2 = new SimpleDateFormat("dd-MM-yyyy").parse(fromdate);
			todate2 = new SimpleDateFormat("dd-MM-yyyy").parse(todate);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginServices.getUserLog(fromdate2, todate2);

	}

	@RequestMapping("/personalval")
	public List<Object[]> personalval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getper(cifId, acctNo);
		return result;
	}

	@RequestMapping("/addressval")
	public List<Object[]> addressval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getadres(cifId, acctNo);
		return result;
	}

	@RequestMapping("/tranval")
	public List<Object[]> tranval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.gettran(cifId, acctNo);
		System.out.println("Result size: " + result.size());
		return result;
	}

	@RequestMapping("/acctval")
	public List<Object[]> acctval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getacct(cifId, acctNo);
		return result;
	}

	@RequestMapping("/tradeval")
	public List<Object[]> tradeval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.gettrade(cifId, acctNo);
		return result;
	}

	@RequestMapping("/empval")
	public List<Object[]> empval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getemp(cifId, acctNo);
		return result;
	}

	@RequestMapping("/docval")
	public List<Object[]> docval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getdoc(cifId, acctNo);
		return result;
	}

	@RequestMapping("/signval")
	public List<Object[]> signval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getsign(cifId, acctNo);
		return result;
	}

	@RequestMapping("/associatedval")
	public List<Object[]> associatedval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getassociated(cifId, acctNo);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/jointsval")
	public List<Object[]> jointsval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getjoints(cifId, acctNo);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/photosval")
	public List<Object[]> photosval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getpics(cifId, acctNo);
		return result;
	}

	/*
	 * @RequestMapping("/documentval") public List<Object[]>
	 * documentval(@RequestParam("cifId") String cifId) {
	 * System.out.println("cifId: " + cifId); List<Object[]> result =
	 * charge_Back_Rep.getdoc(cifId); return result; }
	 */

	/* cust profile */
	/*
	 * =============================================================================
	 * ===
	 */

	@RequestMapping("/personval")
	public List<Object[]> personval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getpersonal(cifId);
		return result;
	}

	@RequestMapping("/adrsval")
	public List<Object[]> adrsval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getadress(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/tradval")
	public List<Object[]> tradval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.gettrad(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/employeval")
	public List<Object[]> employeeval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getemploye(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/documentval")
	public List<Object[]> documentval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getdocument(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/acctsval")
	public List<Object[]> acctsval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getaccts(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/transval")
	public List<Object[]> transval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.gettrans(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/photoval")
	public List<Object[]> photoval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);

		List<Object[]> result = charge_Back_Rep.getpic(cifId);
		return result;
	}

	@RequestMapping("/signatureval")
	public List<Object[]> signatureval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getsignature(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/jointval")
	public List<Object[]> jointval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getjoint(cifId);

		return result; // Make sure result contains the expected data
	}

	/*------------------Cust Profile -Trade finance flag--------------------*/

	@RequestMapping("/bankflgval")
	public List<Object[]> bankflgval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getbankflg(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/tradflgval")
	public List<Object[]> tradflgval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.gettradflg(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/letterofcreditval")
	public List<Object[]> letterofcreditval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getLetofcredit(cifId);

		return result; // Make sure result contains the expected data
	}

	/*---------------------Acct profile -Trade finance flag---------------------*/
	/*
	 * @RequestMapping("/bankflgsval") public List<Object[]>
	 * bankflgsval(@RequestParam("cifId") String cifId) {
	 * System.out.println("cifId: " + cifId); List<Object[]> result =
	 * charge_Back_Rep.getbankflag(cifId); return result; // Make sure result
	 * contains the expected data }
	 */

	@RequestMapping("/bankflgsval")
	public List<Object[]> bankflgsval(@RequestParam("cifId") String cifId, @RequestParam("acctNo") String acctNo) {
		System.out.println("cifId: " + cifId);
		System.out.println("acctNo: " + acctNo);
		List<Object[]> result = charge_Back_Rep.getbankflag(cifId, acctNo);
		return result;
	}

	@RequestMapping("/tradeflgval")
	public List<Object[]> tradeflgval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.gettradeflg(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/lettercreditval")
	public List<Object[]> lettercreditval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getLetofcreditS(cifId);

		return result; // Make sure result contains the expected data
	}

	@RequestMapping("/associateval")
	public List<Object[]> associateval(@RequestParam("cifId") String cifId) {
		System.out.println("cifId: " + cifId);
		List<Object[]> result = charge_Back_Rep.getassociate(cifId);

		return result; // Make sure result contains the expected data
	}

	@GetMapping("getcif")
	public List<Object[]> getcif(@RequestParam(required = false) String natIdNum) {
		List<Object[]> cifdetail = charge_Back_Rep.getcif(natIdNum);
		return cifdetail;
	}

	@GetMapping("getcifall")
	public List<Object[]> getcifall(@RequestParam(required = false) String natIdNum) {
		List<Object[]> cifdetail = charge_Back_Rep.getcifall(natIdNum);
		return cifdetail;
	}

	@GetMapping("getcifRetail")
	public List<Object[]> getcifRetail(@RequestParam(required = false) String natIdNum) {
		List<Object[]> cifdetail = charge_Back_Rep.getcifRetail(natIdNum);
		return cifdetail;
	}

	@GetMapping("getcif1")
	public List<Object[]> getcif1(@RequestParam(required = false) String natIdNum) {
		List<Object[]> cifdetail = charge_Back_Rep.getcif1(natIdNum);
		return cifdetail;
	}

	@GetMapping("getcifall1")
	public List<Object[]> getcifall1(@RequestParam(required = false) String natIdNum) {
		List<Object[]> cifdetail = charge_Back_Rep.getcifall1(natIdNum);
		return cifdetail;
	}

	@GetMapping("getcifRetail1")
	public List<Object[]> getcifRetail1(@RequestParam(required = false) String natIdNum) {
		List<Object[]> cifdetail = charge_Back_Rep.getcifRetail1(natIdNum);
		return cifdetail;
	}

}
