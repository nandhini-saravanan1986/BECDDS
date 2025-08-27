package com.bornfire.xbrl.entities.BECDDS;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * FINAL VERSION: This repository uses the correct query logic and column order for the view,
 * but uses the method names from Kyc_Corprate_Repo (getList, getBranchList, etc.)
 * so it works as a drop-in replacement in the existing controller.
 */
@Repository
public interface EcddIndividualProfileRepository extends JpaRepository<Ecdd_Individual_Profile_Entity, String> {

    /**
     * Reusable base query for Individual KYC, ordered perfectly for the Thymeleaf view.
     * Uses the day-based calculation (+365, etc.) as requested.
     */
    String INDIVIDUAL_KYC_QUERY_FOR_VIEW = "SELECT " +
            "p.CUSTOMER_ID, " +                // 0 -> Customer ID
            "p.SYSTEM_RISK, " +                 // 1 -> Risk Category
            "p.ECDD_DATE, " +                   // 2 -> Last ECDD Review Date
            "CASE " +
            "  WHEN p.SYSTEM_RISK = 'High' THEN p.ECDD_DATE + 365 " +
            "  WHEN p.SYSTEM_RISK = 'Medium' THEN p.ECDD_DATE + 1095 " +
            "  WHEN p.SYSTEM_RISK = 'Low' THEN p.ECDD_DATE + 1825 " +
            "  ELSE NULL " +
            "END AS NEXT_REVIEW_DATE, " +       // 3 -> Next Review Date
            "TRUNC(CASE " +
            "  WHEN p.SYSTEM_RISK = 'High' THEN p.ECDD_DATE + 365 " +
            "  WHEN p.SYSTEM_RISK = 'Medium' THEN p.ECDD_DATE + 1095 " +
            "  WHEN p.SYSTEM_RISK = 'Low' THEN p.ECDD_DATE + 1825 " +
            "  ELSE NULL " +
            "END) - TRUNC(SYSDATE) AS PENDING_DAYS, " + // 4 -> Pending Due Days
            "p.SRLNO, " +                       // 5 -> Serial Number (for link)
            "p.ENTITY_FLG, " +                    // 6 -> Auth Flag (for status)
            "p.MODIFY_FLG, " +                  // 7 -> Modify Flag (for status)
            "p.ACCOUNT_TITLE, " +               // 8 -> Account Title
            "p.ACCOUNT_OPEN_DATE, " +           // 9 -> Account Opening Date
            "p.ASSOCIATED_ACCOUNTS " +          // 10 -> Account Number (for commented-out column)
            "FROM ECDD_INDIV_PROFILE p WHERE del_flg = 'N'";

    // --- Methods renamed to match your controller's expectations ---

    /**
     * Replaces findAllIndividuals(). Corresponds to getList() in the corporate repo.
     */
    @Query(value = INDIVIDUAL_KYC_QUERY_FOR_VIEW + " and p.dormant_flg='N'", nativeQuery = true)
    List<Object[]> findAllIndividuals();
    
    @Query(value = "SELECT  p.CUSTOMER_ID,p.SYSTEM_RISK,p.ECDD_DATE,CASE WHEN p.SYSTEM_RISK = 'High'   THEN p.ECDD_DATE + 365\r\n"
    		+ "        WHEN p.SYSTEM_RISK = 'Medium' THEN p.ECDD_DATE + 1095\r\n"
    		+ "        WHEN p.SYSTEM_RISK = 'Low'    THEN p.ECDD_DATE + 1825\r\n"
    		+ "        ELSE NULL\r\n"
    		+ "    END AS NEXT_REVIEW_DATE,                -- 3 -> Next Review Date\r\n"
    		+ "    \r\n"
    		+ "    TRUNC(\r\n"
    		+ "        CASE \r\n"
    		+ "            WHEN p.SYSTEM_RISK = 'High'   THEN p.ECDD_DATE + 365\r\n"
    		+ "            WHEN p.SYSTEM_RISK = 'Medium' THEN p.ECDD_DATE + 1095\r\n"
    		+ "            WHEN p.SYSTEM_RISK = 'Low'    THEN p.ECDD_DATE + 1825\r\n"
    		+ "            ELSE NULL\r\n"
    		+ "        END\r\n"
    		+ "    ) - TRUNC(SYSDATE) AS PENDING_DAYS,   \r\n"
    		+ "    p.SRLNO,   p.ENTITY_FLG, p.MODIFY_FLG, p.ACCOUNT_TITLE, p.ACCOUNT_OPEN_DATE,               \r\n"
    		+ "    p.ASSOCIATED_ACCOUNTS FROM  ECDD_INDIV_PROFILE p  WHERE p.del_flg = 'N' AND p.dormant_flg = 'Y'\r\n"
    		+ "", nativeQuery = true)
    List<Object[]> findAllDormantIndividuals();

