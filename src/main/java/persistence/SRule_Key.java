package persistence;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Embeddable 
public class SRule_Key implements Serializable{


	private String group_id;

	private String rule_id;



	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getRule_id() {
		return rule_id;
	}

	public void setRule_id(String rule_id) {
		this.rule_id = rule_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group_id == null) ? 0 : group_id.hashCode());
		result = prime * result + ((rule_id == null) ? 0 : rule_id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SRule_Key other = (SRule_Key) obj;
		if (group_id == null) {
			if (other.group_id != null)
				return false;
		} else if (!group_id.equals(other.group_id))
			return false;
		if (rule_id == null) {
			if (other.rule_id != null)
				return false;
		} else if (!rule_id.equals(other.rule_id))
			return false;
		return true;
	}	



	

}
