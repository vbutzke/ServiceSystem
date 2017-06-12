package main;
import java.util.UUID;

public class Student {

	private String name;
	private UUID uuid;
	private Password password;
	
	public Student(String name){
		this.name = name;
		generateUUID();
	}
	
	public void generateUUID(){
		this.uuid = UUID.randomUUID();
	}
	
	public String generatePassword(String first, int last){
		
		String password = first + Integer.toString(last);
		this.password = new Password(password);
		return this.password.getPassword();
		
	}
	
	public String getName() {
		return name;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public String getPassword() {
		return password.getPassword();
	}
	
	public Password getPasswordObj(){
		return password;
	}
	
	public void cancelPassword(){
		this.password.cancelPassword();
	}
	
}
