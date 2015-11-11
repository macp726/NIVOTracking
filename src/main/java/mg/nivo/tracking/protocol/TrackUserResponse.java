package mg.nivo.tracking.protocol;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class TrackUserResponse {

	private List<TrackImeiUser> imeisUsers;
	private Error error;
	
	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public List<TrackImeiUser> getImeisUsers() {
		return imeisUsers;
	}

	public void setImeisUsers(List<TrackImeiUser> imeisUsers) {
		this.imeisUsers = imeisUsers;
	}
	
	
}
