package mg.nivo.tracking.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TrackImeiUser {

	private String imei;
	private String user;
	private String id_company;
	private boolean active;
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getId_company() {
		return id_company;
	}
	public void setId_company(String id_company) {
		this.id_company = id_company;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	} 

}
