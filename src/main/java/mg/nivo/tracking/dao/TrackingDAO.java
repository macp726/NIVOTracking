package mg.nivo.tracking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;














import mg.nivo.tracking.protocol.TrackImeiDays;
import mg.nivo.tracking.protocol.TrackImeiUser;













//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geojson.Feature;
import org.geojson.Point;
import org.springframework.stereotype.Component;

@Component
public class TrackingDAO {
	Logger log = LogManager.getLogger();

	@Resource(name="trackingDB")
	DataSource ds;

	//	private Cache cache;


	private String QUERY_LATEST = "SELECT t1.\"IMEI\", t1.\"GPS_LNG\", t1.\"GPS_LAT\", t1.\"END\" from nivo_query.\"%s\" t1";

	private String LATEST_INNER_JOIN =" INNER JOIN (SELECT \"IMEI\", MAX(\"END\") Max_Fecha FROM nivo_query.\"%s\" tracking"
			+ " WHERE tracking.\"GPS_LNG\" <> 0::numeric AND tracking.\"GPS_LAT\" <> 0::numeric AND"
			+ " tracking.\"IMEI\" IS NOT NULL AND tracking.\"IMEI\"::text <> ''::text"
			+ " GROUP BY  tracking.\"IMEI\") t2"
			+ " on t1.\"IMEI\" = t2.\"IMEI\" AND t1.\"END\" = t2.Max_Fecha,";

	private String LATEST_FIRS_IMEI = " (SELECT DISTINCT first_value(tr.\"ID\") OVER w AS idrecord"
			+ " FROM nivo_query.\"%s\" tr"
			+ " WINDOW w AS (PARTITION BY trunc(date_part('epoch'::text, tr.\"END\") / 60::double precision),"
			+ " tr.\"IMEI\" ORDER BY tr.\"ACCU\")) xx"
			+ " where t1.\"ID\" = xx.idrecord ";

	private String ORDER_BY_END = " order by t1.\"END\"";

	private String QUERY_USERS_IMEI ="SELECT \"IMEI\",\"USUARIO\",\"ID_COMPANY\",\"ACTIVE\" FROM \"IMEI_USER_VS\"  WHERE \"ID_COMPANY\" = ?";

	private String QUERY_IMEI_DAYS = "SELECT DISTINCT(date(\"END\")) FROM nivo_query.\"%s\" WHERE \"IMEI\" = ? AND \"STATE\" = 0 ORDER BY date(\"END\");";

	public TrackingDAO(){
		//		cache = CacheManager.getInstance().getCache("mg.nivo.tracking.MYCACHE");
	}

