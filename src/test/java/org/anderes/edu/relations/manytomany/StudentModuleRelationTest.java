package org.anderes.edu.relations.manytomany;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StudentModuleRelationTest {

    /**
     * Bidirektionale Beziehung zwischen Student und Module testen
     */
    @Test
    public void shouldBeCheckBidirectionalStudent() {
        final Student student = new Student("Peter", "Page");
        final Module module_1 = new Module("Akupunkturanalgesie");
        final Module module_2 = new Module("Breaking Bad News - Gespräch");
        
        student.addModule(module_1);
        
        assertThat(student.getModules(), is(notNullValue()));
        assertThat(student.getModules().size(), is(1));
        assertThat(module_1.getStudents(), is(notNullValue()));
        assertThat(module_1.getStudents().size(), is(1));
        assertThat(module_1.getStudents().contains(student), is(true));
        
        student.addModule(module_2);
        
        assertThat(student.getModules().size(), is(2));
        assertThat(student.getModules().contains(module_2), is(true));
        assertThat(module_2.getStudents().size(), is(1));
        assertThat(module_2.getStudents().contains(student), is(true));
        
        student.removeModule(module_1);
        assertThat(student.getModules().size(), is(1));
        assertThat(student.getModules().contains(module_2), is(true));
        assertThat(module_1.getStudents().contains(student), is(false));
    }
    
    /**
     * Bidirektionale Beziehung zwischen Module und Student testen
     */
    @Test
    public void shouldBeCheckBidirectionalModule() {
        final Student student = new Student("Peter", "Page");
        final Module module_1 = new Module("Akupunkturanalgesie");
        final Module module_2 = new Module("Breaking Bad News - Gespräch");
        
        module_1.addStudent(student);
        
        assertThat(module_1.getStudents(), is(notNullValue()));
        assertThat(module_1.getStudents().size(), is(1));
        assertThat(module_1.getStudents().contains(student), is(true));
        assertThat(student.getModules(), is(notNullValue()));
        assertThat(student.getModules().size(), is(1));
        
        module_2.addStudent(student);
        
        assertThat(student.getModules().size(), is(2));
        assertThat(student.getModules().contains(module_2), is(true));
        assertThat(module_2.getStudents().size(), is(1));
        assertThat(module_2.getStudents().contains(student), is(true));
        
        module_1.removeStudent(student);

        assertThat(module_1.getStudents().size(), is(0));
        assertThat(module_2.getStudents().size(), is(1));
        assertThat(module_2.getStudents().contains(student), is(true));
        assertThat(student.getModules().size(), is(1));
        assertThat(student.getModules().contains(module_2), is(true));
    }
    
    /**
     * Wird ein Student gelöscht ({@code EntityManager.remove()}), 
     * müssen auch die Beziehungen zu den Module auch gelöscht werden.
     * </p>
     * Die Methode {@code preRemove} wird mittels {@code @PreRemove}
     * annotiert und wird im JPA_Umfeld automatsich dafür sorgen das die Methode aufgerufen wird.
     */
    @Test
    public void shouldBeCheckStudentPreRemove() {
        final Student student = new Student("Peter", "Page");
        final Module module_1 = new Module("Akupunkturanalgesie");
        final Module module_2 = new Module("Breaking Bad News - Gespräch");
        module_1.addStudent(student);
        module_2.addStudent(student);
        
        student.preRemove();
        
        assertThat(module_1.getStudents().size(), is(0));
        assertThat(module_2.getStudents().size(), is(0));
        assertThat(student.getModules().size(), is(0));
    }
    
    /**
     * Wird ein Module gelöscht ({@code EntityManager.remove()}), 
     * müssen auch die Beziehungen zu den Studenten auch gelöscht werden.
     * </p>
     * Die Methode {@code preRemove} wird mittels {@code @PreRemove}
     * annotiert und wird im JPA_Umfeld automatsich dafür sorgen das die Methode aufgerufen wird.
     */
    @Test
    public void shouldBeCheckModulePreRemove() {
        final Student student_1 = new Student("Peter", "Page");
        final Student student_2 = new Student("Bill", "Page");
        final Module module = new Module("Akupunkturanalgesie");
        module.addStudent(student_1);
        module.addStudent(student_2);
        
        module.preRemove();
        
        assertThat(module.getStudents().size(), is(0));
        assertThat(student_1.getModules().size(), is(0));
        assertThat(student_2.getModules().size(), is(0));
    }
}
