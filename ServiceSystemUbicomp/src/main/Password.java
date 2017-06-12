package main;

import javax.persistence.*;

@Entity
public class Password {
	@Id
	private String password;
	private boolean cancelled = false;
	
	public Password(String password){
		this.password = password;
	}
	
	public void cancelPassword(){
		this.cancelled = true;
	}

	public String getPassword() {
		return password;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

}
