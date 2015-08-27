package org.anderes.edu.relations.manytomany;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;

@Entity
public class Module {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String title;
    
    @ManyToMany(mappedBy = "modules")
    private Set<Student> students = new HashSet<>();
    
    Module() {
        super();
    }
    
    public Module(String title) {
        this.title = title;
    }
    
    public Collection<Student> getStudents() {
        return Collections.unmodifiableSet(students);
    }

    /* ------------- Pattern für JPA bidirektionale Beziehung ------------ */ 

    public void addStudent(Student student) {
        students.add(student);
        student.internalAddModule(this);
    }
    
    public void removeStudent(Student student) {
        students.remove(student);
        student.internalRemoveModule(this);
    }
    
    @PreRemove
    public void preRemove() {
        final Collection<Student> tempStudents = new HashSet<Student>(students);
        for (Student student : tempStudents) {
            removeStudent(student);
        }
    }

    /* /------------ Pattern für JPA bidirektionale Beziehung ------------ */
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Module other = (Module) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }
}

