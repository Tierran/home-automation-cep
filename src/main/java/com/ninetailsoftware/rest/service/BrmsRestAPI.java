package com.ninetailsoftware.rest.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ninetailsoftware.kie.engine.KieEngine;
import com.ninetailsoftware.model.events.HaEvent;
import com.ninetailsoftware.model.facts.RequestContainer;

@Path("rest/services/rules")
public class BrmsRestAPI {
	Logger log = LoggerFactory.getLogger(BrmsRestAPI.class);
	
	private KieEngine kieEngine;
	private static String version;
	
	public BrmsRestAPI(){
		kieEngine = new KieEngine();
	}

	@GET
	@Path("/fire-until-halt")
	public Response fireUntilHalt(){
		kieEngine.fireUntilHalt("com.ninetailsoftware.ha", "home-automation-rules");
		return Response.status(201).entity("Success!").build();
	}
	
	@GET
	@Path("/halt")
	public Response halt(){		
		return Response.status(201).entity("Success!").build();
	}
	
	@POST
	@Path("/insert-facts")
	@Consumes("application/json")
	public Response insertFacts(RequestContainer requestBody){
		if(requestBody.getSimpleSwitch() != null){
			log.info("Recieved Request to Insert Fact");
			log.info("Simple Switch");
			log.info("id:" + requestBody.getSimpleSwitch().getId());
			log.info("status:" + requestBody.getSimpleSwitch().getStatus());
		}
		
		List<Object> facts = new ArrayList<Object>();
		facts.add(requestBody.getSimpleSwitch());
		
		kieEngine.insertFacts("com.ninetailsoftware.ha", "home-automation-rules", facts);
		return Response.status(201).entity("Success!").build();
	}
	
	@POST
	@Path("/insert-event")
	@Consumes("application/json")
	public Response insertEvent(HaEvent event){
		
		List<Object> facts = new ArrayList<Object>();
		facts.add(event);
		
		kieEngine.insertEvents("com.ninetailsoftware.ha", "home-automation-rules", facts);
		return Response.status(201).entity("Success!").build();
	}
}
