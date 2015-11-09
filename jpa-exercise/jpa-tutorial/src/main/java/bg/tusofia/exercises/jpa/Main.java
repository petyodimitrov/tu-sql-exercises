package bg.tusofia.exercises.jpa;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bg.tusofia.exercises.jpa.entity.IdCard;
import bg.tusofia.exercises.jpa.entity.Period;
import bg.tusofia.exercises.jpa.entity.Person;
import bg.tusofia.exercises.jpa.entity.Phone;
import bg.tusofia.exercises.jpa.entity.Project;
import bg.tusofia.exercises.jpa.entity.Student;

public class Main {

	private static final Logger LOGGER = Logger.getLogger("JPA");

	public static void main(String[] args) {
		Main main = new Main();
		main.run();
	}

	public void run() {
		EntityManagerFactory factory = null;
		EntityManager entityManager = null;
		try {
			factory = Persistence.createEntityManagerFactory("jpa-tutorial");
			entityManager = factory.createEntityManager();
			persistPerson(entityManager);
			persistStudents(entityManager);
			addPhones(entityManager);
			loadPersons(entityManager);
			createProject(entityManager);
			queryProject(entityManager);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
			if (factory != null) {
				factory.close();
			}
		}
	}

	private void queryProject(EntityManager entityManager) {
		String QUERY = "select p from Project p where p.projectPeriod.startDate = :startDate";
		TypedQuery<Project> query = entityManager.createQuery(QUERY, Project.class).setParameter("startDate",
				createDate(1, 1, 2015));
		List<Project> resultList = query.getResultList();
		for (Project project : resultList) {
			LOGGER.info(project.getProjectPeriod().getStartDate().toString());
		}
	}

	private void createProject(EntityManager entityManager) {
		String QUERY = "select s from Student s where s.favouriteProgrammingLanguage = :fpl";
		List<Student> resultList = entityManager.createQuery(QUERY, Student.class).setParameter("fpl", "Java")
				.getResultList();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		Project project = new Project();
		project.setTitle("Java Project");
		project.setProjectType(Project.ProjectType.EXERCISE);
		Period period = new Period();
		period.setStartDate(createDate(1, 1, 2015));
		period.setEndDate(createDate(31, 12, 2015));
		project.setProjectPeriod(period);
		for (Student student : resultList) {
			project.getStudents().add(student);
			student.getProjects().add(project);
		}
		entityManager.persist(project);
		transaction.commit();
	}

	private Date createDate(int day, int month, int year) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.DAY_OF_MONTH, day);
		gc.set(Calendar.MONTH, month - 1);
		gc.set(Calendar.YEAR, year);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		return new Date(gc.getTimeInMillis());
	}

	private void addPhones(EntityManager entityManager) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> query = builder.createQuery(Person.class);
		Root<Person> personRoot = query.from(Person.class);
		query.where(builder.equal(personRoot.get("lastName"), "Simpson"));
		List<Person> resultList = entityManager.createQuery(query).getResultList();
		for (Person person : resultList) {
			Phone phone = new Phone();
			phone.setNumber("+49 1234 456789");
			entityManager.persist(phone);
			person.getPhones().add(phone);
			phone.setPerson(person);
		}
		transaction.commit();
	}

	private void persistPerson(EntityManager entityManager) {
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			Person person = new Person();
			person.setFirstName("Homer");
			person.setLastName("Simpson");
			entityManager.persist(person);
			IdCard idCard = new IdCard();
			idCard.setIdNumber("4711");
			idCard.setIssueDate(new Date());
			person.setIdCard(idCard);
			entityManager.persist(idCard);
			transaction.commit();
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	private void persistStudents(EntityManager entityManager) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		Student studentA = new Student();
		studentA.setFirstName("Gavin");
		studentA.setLastName("Coffee");
		studentA.setFavouriteProgrammingLanguage("Java");
		entityManager.persist(studentA);

		Student studentB = new Student();
		studentB.setFirstName("Thomas");
		studentB.setLastName("Micro");
		studentB.setFavouriteProgrammingLanguage("C#");
		entityManager.persist(studentB);

		Student studentC = new Student();
		studentC.setFirstName("Christian");
		studentC.setLastName("Cup");
		studentC.setFavouriteProgrammingLanguage("Java");
		entityManager.persist(studentC);

		transaction.commit();
	}

	private void loadPersons(EntityManager entityManager) {
		entityManager.clear();
		String GET_ALL = "select p from Person p left join fetch p.phones";
		TypedQuery<Person> query = entityManager.createQuery(GET_ALL, Person.class);
		for (Person person : query.getResultList()) {
			String result = person.getFirstName() + " " + person.getLastName();
			if (person instanceof Student) {
				Student student = (Student) person;
				result += " ";
				result += student.getFavouriteProgrammingLanguage();
			}
			IdCard idCard = person.getIdCard();
			if (idCard != null) {
				result += " ";
				result += idCard.getIdNumber();
				result += " ";
				result += idCard.getIssueDate();
			}
			List<Phone> phones = person.getPhones();
			for (Phone phone : phones) {
				result += " ";
				result += phone.getNumber();
			}
			LOGGER.info(result);
		}
	}
}