	public List<Feature> getLatestPoints(String clientId) {
		StringBuffer query = new StringBuffer();
		query.append(String.format(QUERY_LATEST, clientId));
		query.append(String.format(LATEST_INNER_JOIN, clientId));
		query.append(String.format(LATEST_FIRS_IMEI, clientId));
		query.append(String.format(ORDER_BY_END, clientId));
		try(Connection conn= ds.getConnection(); PreparedStatement ps = conn.prepareStatement(query.toString());){
			//	cache.isKeyInCache("keyValueXXX");
			//  cache.put(new Element("keyValueXXX", myObject));
			//	Element row = cache.get("keyValueXXX");
			//  row.getObjectValue();
			List<Feature> features = new ArrayList<Feature>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
				{
					Feature feature = new Feature();
					Point point = new Point(rs.getDouble("GPS_LNG"),rs.getDouble("GPS_LAT"));
					feature.setGeometry(point);
					feature.setProperty("imei", rs.getString("IMEI"));
					feature.setProperty("end", rs.getString("END"));
					features.add(feature);
				}
				return features;
			}
			catch (SQLException e) {
				log.error("Failed getLatestPoints", e);
			}
			return null;	
		} catch (SQLException e) {
			log.error("Failed getLatestPoints", e);
		}
		return null;
	}

	public List<TrackImeiUser> getUsersImeis(String clientId) {
		try(Connection conn= ds.getConnection(); PreparedStatement ps = conn.prepareStatement(QUERY_USERS_IMEI);){
			ps.setString(1, clientId);
			List<TrackImeiUser> listUsers = new ArrayList<TrackImeiUser>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
				{
					TrackImeiUser trackUser = new TrackImeiUser();
					trackUser.setActive(rs.getBoolean("ACTIVE"));
					trackUser.setId_company(rs.getString("ID_COMPANY"));
					trackUser.setUser(rs.getString("USUARIO"));
					trackUser.setImei(rs.getString("IMEI"));
					listUsers.add(trackUser);
				}
				if(listUsers.size()!=0)
					return listUsers;
			}
			catch (SQLException e) {
				log.error("Failed getUsersImeis", e);
			}
		}catch (SQLException e) {
			log.error("Failed getUsersImeis", e);
		}
		return null;
	}

	public TrackImeiDays getDaysImei(String clientId, String mobileId) {
		TrackImeiDays imeiDays = new TrackImeiDays();
		StringBuffer query = new StringBuffer();
		query.append(String.format(QUERY_IMEI_DAYS, clientId));
		try(Connection conn= ds.getConnection(); PreparedStatement ps = conn.prepareStatement(query.toString());){
			ps.setString(1, mobileId);
			try (ResultSet rs = ps.executeQuery()) {
				List<Date> days = new ArrayList<Date>();
				while (rs.next())
				{
					days.add(rs.getDate("date"));
				}
				imeiDays.setImei(mobileId);
				imeiDays.setDays(days);
				return imeiDays;
			}catch (SQLException e) {
				log.error("Failed getDaysImei", e);
			}
		}catch (SQLException e) {
			log.error("Failed getDaysImei", e);
		}
		return null;
	}

	public List<Feature> getTrackImei(String clientId, String mobileId, String day) {
		StringBuffer query = new StringBuffer();
		query.append(String.format(QUERY_LATEST, clientId));
		query.append(",");
		query.append(String.format(LATEST_FIRS_IMEI, clientId));
		query.append(" AND date(\"END\") = ? AND \"IMEI\" = ? AND \"STATE\" = '0' ");
		query.append(ORDER_BY_END);
		try(Connection conn= ds.getConnection(); PreparedStatement ps = conn.prepareStatement(query.toString());){
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date dateUtil = sdf1.parse(day);
			java.sql.Date sqlStartDate = new java.sql.Date(dateUtil.getTime()); 
			ps.setDate(1, sqlStartDate);
			ps.setString(2, mobileId);
			List<Feature> features = new ArrayList<Feature>();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
				{
					Feature feature = new Feature();
					Point point = new Point(rs.getDouble("GPS_LNG"),rs.getDouble("GPS_LAT"));
					feature.setGeometry(point);
					feature.setProperty("imei", rs.getString("IMEI"));
					feature.setProperty("end", rs.getString("END"));
					features.add(feature);
				}
				return features;
			}
			catch (SQLException e) {
				log.error("Failed getLatestPoints", e);
			}
			return null;		
		}
		catch (SQLException e) {
			log.error("Failed getTrackImei", e);
		} catch (ParseException e1) {
			log.error("Failed getTrackImei", e1);
		}
		return null;
	}

	public Map<String, Object> getPropertiesDays(String clientId, String mobileId, String day) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			TrackImeiDays imeiDays = getDaysImei(clientId, mobileId);
			List<Date> days = imeiDays.getDays();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date dateUtil = sdf1.parse(day);
			if(days.contains(dateUtil))
			{
				int selectDate = days.indexOf(dateUtil);
				Date nextDate;
				Date previousDate;
				if(days.size()-1!=selectDate)
					nextDate =days.get(selectDate+1);
				else
					nextDate = days.get(days.size()-1);
				if(selectDate!=0)
					previousDate = days.get(selectDate-1);
				else
					previousDate = days.get(0);
				map.put("firtsDate", days.get(0));
				map.put("previousDate", previousDate);
				map.put("presentDate", day);
				map.put("nextDate", nextDate);
				map.put("lastDate", days.get(days.size()-1));
			}
			return map;
		} catch (ParseException e) {
			log.error("Failed getPropertiesDays", e);
		}
		return null;
	}
}