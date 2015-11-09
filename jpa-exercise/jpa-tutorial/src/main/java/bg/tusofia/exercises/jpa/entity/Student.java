package bg.tusofia.exercises.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "T_STUDENT")
public class Student extends Person {

	@Column(name = "FAV_PROG_LANG")
	private String favouriteProgrammingLanguage;

	@ManyToMany(mappedBy = "students")
	private List<Project> projects = new ArrayList<Project>();

	public String getFavouriteProgrammingLanguage() {
		return favouriteProgrammingLanguage;
	}

	public void setFavouriteProgrammingLanguage(String favouriteProgrammingLanguage) {
		this.favouriteProgrammingLanguage = favouriteProgrammingLanguage;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

}
