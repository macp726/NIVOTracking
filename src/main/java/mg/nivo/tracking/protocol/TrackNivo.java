package mg.nivo.tracking.protocol;

import java.util.List;


public  class  TrackNivo  {

	public static class TrackingNivo{

		private List<Track> track;

		public static class Track{

			private String devId;
			private String end;
			private double lat;
			private double lon;
			private String imei;
			private String mac;
			private double accu;
			private double state;

			
			
			public double getState() {
				return state;
			}
			public void setState(double state) {
				this.state = state;
			}
			public double getAccu() {
				return accu;
			}
			public void setAccu(double accu) {
				this.accu = accu;
			}
			public String getDevId() {
				return devId;
			}
			public void setDevId(String devId) {
				this.devId = devId;
			}
			public String getEnd() {
				return end;
			}
			public void setEnd(String end) {
				this.end = end;
			}
			public double getLat() {
				return lat;
			}
			public void setLat(double lat) {
				this.lat = lat;
			}
			public double getLon() {
				return lon;
			}
			public void setLon(double lon) {
				this.lon = lon;
			}
			public String getImei() {
				return imei;
			}
			public void setImei(String imei) {
				this.imei = imei;
			}
			public String getMac() {
				return mac;
			}
			public void setMac(String mac) {
				this.mac = mac;
			}
		}

		public List<Track> getTrack() {
			return track;
		}

		public void setTrack(List<Track> track) {
			this.track = track;
		}

	}

	private TrackingNivo trackingNivo;

	public TrackingNivo getTrackingNivo() {
		return trackingNivo;
	}

	public void setTrackingNivo(TrackingNivo trackingNivo) {
		this.trackingNivo = trackingNivo;
	}
}


