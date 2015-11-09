package bg.tusofia.exercises.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "T_PHONE")
public class Phone {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GENERATOR")
	@TableGenerator(name = "TABLE_GENERATOR", table="T_SEQUENCES", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_VALUE", pkColumnValue = "PHONE")
	private Long id;
	
	private String number;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Person person;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
