package com.bornfire.xbrl.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Kyc_Corprate_Repo extends JpaRepository<EcddCorporateEntity, String> {

	/// Get the Branchwise pending count for Dashboard Page
	@Query(value = "select DISTINCT nvl(BRANCH_CODE,0) as BRANCH_CODE ,COUNT(CUSTOMER_ID) from ECDD_CORPORATE_TABLE where nvl(ENTITY_FLG,'N')='N' group by BRANCH_CODE", nativeQuery = true)
	List<Object[]> GetbranchPendingcount();

	@Query(value = "select * from ECDD_CORPORATE_TABLE where SRL_NO = ?1 ", nativeQuery = true)
	List<EcddCorporateEntity> GetUser(String srl_no);

	@Query(value = "SELECT CUSTOMER_ID, COMPANY_NAME, ASSOCIATED_ACCOUNT_NUMBER, TRADE_LICENSE_NUMBER, TRADE_EXPIRY_DATE, SYSTEM_RISK, LATEST_RISK, ECDD_DATE,\r\n"
			+ "			CASE WHEN SYSTEM_RISK = 'High' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-365)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1095)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1825) END AS Age,\r\n"
			+ "                 CASE WHEN SYSTEM_RISK = 'High' THEN ECDD_DATE+365\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ECDD_DATE+1095\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ECDD_DATE+1825 END AS DUE_DATE,SRL_NO,MODIFY_FLG,ENTITY_FLG\r\n"
			+ "            FROM ECDD_CORPORATE_TABLE WHERE del_flg = 'N' AND ECDD_DATE IS NOT NULL and dormant_flg='N'", nativeQuery = true)
	List<Object[]> getList();

	@Query(value = "SELECT CUSTOMER_ID, COMPANY_NAME, ASSOCIATED_ACCOUNT_NUMBER, TRADE_LICENSE_NUMBER, TRADE_EXPIRY_DATE, SYSTEM_RISK, LATEST_RISK, ECDD_DATE,\r\n"
			+ "			CASE WHEN SYSTEM_RISK = 'High' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-365)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1095)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1825) END AS Age,\r\n"
			+ "                 CASE WHEN SYSTEM_RISK = 'High' THEN ECDD_DATE+365\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ECDD_DATE+1095\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ECDD_DATE+1825 END AS DUE_DATE,SRL_NO,MODIFY_FLG,ENTITY_FLG\r\n"
			+ "            FROM ECDD_CORPORATE_TABLE WHERE del_flg = 'N' AND ECDD_DATE IS NOT NULL and dormant_flg='Y'", nativeQuery = true)
	List<Object[]> Getdormantprofiles();

	//// Retrive Branch wise details
	@Query(value = "SELECT CUSTOMER_ID, COMPANY_NAME, ASSOCIATED_ACCOUNT_NUMBER, TRADE_LICENSE_NUMBER, TRADE_EXPIRY_DATE, SYSTEM_RISK, LATEST_RISK, ECDD_DATE,\r\n"
			+ "			CASE WHEN SYSTEM_RISK = 'High' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-365)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1095)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1825) END AS Age,\r\n"
			+ "                 CASE WHEN SYSTEM_RISK = 'High' THEN ECDD_DATE+365\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ECDD_DATE+1095\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ECDD_DATE+1825 END AS DUE_DATE,SRL_NO,MODIFY_FLG,ENTITY_FLG\r\n"
			+ "            FROM ECDD_CORPORATE_TABLE WHERE del_flg = 'N' AND ECDD_DATE IS NOT NULL AND BRANCH_CODE = ?1 "
			+ "and dormant_flg='N'", nativeQuery = true)
	List<Object[]> getBranchList(String Branchcode);

	@Query(value = "SELECT CUSTOMER_ID, COMPANY_NAME, ASSOCIATED_ACCOUNT_NUMBER, TRADE_LICENSE_NUMBER, TRADE_EXPIRY_DATE, SYSTEM_RISK, LATEST_RISK, ECDD_DATE,\r\n"
			+ "			CASE WHEN SYSTEM_RISK = 'High' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-365)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1095)\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ABS(ABS(FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE))-1825) END AS Age,\r\n"
			+ "                 CASE WHEN SYSTEM_RISK = 'High' THEN ECDD_DATE+365\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Medium' THEN ECDD_DATE+1095\r\n"
			+ "                 WHEN SYSTEM_RISK = 'Low' THEN ECDD_DATE+1825 END AS DUE_DATE,SRL_NO,MODIFY_FLG,ENTITY_FLG\r\n"
			+ "            FROM ECDD_CORPORATE_TABLE WHERE del_flg = 'N' AND ECDD_DATE IS NOT NULL AND BRANCH_CODE = ?1 "
			+ "and dormant_flg='Y'", nativeQuery = true)
	List<Object[]> Getdormantprofilesbranch(String Branchcode);

	@Query(value = "SELECT CUSTOMER_ID, COMPANY_NAME, ASSOCIATED_ACCOUNT_NUMBER, TRADE_LICENSE_NUMBER, TRADE_EXPIRY_DATE, SYSTEM_RISK, LATEST_RISK, ECDD_DATE, FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE) AS Age,SRL_NO,MODIFY_FLG,ENTITY_FLG FROM ECDD_CORPORATE_TABLE WHERE SYSTEM_RISK = ?1 AND FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE) <= ?2 AND del_flg = 'N'", nativeQuery = true)
	List<Object[]> getDynamicValue(String customerRisk, Integer days);

	//// Retrive Branch wise details
	@Query(value = "SELECT CUSTOMER_ID, COMPANY_NAME, ASSOCIATED_ACCOUNT_NUMBER, TRADE_LICENSE_NUMBER, TRADE_EXPIRY_DATE, SYSTEM_RISK, LATEST_RISK, ECDD_DATE, FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE) AS Age,SRL_NO,MODIFY_FLG,ENTITY_FLG FROM ECDD_CORPORATE_TABLE WHERE SYSTEM_RISK = ?1 AND FLOOR(NVL(ECDD_DATE, SYSDATE) - SYSDATE) <= ?2 AND del_flg = 'N' AND BRANCH_CODE = ?3", nativeQuery = true)
	List<Object[]> getBranchDynamicValue(String customerRisk, Integer days, String Branchcode);

////Overall Counts
	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='Y' "
			+ "  AND NVL(FINACLE_FLG,'N')='Y' " + "  AND NVL(MODIFY_FLG,'N')='N' "
			+ "  AND DORMANT_FLG='N'", nativeQuery = true)
	Integer getFinacleCompletedCount();

	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='Y' "
			+ "  AND NVL(FINACLE_FLG,'Y')='N' " + "  AND NVL(MODIFY_FLG,'N')='N' "
			+ "  AND DORMANT_FLG='N'", nativeQuery = true)
	Integer getCompletedCount();

	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='N' "
			+ "  AND NVL(FINACLE_FLG,'N')='N' " + "  AND NVL(MODIFY_FLG,'Y')='Y' "
			+ "  AND DORMANT_FLG='N'", nativeQuery = true)
	Integer getPendingCount();

	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='N' "
			+ "  AND NVL(FINACLE_FLG,'N')='N' " + "  AND NVL(MODIFY_FLG,'N')='N' "
			+ "  AND DORMANT_FLG='N'", nativeQuery = true)
	Integer getUnattendedCount();

