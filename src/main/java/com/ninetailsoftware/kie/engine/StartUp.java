package com.ninetailsoftware.kie.engine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class StartUp extends HttpServlet{
	
	private KieEngine kieEngine;
	
	public void init() throws ServletException{
		this.startKieEngine();
	}
	
	private void startKieEngine() {
		kieEngine.fireUntilHalt("com.ninetailsoftware.ha", "home-automation-rules");
	}
}
