package mg.nivo.tracking.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.nivo.tracking.dao.TrackingDAO;
import mg.nivo.tracking.protocol.Error;
import mg.nivo.tracking.protocol.ErrorType;
import mg.nivo.tracking.protocol.TrackImeiDays;
import mg.nivo.tracking.protocol.TrackImeiUser;
import mg.nivo.tracking.protocol.TrackUserResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracking")
public class TrackingRest {

	private static Logger log = LogManager.getLogger();
	private final static Crs CRS = new Crs();
	static {
		Map<String, Object> prop = Collections.singletonMap("name",
				(Object) "urn:ogc:def:crs:EPSG::4326");
		CRS.setProperties(prop);
	}

	@Autowired
	TrackingDAO dao;
	
	/**
	 * 
	 * @param clientId
	 * @return a GeoJson FeatureCollection
	 */
	@RequestMapping("/{clientId}/latest")
	public FeatureCollection getGeoJsonVisitList(@PathVariable String clientId) {
		try{
			log.debug("Latest request for {}", clientId);
			FeatureCollection resp = new FeatureCollection();
			resp.setCrs(CRS);
			List<Feature> features = new ArrayList<Feature>();
			features = dao.getLatestPoints(clientId);		
			resp.addAll(features);
			return resp;
		}
		catch (RuntimeException ex) {
			log.error("Failed to parse request", ex);
			throw new InvalidParameterException();
		}
	}

	/**
	 * 
	 * @param clientId
	 * @return TrackUserResponse
	 */
	@RequestMapping("/{clientId}")
	public TrackUserResponse getImeisUsers(@PathVariable String clientId) {
		try{
			log.debug("UsersImeis request for {}", clientId);		
			List<TrackImeiUser> imeisUsers = dao.getUsersImeis(clientId);
			TrackUserResponse tres = new TrackUserResponse();
			if(imeisUsers!=null)
				tres.setImeisUsers(imeisUsers);
			else{
				Error error = Error.build(ErrorType.invalidValue,"Por favor solicite el servicio de tracking con el administrador", "No existen usuarios para ese el clientId: ".concat(clientId));
				tres.setError(error);
			}
			return tres;
		}
		catch (RuntimeException ex) {
			log.error("Failed to parse request", ex);
			throw new InvalidParameterException();
		}
	}

	/**
	 * 
	 * @param clientId
	 * @param mobileId
	 * @return TrackImeiDays
	 */
	@RequestMapping("/{clientId}/{mobileId}/Days")
	public TrackImeiDays getDaysImei(@PathVariable String clientId,@PathVariable String mobileId) {
		try{
			TrackImeiDays imeiDays = new TrackImeiDays();
			log.debug("DaysImei request for {}", clientId.concat(" Imei: ").concat(mobileId));
			imeiDays =dao.getDaysImei(clientId,mobileId);
			if(imeiDays!=null)
				return imeiDays;
			else{
				Error error = Error.build(ErrorType.invalidValue,"Por favor solicite el servicio de tracking con el administrador", "No existen usuarios para ese el clientId: ".concat(clientId));
				imeiDays.setError(error);
			}
			return imeiDays;
		}
		catch (RuntimeException ex) {
			log.error("Failed to parse request", ex);
			throw new InvalidParameterException();
		}

	}
	
	/**
	 * 
	 * @param clientId
	 * @param mobileId
	 * @param day
	 * @return a GeoJson FeatureCollection
	 */
	@RequestMapping("/{clientId}/{mobileId}/{day}")
	public FeatureCollection getImeiTrack(@PathVariable String clientId,@PathVariable String mobileId,@PathVariable String day) {
		try{
			log.debug("IMeiTrack request for {}", clientId.concat(" Imei: ").concat(mobileId));
			FeatureCollection resp = new FeatureCollection();
			resp.setCrs(CRS);
			List<Feature> features = new ArrayList<Feature>();
			features = dao.getTrackImei(clientId, mobileId, day);
			resp.addAll(features);

			Map<String, Object> map = new HashMap<String, Object>();
			map = dao.getPropertiesDays(clientId,mobileId,day);

			resp.setProperties(map);
			return resp;
		}
		catch (RuntimeException ex) {
			log.error("Failed to parse request", ex);
			throw new InvalidParameterException();
		}
	}
}
