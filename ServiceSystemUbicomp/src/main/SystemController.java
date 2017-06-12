package main;

import java.io.InvalidObjectException;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SystemController {
	
	@Autowired
	App app;
	
	@RequestMapping("\register")
	public void registerStudent(@RequestParam(value="name") String name, HttpServletResponse response){
		try{
			app.registerStudent(name);
			System.out.println("Student registered successfully");
			 response.setStatus( HttpServletResponse.SC_OK  );
		}catch(Exception e){
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST  );
		}
	}
	
	@RequestMapping("\tokengen")
	public String generatePassword(@RequestParam(value="name") String name, @RequestParam(value="uuid") UUID uuid, HttpServletResponse response){
		try{
			String password = app.generatePassword(name, uuid);
			System.out.println("Password created");
			response.setStatus( HttpServletResponse.SC_OK  );
			return password;
		}catch(NoSuchElementException e){
			response.setStatus( HttpServletResponse.SC_NO_CONTENT  );
		}
		return null;
	}
	
	@RequestMapping("\tokencanc")
	public void cancelPassword(@RequestParam(value="name") String name, @RequestParam(value="uuid") UUID uuid, HttpServletResponse response){
		try{
			app.cancelPassword(name, uuid);
			response.setStatus( HttpServletResponse.SC_OK  );
			System.out.println("Password cancelled successfully");
		}catch(InvalidObjectException e){
			response.setStatus( HttpServletResponse.SC_NOT_FOUND );
		}
	}
	
	@RequestMapping("\next")
	public String callNextStudent(HttpServletResponse response){
		
		Password next = app.callStudent();
		
		if(next == null){
			app.closeDatabase();
			response.setStatus( HttpServletResponse.SC_NO_CONTENT  );
			return null;
		}else{
			response.setStatus( HttpServletResponse.SC_OK  );
			return next.getPassword();
		}
	}
}
