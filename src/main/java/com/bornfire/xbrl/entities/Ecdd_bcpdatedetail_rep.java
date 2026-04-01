package com.bornfire.xbrl.entities;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Ecdd_bcpdatedetail_rep extends JpaRepository<Ecdd_bcpdatedetail, Date> {

	@Query(value = "Select * from ECDD_BCP_TABLES_LAST_UPDATED_DATE where report_date =?1", nativeQuery = true)
	Ecdd_bcpdatedetail Getdataforcurrentdate(Date Report_date);
	
}
