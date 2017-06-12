package main;

import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class App {
	
	private Database db;
	private LinkedList<Password> list;
	private LinkedList<Student> students;
	private LinkedList<String> firstPasswordCharacter;
	private LinkedList<Integer> lastPasswordCharacter;
	
	public App(){
		createDatabase();
		this.list = new LinkedList<Password>();
		this.students = new LinkedList<Student>();
		this.firstPasswordCharacter = new LinkedList<String>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
		this.lastPasswordCharacter = new LinkedList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
	}
	
	public void createDatabase(){
		this.db = new Database();
		this.db.createDB();
		this.db.createCollections();
	}
	
	public void closeDatabase(){
		this.db.clearPasswordsHistory();
		this.db.closeConnection();
	}
	
	public Password callStudent() {
		
		students.remove();
		Password next = list.remove();

		if(!db.isPasswordActive(next)){
			System.out.println("Password is cancelled");
			if(list.isEmpty()){
				return null;
			}
			else{
				return callStudent();
			}
		}
		return next;
	}
	
	public void registerStudent(String name) throws Exception{
		Student student = new Student(name);
		students.addLast(student);
		try{
			this.db.addStudent(student);
		}catch(Exception e){
			System.out.println("The user is already registered on the system.");
			throw e;
		}
	}
	
	public String generatePassword(String name, UUID uuid) throws NoSuchElementException{
	
		String firstCharacter = firstPasswordCharacter.remove();
		int lastCharacter = lastPasswordCharacter.remove();
		Student student;
		firstPasswordCharacter.addLast(firstCharacter);
		lastPasswordCharacter.addLast(lastCharacter);
		try{	
			student = (Student)(this.db.findStudent(name, uuid)).keySet();
			int index = students.indexOf(student);
			String password = student.generatePassword(firstCharacter, lastCharacter);
			this.db.createStudentPassword(student);
			list.addLast(student.getPasswordObj());
			if(index > 0){
				students.set(index, student);			
			}
			return password;
		}catch(NoSuchElementException e){
			System.out.println("Student does not exist.");
			throw e;
		}
	}
	
	public void cancelPassword(String name, UUID uuid) throws InvalidObjectException{
		Student[] allst = new Student[students.size()];
		students.toArray(allst);
		
		for(Student st:allst){
			if((st.getName()).equals(name) && (st.getUuid()).equals(uuid)){
				try{
					this.db.cancelPassword(st);
				}catch(InvalidObjectException e){
					System.out.println("The password could not be removed, because the user does not exist.");
					throw e;
				}
				list.remove(st.getPasswordObj());
				students.remove(st);
			}
		}
	}
	
}
