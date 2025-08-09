package com.bornfire.xbrl.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ECDD_PROFILE_WORKING_DAILY_STATUS_REPORT")
public class Ecdd_profile_report_entity {	
	
	private String	branch_code;
	private String	profile_type;
	private BigDecimal	high_risk_completed;
	private BigDecimal	medium_risk_completed;
	private BigDecimal	low_risk_completed;
	private BigDecimal	high_risk_pending;
	private BigDecimal	medium_risk_pending;
	private BigDecimal	low_risk_pending;
	private BigDecimal	high_risk_non_atended;
	private BigDecimal	medium_risk_non_atended;
	private BigDecimal	low_risk_non_atended;
	private Date	report_date;
	@Id
	private BigDecimal	srl_no;
	public String getBranch_code() {
		return branch_code;
	}
	public void setBranch_code(String branch_code) {
		this.branch_code = branch_code;
	}
	public String getProfile_type() {
		return profile_type;
	}
	public void setProfile_type(String profile_type) {
		this.profile_type = profile_type;
	}
	public BigDecimal getHigh_risk_completed() {
		return high_risk_completed;
	}
	public void setHigh_risk_completed(BigDecimal high_risk_completed) {
		this.high_risk_completed = high_risk_completed;
	}
	public BigDecimal getMedium_risk_completed() {
		return medium_risk_completed;
	}
	public void setMedium_risk_completed(BigDecimal medium_risk_completed) {
		this.medium_risk_completed = medium_risk_completed;
	}
	public BigDecimal getLow_risk_completed() {
		return low_risk_completed;
	}
	public void setLow_risk_completed(BigDecimal low_risk_completed) {
		this.low_risk_completed = low_risk_completed;
	}
	public BigDecimal getHigh_risk_pending() {
		return high_risk_pending;
	}
	public void setHigh_risk_pending(BigDecimal high_risk_pending) {
		this.high_risk_pending = high_risk_pending;
	}
	public BigDecimal getMedium_risk_pending() {
		return medium_risk_pending;
	}
	public void setMedium_risk_pending(BigDecimal medium_risk_pending) {
		this.medium_risk_pending = medium_risk_pending;
	}
	public BigDecimal getLow_risk_pending() {
		return low_risk_pending;
	}
	public void setLow_risk_pending(BigDecimal low_risk_pending) {
		this.low_risk_pending = low_risk_pending;
	}
	public BigDecimal getHigh_risk_non_atended() {
		return high_risk_non_atended;
	}
	public void setHigh_risk_non_atended(BigDecimal high_risk_non_atended) {
		this.high_risk_non_atended = high_risk_non_atended;
	}
	public BigDecimal getMedium_risk_non_atended() {
		return medium_risk_non_atended;
	}
	public void setMedium_risk_non_atended(BigDecimal medium_risk_non_atended) {
		this.medium_risk_non_atended = medium_risk_non_atended;
	}
	public BigDecimal getLow_risk_non_atended() {
		return low_risk_non_atended;
	}
	public void setLow_risk_non_atended(BigDecimal low_risk_non_atended) {
		this.low_risk_non_atended = low_risk_non_atended;
	}
	public Date getReport_date() {
		return report_date;
	}
	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}
	public BigDecimal getSrl_no() {
		return srl_no;
	}
	public void setSrl_no(BigDecimal srl_no) {
		this.srl_no = srl_no;
	}
	public Ecdd_profile_report_entity(String branch_code, String profile_type, BigDecimal high_risk_completed,
			BigDecimal medium_risk_completed, BigDecimal low_risk_completed, BigDecimal high_risk_pending,
			BigDecimal medium_risk_pending, BigDecimal low_risk_pending, BigDecimal high_risk_non_atended,
			BigDecimal medium_risk_non_atended, BigDecimal low_risk_non_atended, Date report_date, BigDecimal srl_no) {
		super();
		this.branch_code = branch_code;
		this.profile_type = profile_type;
		this.high_risk_completed = high_risk_completed;
		this.medium_risk_completed = medium_risk_completed;
		this.low_risk_completed = low_risk_completed;
		this.high_risk_pending = high_risk_pending;
		this.medium_risk_pending = medium_risk_pending;
		this.low_risk_pending = low_risk_pending;
		this.high_risk_non_atended = high_risk_non_atended;
		this.medium_risk_non_atended = medium_risk_non_atended;
		this.low_risk_non_atended = low_risk_non_atended;
		this.report_date = report_date;
		this.srl_no = srl_no;
	}
	public Ecdd_profile_report_entity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	


}
