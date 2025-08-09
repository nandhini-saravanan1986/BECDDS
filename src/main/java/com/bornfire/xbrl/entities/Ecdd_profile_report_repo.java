package com.bornfire.xbrl.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Ecdd_profile_report_repo extends JpaRepository<Ecdd_profile_report_entity, BigDecimal> {
	
	@Query(value = "Select * from ecdd_profile_working_daily_status_report where profile_type = 'Corporate' and report_date =?1 ORDER BY BRANCH_CODE ASC", nativeQuery = true)
	List<Ecdd_profile_report_entity> getcorporatedata(Date Report_date);
	
	@Query(value = "Select * from ecdd_profile_working_daily_status_report where profile_type = 'Individual' and report_date =?1 ORDER BY BRANCH_CODE ASC", nativeQuery = true)
	List<Ecdd_profile_report_entity> getindividualdata(Date Report_date);

}
