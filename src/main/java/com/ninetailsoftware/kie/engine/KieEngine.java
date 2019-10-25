package com.ninetailsoftware.kie.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ninetailsoftware.kie.engine.listener.FactUpdateListener;

public class KieEngine {
	
	Logger log = LoggerFactory.getLogger(KieEngine.class);

	private static Map<String, KieContainer> kContainers = new HashMap<String, KieContainer>();
	private static Map<String, KieSession> kSessions = new HashMap<String, KieSession>();
	private static KieScanner kScanner;
	private static String latest;

	public void registerNewKieScanner(String groupId, String artifactId) {

		log.debug("Starting registerNewKieScanner method in KieEngine.java");
		
		if (!kContainers.containsKey(groupId + artifactId)) {
			KieServices kieServices = KieServices.Factory.get();      
			ReleaseId releaseId = kieServices.newReleaseId(groupId, artifactId, "RELEASE");
			KieContainer kContainer = kieServices.newKieContainer(releaseId);
			kScanner = kieServices.newKieScanner(kContainer);
			kScanner.start(60000);
			kContainers.put(groupId + artifactId, kContainer);
			KieSession kSession = kContainer.newKieSession();
			RuleRuntimeEventListener ruleEventListener = new FactUpdateListener();
			kSession.addEventListener(ruleEventListener);
			kSessions.put(groupId + artifactId, kSession);
			latest = groupId + artifactId;
			log.info(groupId + ":" + artifactId + ":LATEST has been started in a new KieContainer and logging has started");
		}
	}
		
	public void insertFacts(String groupId, String artifactId, List<Object> facts){
		
		log.info("Inserting Fact in KieEngine.java");
		
		if(kSessions.containsKey(groupId + artifactId)){
			
			KieSession kSession = kSessions.get(groupId + artifactId);
			for(int i = 0; i < facts.size(); i++){
				log.info("Insertig Fact in KieEngine.java " + facts.get(i).toString());
				kSession.insert(facts.get(i));
			}
		}else {
			log.info(groupId + ":" + artifactId + ":LATEST does not have an existing KieContainer.");
			this.registerNewKieScanner(groupId, artifactId);
			this.fireUntilHalt(groupId, artifactId);
			this.insertFacts(groupId, artifactId, facts);
		}
	}
	
	public void insertEvents(String groupId, String artifactId, List<Object> facts){
			
		if(kSessions.containsKey(groupId + artifactId)){
			
			KieSession kSession = kSessions.get(groupId + artifactId);
			for(int i = 0; i < facts.size(); i++){
				log.info("Insertig Event in KieEngine.java " + facts.get(i).toString());
				kSession.insert(facts.get(i));
			}
		}else {
			log.info(groupId + ":" + artifactId + ":LATEST does not have an existing KieContainer.");
			this.registerNewKieScanner(groupId, artifactId);
			this.fireUntilHalt(groupId, artifactId);
			this.insertEvents(groupId, artifactId, facts);
		}
	}
	
	public void fireUntilHalt(String groupId, String artifactId){
		
		log.debug("Starting fireUntilHalt method in KieEngine.java");
		
		if(kSessions.containsKey(groupId + artifactId)){
			final KieSession kSession = kSessions.get(groupId + artifactId);
			
			new Thread(){
				@Override
				public void run(){
					kSession.fireUntilHalt();
				}
			}.start();	
			
			log.info(groupId + ":" + artifactId + ":LATEST is now running.  Execution will continue until halt() is called.");
		} else {
			log.info(groupId + ":" + artifactId + ":LATEST does not have an existing KieContainer.");
			this.registerNewKieScanner(groupId, artifactId);
			this.fireUntilHalt(groupId, artifactId);
		}
	}
	
	public void halt(String groupId, String artifactId){
		if(kSessions.containsKey(groupId + artifactId)){
			KieSession kSession = kSessions.get(groupId + artifactId);
			kSession.halt();
		}
	}
	
	public Integer queryEventCount(String eventId){
		KieSession kSession = kSessions.get(latest);
		QueryResults results = kSession.getQueryResults("Event Count", new Object[] {eventId});
		return results.size();
	}

}
