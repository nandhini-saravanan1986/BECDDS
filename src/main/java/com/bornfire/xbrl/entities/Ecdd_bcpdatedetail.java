package com.bornfire.xbrl.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ECDD_BCP_TABLES_LAST_UPDATED_DATE")
public class Ecdd_bcpdatedetail {
	@Id
	private Date	report_date;
	private Date	general_detail_table;
	private Date	coreinterface_detail_table;
	private Date	demographic_detail_table;
	private Date	address_detail_table;
	private Date	corporate_detail_table;
	public Date getReport_date() {
		return report_date;
	}
	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}
	public Date getGeneral_detail_table() {
		return general_detail_table;
	}
	public void setGeneral_detail_table(Date general_detail_table) {
		this.general_detail_table = general_detail_table;
	}
	public Date getCoreinterface_detail_table() {
		return coreinterface_detail_table;
	}
	public void setCoreinterface_detail_table(Date coreinterface_detail_table) {
		this.coreinterface_detail_table = coreinterface_detail_table;
	}
	public Date getDemographic_detail_table() {
		return demographic_detail_table;
	}
	public void setDemographic_detail_table(Date demographic_detail_table) {
		this.demographic_detail_table = demographic_detail_table;
	}
	public Date getAddress_detail_table() {
		return address_detail_table;
	}
	public void setAddress_detail_table(Date address_detail_table) {
		this.address_detail_table = address_detail_table;
	}
	public Date getCorporate_detail_table() {
		return corporate_detail_table;
	}
	public void setCorporate_detail_table(Date corporate_detail_table) {
		this.corporate_detail_table = corporate_detail_table;
	}
	@Override
	public String toString() {
		return "Ecdd_bcpdatedetail [report_date=" + report_date + ", general_detail_table=" + general_detail_table
				+ ", coreinterface_detail_table=" + coreinterface_detail_table + ", demographic_detail_table="
				+ demographic_detail_table + ", address_detail_table=" + address_detail_table
				+ ", corporate_detail_table=" + corporate_detail_table + "]";
	}
	public Ecdd_bcpdatedetail(Date report_date, Date general_detail_table, Date coreinterface_detail_table,
			Date demographic_detail_table, Date address_detail_table, Date corporate_detail_table) {
		super();
		this.report_date = report_date;
		this.general_detail_table = general_detail_table;
		this.coreinterface_detail_table = coreinterface_detail_table;
		this.demographic_detail_table = demographic_detail_table;
		this.address_detail_table = address_detail_table;
		this.corporate_detail_table = corporate_detail_table;
	}
	public Ecdd_bcpdatedetail() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