    /**
     * Replaces findAllIndividualsByBranch(). Corresponds to getBranchList() in the corporate repo.
     */
    @Query(value = INDIVIDUAL_KYC_QUERY_FOR_VIEW + " AND p.BRANCH = ?1 and p.dormant_flg='N'", nativeQuery = true)
    List<Object[]> findAllIndividualsByBranch(String Branchcode);
    
    @Query(value = INDIVIDUAL_KYC_QUERY_FOR_VIEW + " p.BRANCH = ?1 and p.dormant_flg='Y'", nativeQuery = true)
    List<Object[]> findAllIndividualsdormantBranch(String Branchcode);

    /**
     * Replaces findFilteredIndividuals(). Corresponds to getDynamicValue() in the corporate repo.
     * NOTE: The parameter is named 'days' to match the old repo, even though your controller passes a variable named 'age'.
     */
    @Query(value = INDIVIDUAL_KYC_QUERY_FOR_VIEW + " AND p.SYSTEM_RISK = ?1 HAVING " +
            "TRUNC(CASE " +
            "  WHEN p.SYSTEM_RISK = 'High' THEN p.ECDD_DATE + 365 " +
            "  WHEN p.SYSTEM_RISK = 'Medium' THEN p.ECDD_DATE + 1095 " +
            "  WHEN p.SYSTEM_RISK = 'Low' THEN p.ECDD_DATE + 1825 " +
            "  ELSE NULL " +
            "END) - TRUNC(SYSDATE) <= ?2", nativeQuery = true)
    List<Object[]> findFilteredIndividuals(String customerRisk, Integer days);
    
    /**
     * Replaces findFilteredIndividualsByBranch(). Corresponds to getBranchDynamicValue() in the corporate repo.
     */
    @Query(value = INDIVIDUAL_KYC_QUERY_FOR_VIEW + " AND p.SYSTEM_RISK = 'High' AND p.BRANCH = ?3 HAVING " +
            "TRUNC(CASE " +
            "  WHEN p.SYSTEM_RISK = 'High' THEN p.ECDD_DATE + 365 " +
            "  WHEN p.SYSTEM_RISK = 'Medium' THEN p.ECDD_DATE + 1095 " +
            "  WHEN p.SYSTEM_RISK = 'Low' THEN p.ECDD_DATE + 1825 " +
            "  ELSE NULL " +
            "END) - TRUNC(SYSDATE) <= ?2", nativeQuery = true)
    List<Object[]> findFilteredIndividualsByBranch(String customerRisk, Integer days, String Branchcode);

    /**
     * Finds a single individual entity by its Serial Number.
     */
    @Query(value = "SELECT * FROM ECDD_INDIV_PROFILE WHERE SRLNO = ?1", nativeQuery = true)
    Ecdd_Individual_Profile_Entity GetUserBySrlNo(String srlno);
    
    @Query(value = "select NVL(count(*),0) from ECDD_INDIV_PROFILE where nvl(ENTITY_FLG,'N')='Y' and nvl(finacle_flg,'N')='Y' "
    		+ " and nvl(modify_flg,'N') = 'N' and dormant_flg='N'", nativeQuery = true)
	Integer Getfinacompletedcount();
    
    @Query(value = "select NVL(count(*),0) from ECDD_INDIV_PROFILE where nvl(ENTITY_FLG,'N')='Y' and nvl(finacle_flg,'Y')='N'"
    		+ " and nvl(modify_flg,'N')='N' and dormant_flg='N'", nativeQuery = true)
	Integer Getcompletedcount();

	@Query(value = "select NVL(count(*),0) from ECDD_INDIV_PROFILE where nvl(ENTITY_FLG,'N')='N'  and NVL(finacle_flg,'N')= 'N'"
			+ " and nvl(modify_flg,'N')='Y' and dormant_flg='N'", nativeQuery = true)
	Integer GetPendingcount();

	@Query(value = "select NVL(count(*),0) from ECDD_INDIV_PROFILE where nvl(ENTITY_FLG,'N')='N' and NVL(MODIFY_FLG,'N')= 'N'"
			+ " and NVL(finacle_flg,'N')= 'N' and dormant_flg='N'", nativeQuery = true)
	Integer GetUnattendcount();

	// Finacle Submitted (ECDD form submitted and date updated)
	@Query(value = "SELECT NVL(COUNT(*), 0) FROM ECDD_INDIV_PROFILE " +
	               "WHERE NVL(ENTITY_FLG,'N')='Y' AND NVL(FINACLE_FLG,'N')='Y' " +
	               "AND NVL(MODIFY_FLG,'N')='N' AND DORMANT_FLG='N' AND BRANCH=?1",
	       nativeQuery = true)
	Integer getBranchFinacleCompletedCount(String branch);

