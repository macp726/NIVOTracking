package mg.nivo.tracking.protocol;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TrackImeiDays {

	private String imei;
	private List<Date> days;
	private Error error;
	
	
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public List<Date> getDays() {
		return days;
	}
	public void setDays(List<Date> days) {
		this.days = days;
	}
	
	
	
	
	
}
