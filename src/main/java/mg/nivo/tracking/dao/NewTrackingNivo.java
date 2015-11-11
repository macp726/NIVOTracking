package mg.nivo.tracking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import mg.nivo.tracking.protocol.TrackNivo;
import mg.nivo.tracking.protocol.TrackNivo.TrackingNivo.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewTrackingNivo {

	Logger log = LogManager.getLogger();
	private DataSource pgDS;

	public void setDataSources(DataSource pgDS) throws Exception{
		this.pgDS = pgDS;
	}

	public void newRecord(TrackNivo trackingObject) throws Exception{

		String sql = "insert into nivo_query.\"%s\" (\"DEVICEID\",\"GPS_LNG\",\"GPS_LAT\",\"END\",\"MAC\",\"IMEI\",\"ACCU\",\"STATE\") VALUES (?,?,?,?,?,?,?,?)";

		List<Track> trackList =	trackingObject.getTrackingNivo().getTrack();

		log.info("Size trackList ".concat(String.valueOf(trackList.size())));

		int index = 1;
		for (Iterator<Track> iterator = trackList.iterator(); iterator.hasNext();) {
			Track track = (Track) iterator.next();
			log.info("--IMEI A REGISTRAR: ".concat((track.getImei()!=null)?track.getImei():"Sin IMEI"));
			if(track.getImei()==null)
				log.info("Registro no tiene IMEI ".concat(String.valueOf(index)));
			else
			{
				String idCompany = getTableIdCompany(track.getImei());
				if(idCompany!=null && idCompany!="")
				{
					sql = String.format(sql, idCompany);
					log.info("Numero a registrar: ".concat(String.valueOf(index)));
					log.info("Tabla tracking a registrar: ".concat(idCompany));
					try(Connection con = this.pgDS.getConnection(); 
							PreparedStatement ps = con.prepareStatement(sql);
							){
						ps.setString(1, track.getDevId());
						ps.setDouble(2, track.getLon());
						ps.setDouble(3, track.getLat());
						ps.setTimestamp(4, Timestamp.valueOf(track.getEnd()));
						ps.setString(5, track.getMac());
						ps.setString(6, track.getImei());
						ps.setDouble(7, track.getAccu());
						ps.setDouble(8, track.getState());
						int rows= ps.executeUpdate();
						log.info("Insert rows: ".concat(String.valueOf(rows)));
						log.info("DeviceID: ".concat(track.getDevId()));
						log.info("END: ".concat(track.getEnd()));
						index++;
					}
					catch (Exception e) {
						e.printStackTrace();
						log.error("newRecord TrackNivo",e);
						throw new Exception(e);
					}
				}
				else
				{
					log.info("NO EXISTE IDCOMPANY");
					throw new Exception("NO EXISTE IDCOMPANY EN TABLA IMEI_USERS");
				}
			}
		}
	}

	private String getTableIdCompany(String imei) throws Exception{
		// SELECT "ID_COMPANY" FROM "IMEI_USER_VS" WHERE "IMEI" = '355357058830493'
		String sql = "SELECT \"ID_COMPANY\", \"ACTIVE\" FROM \"IMEI_USER_VS\" WHERE \"IMEI\" = '%s' AND \"ACTIVE\" = TRUE";
		sql = String.format(sql,imei);
		String idCompany = null;
		Boolean active = null;
		try(Connection con = this.pgDS.getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();	
				){
			while(rs.next())
			{
				idCompany=rs.getString(1);
				active=rs.getBoolean(2);
				if(active!=null && active)
					return idCompany;
				else {
					return null;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("getTableIdCompany TrackNivo",e);
			throw new Exception(e);
		}
		return idCompany;
	}
}
