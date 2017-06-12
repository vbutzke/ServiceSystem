package main;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;
import java.io.InvalidObjectException;
import java.lang.Exception;
import java.util.NoSuchElementException;
import java.util.UUID;
import com.mongodb.client.model.Projections;

public class Database {
	
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> passwords;
	private MongoCollection<Document> students;
	
//Database management
	public void createDB(){
		
		MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
		this.mongoClient = new MongoClient(connectionString);
		this.database = mongoClient.getDatabase("ServiceSystem");
		
	}
	
	public void createCollections(){
		
		this.students = database.getCollection("students");
		this.passwords = database.getCollection("passwords");
		
	}
	
	public void closeConnection(){
		this.mongoClient.close();
	}

//Students management
	public void addStudent(Student student) throws Exception {
		
		boolean studentExists = isStudentInRecords(student.getName(), student.getUuid());
		
		if(!studentExists){
			Document studentRecord = new Document("name", student.getName())
		        						  .append("uuid", student.getUuid())
		        						  .append("password", student.getPassword());
				 
			this.students.insertOne(studentRecord);
		}else{
			throw new Exception();
		}
		
	}
	
	public boolean isStudentInRecords(String name, UUID uuid){

		Document student = this.students.find(and(eq("name", name),  eq("uuid", uuid))).first();
		if(student == null){
			return true;
		}
		return false;
	}
	
	public Document findStudent(String name, UUID uuid) throws NoSuchElementException{
		
		Document student = this.students.find(eq(name, uuid)).first();
		
		if(student.isEmpty()){
			throw new NoSuchElementException();
		}
		
		return student;
	
	}
	
//Passwords management	
	public void addPassword(Password password) throws Exception{
		
		boolean passwordExists = isPasswordInRecords(password);
		
		if(!passwordExists){
			Document passwordRecord = new Document("password", password.getPassword())
		        						  .append("cancelled", password.isCancelled());
				 
			this.passwords.insertOne(passwordRecord);
		}else{
			throw new Exception();
		}

	}
	
	public boolean isPasswordInRecords(Password passwordObj){
		Document record = this.passwords.find(eq("password", passwordObj.getPassword())).first();
		if(record == null){
			return false;
		}
		return true;
	}
	
	public void createStudentPassword(Student student){
		
		Bson filter = new Document("uuid", student.getUuid());
		this.students.updateOne(filter, new Document("$set", new Document("password", student.getPassword())));
		try{
			addPassword(student.getPasswordObj());
		}catch(Exception e){
			System.out.println("This password has been generated already.");
		}
		System.out.println("Password updated.");
		
	}
	
	public void cancelPassword(Student student) throws InvalidObjectException{
		
		Document studentDoc = findStudent(student.getName(), student.getUuid());
		Bson filterStudent = new Document("uuid", student.getUuid());
		Bson filterPassword = new Document("password", student.getPassword());
		
		if(studentDoc == null){
			throw new InvalidObjectException("Student does not exist");
		}else{
			student.cancelPassword();
			this.students.updateOne(filterStudent, new Document("$set", new Document("password", student.getPassword())));
			this.passwords.updateOne(filterPassword, new Document("$set", new Document("cancelled", true)));
		}
		
	}
	
	public void clearPasswordsHistory(){
		 passwords.drop();
	}
	
	public boolean isPasswordActive(Password passwordObj){
		
		String passwordToFind = passwordObj.getPassword(); 
		FindIterable<Document> findIterable = this.passwords.find(eq("password", passwordToFind)).projection(Projections.fields(Projections.include("cancelled")));
		boolean isCancelled = false;
		
		for(Document document:findIterable){
			isCancelled = document.getBoolean("cancelled");
		}
		
		return isCancelled;
	}
	
}