	// Verified (Completed)
	@Query(value = "SELECT NVL(COUNT(*), 0) FROM ECDD_INDIV_PROFILE " +
	               "WHERE NVL(ENTITY_FLG,'N')='Y' AND NVL(FINACLE_FLG,'Y')='N' " +
	               "AND NVL(MODIFY_FLG,'N')='N' AND DORMANT_FLG='N' AND BRANCH=?1",
	       nativeQuery = true)
	Integer getBranchWiseCompletedCount(String branch);

	// Pending (In-progress, modified but not yet verified)
	@Query(value = "SELECT NVL(COUNT(*), 0) FROM ECDD_INDIV_PROFILE " +
	               "WHERE NVL(ENTITY_FLG,'N')='N' AND NVL(FINACLE_FLG,'N')='N' " +
	               "AND NVL(MODIFY_FLG,'N')='Y' AND DORMANT_FLG='N' AND BRANCH=?1",
	       nativeQuery = true)
	Integer getBranchWisePendingCount(String branch);

	// Unattended (no action yet)
	@Query(value = "SELECT NVL(COUNT(*), 0) FROM ECDD_INDIV_PROFILE " +
	               "WHERE NVL(ENTITY_FLG,'N')='N' AND NVL(MODIFY_FLG,'N')='N' " +
	               "AND NVL(FINACLE_FLG,'N')='N' AND DORMANT_FLG='N' AND BRANCH=?1",
	       nativeQuery = true)
	Integer getBranchWiseUnattendedCount(String branch);

	
	@Query(value = "select DISTINCT nvl(branch,0) as branch ,COUNT(CUSTOMER_ID) from ECDD_INDIV_PROFILE where nvl(ENTITY_FLG,'N')='N' group by branch", nativeQuery = true)
	List<Object[]> GetbranchPendingcount();
	
	@Query(value = "SELECT *\r\n"
			+ "FROM (\r\n"
			+ "    SELECT branch, system_risk\r\n"
			+ "    FROM ecdd_indiv_profile\r\n"
			+ "    WHERE entity_flg = 'Y' AND modify_flg = 'N'\r\n"
			+ ")\r\n"
			+ "PIVOT (\r\n"
			+ "    COUNT(system_risk)\r\n"
			+ "    FOR system_risk IN ('Low' AS LOW_RISK, 'Medium' AS MEDIUM_RISK, 'High' AS HIGH_RISK)\r\n"
			+ ")\r\n"
			+ "ORDER BY branch\r\n"
			+ "", nativeQuery = true)
	List<Object[]> getstatuscount();
	
	@Query(value = "SELECT *\r\n"
			+ "FROM (\r\n"
			+ "    SELECT branch, system_risk\r\n"
			+ "    FROM ecdd_indiv_profile\r\n"
			+ "    WHERE entity_flg = 'N' AND modify_flg = 'Y'\r\n"
			+ ")\r\n"
			+ "PIVOT (\r\n"
			+ "    COUNT(system_risk)\r\n"
			+ "    FOR system_risk IN ('Low' AS LOW_RISK, 'Medium' AS MEDIUM_RISK, 'High' AS HIGH_RISK)\r\n"
			+ ")\r\n"
			+ "ORDER BY branch\r\n"
			+ "", nativeQuery = true)
	List<Object[]> getpendingstatuscount();
	
	@Query(value = "Select customer_id,account_title,branch,Case when FINACLE_FLG = 'Y' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Completed in finacle'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Verified in portal Fincale Pending'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'Y' THEN 'Working in Progress'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'N' THEN 'Unattended' End as \"ECDD STATUS\",modify_user,modify_time,\r\n"
			+ "			verify_user,verify_time,system_risk from ecdd_indiv_profile where DORMANT_FLG ='N'", nativeQuery = true)
	List<Object[]> GetEcddstatus();
	
	@Query(value = "Select customer_id,account_title,branch,Case when FINACLE_FLG = 'Y' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Completed in finacle'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'Y' AND modify_flg = 'N' Then 'Verified in portal Fincale Pending'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'Y' THEN 'Working in Progress'\r\n"
			+ "			when FINACLE_FLG = 'N' AND entity_flg = 'N' AND modify_flg = 'N' THEN 'Unattended' End as \"ECDD STATUS\",modify_user,modify_time,\r\n"
			+ "verify_user,verify_time,system_risk from ecdd_indiv_profile where DORMANT_FLG ='N' and branch=?1", nativeQuery = true)
	List<Object[]> GetEcddbranchstatus(String branch);
    
}