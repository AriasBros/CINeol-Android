package net.cineol.model;

public class Person {
	
	public enum Type {
		ACTOR,
		DIRECTOR,
		SCRIPWRITER,
		MUSICIAN,
		PRODUCTOR,
		PHOTOGRAPHER
	}

	protected long id;
	protected String name;
	protected String urlCINeol;
	private String job;
	
	public Person() {
		super();
	}
	
	public Person(String name, String job, String url) {
		super();
		
		this.name = name;
		this.job = job;
		this.urlCINeol = url;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUrlCINeol(String urlCINeol) {
		this.urlCINeol = urlCINeol;
	}

	public String getUrlCINeol() {
		return urlCINeol;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getJob() {
		return job;
	}
	
	@Override
	public String toString() {
		return "Person [job=" + job + ", name=" + name + ", urlCINeol="
				+ urlCINeol + "]";
	}
}
