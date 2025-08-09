package com.bornfire.xbrl.entities.BECDDS;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.persistence.Id;

@Embeddable
public class AUD_SERVICES_SL_NO implements Serializable {
    @Id
    private BigDecimal	sl_no;
		public BigDecimal getSl_no() {
		return sl_no;
	}
	public void setSl_no(BigDecimal sl_no) {
		this.sl_no = sl_no;
	}
	
    
    
    
}
