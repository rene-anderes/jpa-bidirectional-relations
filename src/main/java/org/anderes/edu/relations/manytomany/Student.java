package org.anderes.edu.relations.manytomany;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;

@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    
    @ManyToMany
    @JoinTable(name="STUDENT_MODULE",
        joinColumns = @JoinColumn(name="S_ID"),
        inverseJoinColumns = @JoinColumn(name="M_ID"))
    private Set<Module> modules = new HashSet<>();
    
    Student() {
        super();
    }

    public Student(final String firstname, final String lastname) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public Long getId() {
        return id;
    }

    /* ------------- Pattern für JPA bidirektionale Beziehung ------------ */ 
    
    public void addModule(Module module) {
        module.addStudent(this);
    }
    
    public void internalAddModule(Module module) {
        modules.add(module);
    }

    public void removeModule(Module module) {
        module.removeStudent(this);
    }
    
    public void internalRemoveModule(Module module) {
        modules.remove(module);
    }
    
    @PreRemove
    public void preRemove() {
        final Collection<Module> tempModules = new HashSet<Module>(modules);
        for (Module module : tempModules) {
            removeModule(module);
        }
    }
    
    /* /------------ Pattern für JPA bidirektionale Beziehung ------------ */ 
    
    public Collection<Module> getModules() {
        return Collections.unmodifiableSet(modules);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
        result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
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
        Student other = (Student) obj;
        if (firstname == null) {
            if (other.firstname != null)
                return false;
        } else if (!firstname.equals(other.firstname))
            return false;
        if (lastname == null) {
            if (other.lastname != null)
                return false;
        } else if (!lastname.equals(other.lastname))
            return false;
        return true;
    }
}