////Branch wise counts
	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='Y' "
			+ "  AND NVL(FINACLE_FLG,'N')='Y' " + "  AND NVL(MODIFY_FLG,'N')='N' " + "  AND DORMANT_FLG='N' "
			+ "  AND BRANCH_CODE = ?1", nativeQuery = true)
	Integer getBranchFinacleCompletedCount(String branch);

	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='Y' "
			+ "  AND NVL(FINACLE_FLG,'Y')='N' " + "  AND NVL(MODIFY_FLG,'N')='N' " + "  AND DORMANT_FLG='N' "
			+ "  AND BRANCH_CODE = ?1", nativeQuery = true)
	Integer getBranchCompletedCount(String branch);

	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='N' "
			+ "  AND NVL(FINACLE_FLG,'N')='N' " + "  AND NVL(MODIFY_FLG,'Y')='Y' " + "  AND DORMANT_FLG='N' "
			+ "  AND BRANCH_CODE = ?1", nativeQuery = true)
	Integer getBranchPendingCount(String branch);

	@Query(value = "SELECT NVL(COUNT(*),0) " + "FROM ECDD_CORPORATE_TABLE " + "WHERE NVL(ENTITY_FLG,'N')='N' "
			+ "  AND NVL(FINACLE_FLG,'N')='N' " + "  AND NVL(MODIFY_FLG,'N')='N' " + "  AND DORMANT_FLG='N' "
			+ "  AND BRANCH_CODE = ?1", nativeQuery = true)
	Integer getBranchUnattendedCount(String branch);

	@Query(value = "SELECT *\r\n" + "FROM (\r\n" + "    SELECT branch_code, system_risk\r\n"
			+ "    FROM ECDD_CORPORATE_TABLE\r\n" + "    WHERE entity_flg = 'Y' AND modify_flg = 'N'\r\n" + ")\r\n"
			+ "PIVOT (\r\n" + "    COUNT(system_risk)\r\n"
			+ "    FOR system_risk IN ('Low' AS LOW_RISK, 'Medium' AS MEDIUM_RISK, 'High' AS HIGH_RISK)\r\n" + ")\r\n"
			+ "ORDER BY branch_code", nativeQuery = true)
	List<Object[]> getstatuscount();

	@Query(value = "SELECT *\r\n" + "FROM (\r\n" + "    SELECT branch_code, system_risk\r\n"
			+ "    FROM ECDD_CORPORATE_TABLE\r\n" + "    WHERE entity_flg = 'N' AND modify_flg = 'Y'\r\n" + ")\r\n"
			+ "PIVOT (\r\n" + "    COUNT(system_risk)\r\n"
			+ "    FOR system_risk IN ('Low' AS LOW_RISK, 'Medium' AS MEDIUM_RISK, 'High' AS HIGH_RISK)\r\n" + ")\r\n"
			+ "ORDER BY branch_code", nativeQuery = true)
	List<Object[]> getpendingstatuscount();

	@Query(value = "SELECT *\r\n" + "FROM (\r\n" + "    SELECT branch_code, system_risk\r\n"
			+ "    FROM ECDD_CORPORATE_TABLE\r\n" + "    WHERE entity_flg = 'N' AND modify_flg = 'N'\r\n" + ")\r\n"
			+ "PIVOT (\r\n" + "    COUNT(system_risk)\r\n"
			+ "    FOR system_risk IN ('Low' AS LOW_RISK, 'Medium' AS MEDIUM_RISK, 'High' AS HIGH_RISK)\r\n" + ")\r\n"
			+ "ORDER BY branch_code", nativeQuery = true)
	List<Object[]> getunattendstatuscount();

	@Query(value = "Select customer_id,company_name,branch_code,Case when FINACLE_FLG = 'Y' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Completed in finacle'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Verified in portal Fincale Pending'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'Y' THEN 'Working in Progress'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'N' THEN 'Unattended' End as \"ECDD STATUS\",modify_user,modify_time,\r\n"
			+ "verify_user,verify_time,system_risk from ecdd_corporate_table where DORMANT_FLG= 'N'", nativeQuery = true)
	List<Object[]> GetEcddstatusreport();
	
	@Query(value = "Select customer_id,company_name,branch_code,Case when FINACLE_FLG = 'Y' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Completed in finacle'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Verified in portal Fincale Pending'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'Y' THEN 'Working in Progress'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'N' THEN 'Unattended' End as \"ECDD STATUS\",modify_user,modify_time,\r\n"
			+ "verify_user,verify_time,system_risk from ecdd_corporate_table where DORMANT_FLG= 'N' and branch_code=?1", nativeQuery = true)
	List<Object[]> GetEcddbranchstatusreport(String branchcode);

}