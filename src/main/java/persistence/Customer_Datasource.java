package persistence;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Customer_Datasource")

public class Customer_Datasource {

	@Id
	private String customer_id;
	@Basic
	private String jndi_name;
	@Basic
	private String status;
	
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getJndi_name() {
		return jndi_name;
	}
	public void setJndi_name(String jndi_name) {
		this.jndi_name = jndi_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
