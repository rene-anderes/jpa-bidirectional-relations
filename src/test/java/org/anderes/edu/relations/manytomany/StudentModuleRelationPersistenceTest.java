package org.anderes.edu.relations.manytomany;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StudentModuleRelationPersistenceTest {

    private EntityManager entityManager;
    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("puEclipselink");
    
    @Before
    public void setUp() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        entityManager.close();
    }
    
    /**
     * Es wird ein neuer Student und Module instanziiert und gespeichert (separat).
     * </p>
     * Die Zwischentabelle wird ebenfalls einen neuen Eintrag erhalten, 
     * da dem Module dieser Student zugeordnet worden ist. 
     */
    @Test
    public void shouldBePersistNewModuleAndNewStudent() {
        final Student student = new Student("Peter", "Page");
        final Module module = new Module("Akupunkturanalgesie");
        module.addStudent(student);
        
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.persist(module);
        entityManager.getTransaction().commit();
        
        assertThat(student.getModules().size(), is(1));
        assertThat(module.getStudents().size(), is(1));
    }
    
    /* ------------------- Vergleich zwischen PERIST und MERGE --------------------------------- */
    
    /**
     * Es wird ein neuer Student und Module instanziiert und gespeichert (separat).
     * Wird versucht mittels {@code persist} nur der Student zu speichern, wird
     * die erwartete Exception ausgelöst: "... was not marked cascade PERSIST..."
     */
    @Test(expected = RollbackException.class)
    public void shouldBeNotPersistNewModuleAndNewStudent() {
        final Student student = new Student("Peter", "Page");
        final Module module = new Module("Akupunkturanalgesie");
        module.addStudent(student);
        
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
        
        assertThat(student.getModules().size(), is(1));
        assertThat(module.getStudents().size(), is(1));
    }
    
    /**
     * Es wird ein neuer Student und Module instanziiert und 
     * und nur der Student mittels {@code merge} gespeichert.
     * Unerwartetes Verhalten: Das noch nicht existierende Module wird
     * ebenfalls gespeichert.
     */
    @Test
    public void shouldBeMergeNewModuleAndNewStudent() {
        final Student student = new Student("Peter", "Page");
        final Module module = new Module("Akupunkturanalgesie");
        module.addStudent(student);
        
        entityManager.getTransaction().begin();
        entityManager.merge(student);
        entityManager.getTransaction().commit();
        
        assertThat(student.getModules().size(), is(1));
        assertThat(module.getStudents().size(), is(1));
    }
    
    /**
     * Einem bestehenden Module einen neuen Studenten hinzufügen und
     * den Studenten speichern.
     */
    @Test
    public void shouldBeAddNewStudentToExistsModule() {
        final Student student = new Student("Harry", "Sneed");
        final Module module = entityManager.find(Module.class, 8001L);
        assertThat(module.getStudents().size(), is(1));
        
        module.addStudent(student);
        
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
        
        assertThat(student.getModules().size(), is(1));
        assertThat(module.getStudents().size(), is(2));
    }
    
    /**
     * Einem bestehenden Module einen neuen Studenten hinzufügen und
     * den Studenten mittels {@code merge} persistieren.
     * </p>
     * Funktioniert mit {@code merge} nicht: "... was not marked cascade PERSIST..."
     */
    @Test(expected = RollbackException.class)
    public void shouldBeNotAddNewStudentToExistsModule() {
        final Student student = new Student("Jean", "NoGo");
        final Module module = entityManager.find(Module.class, 8001L);
        
        module.addStudent(student);
        
        entityManager.getTransaction().begin();
        entityManager.merge(student);
        entityManager.getTransaction().commit();
    }
    
    /* /------------------ Vergleich zwischen PERIST und MERGE --------------------------------- */
    
    /**
     * Ein Module wird gelöscht und alle Beziehungen in der Zwischentabelle
     * sollten auch gelöscht werden
     */
    @Test
    public void shouldBeRemoveExistsModule() {
        final Module module = entityManager.find(Module.class, 8002L);
        assertThat(module.getStudents().size(), is(2));
        
        entityManager.getTransaction().begin();
        entityManager.remove(module);
        entityManager.getTransaction().commit();
        
        final Student hancock = entityManager.find(Student.class, 9002L);
        final Student wonder = entityManager.find(Student.class, 9003L);
        
        assertThat(hancock.getModules().size(), is(0));
        assertThat(wonder.getModules().size(), is(1));
    }
    
    /**
     * Wird ein Student entfernt, so muss auch die Gegenbeziehung
     * ausgelöst werden. D.h. das entsprechende Module besitzt
     * einen Student weniger.
     */
    @Test
    public void shouldBeRemoveExistsStudent() {
        final Student mccarty = entityManager.find(Student.class, 9004L);
        final Module module = entityManager.find(Module.class, 8004L);
        assertThat(mccarty.getModules().size(), is(1));
        assertThat(module.getStudents().size(), is(1));
        
        entityManager.getTransaction().begin();
        entityManager.remove(mccarty);
        entityManager.getTransaction().commit();
        
        assertThat(module.getStudents().size(), is(0));
    }
}
