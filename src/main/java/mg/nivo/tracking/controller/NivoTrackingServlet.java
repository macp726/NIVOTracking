package mg.nivo.tracking.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import mg.nivo.tracking.dao.NewTrackingNivo;
import mg.nivo.tracking.protocol.ResponseTrackingNivo;
import mg.nivo.tracking.protocol.TrackNivo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class TrackingNivo
 */
public class NivoTrackingServlet extends HttpServlet {
	private static final Logger log = LogManager.getLogger();
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/trackingDS")
	private DataSource trackingDS;	
	private ObjectMapper mapper = new ObjectMapper();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NivoTrackingServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub'
		log.error("Attempting to invoke Get");
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		ResponseTrackingNivo responseTracking = new ResponseTrackingNivo();
		NewTrackingNivo newTrack = new NewTrackingNivo();
		StringBuilder jsonRequest = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(  
	                new InputStreamReader(request.getInputStream()));  
			
			String line = null;  
	        while((line = in.readLine()) != null) {    
	            jsonRequest.append(line);
	        }
	        ObjectMapper toObject = new ObjectMapper();
	        TrackNivo trackingObject = new TrackNivo();
	        
	        trackingObject = toObject.readValue(jsonRequest.toString(), TrackNivo.class);
	        
	        newTrack.setDataSources(trackingDS);
	        newTrack.newRecord(trackingObject);
	        
	        responseTracking.setStatus(1);
	        responseTracking.setMsg(String.valueOf(trackingObject.getTrackingNivo().getTrack().size()).concat(" Registros sincronizado"));
	        
	        ObjectMapper objectResponse = new ObjectMapper();
	        response.setContentType("text/plain; charset=UTF-8");
	        log.info("Response Tracking".concat(objectResponse.writeValueAsString(responseTracking)));
	        
			PrintWriter out = response.getWriter();
			mapper.writeValue(out, responseTracking);
			out.close();
		} catch (Exception e) {
			log.error("NivoTracking: ",e);
	        responseTracking.setStatus(0);
	        responseTracking.setMsg("Error: ".concat(e.getMessage()));
	        
	        ObjectMapper objectResponse = new ObjectMapper();
	        response.setContentType("text/plain; charset=UTF-8");
	        log.info("Response Tracking: ".concat(objectResponse.writeValueAsString(responseTracking)));
	        
	        PrintWriter out = response.getWriter();
			out.append(mapper.writeValueAsString(responseTracking));
			out.close();
		}
	}
}
