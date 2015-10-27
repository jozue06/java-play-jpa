package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by faren on 10/27/15.
 */
@Entity
public class Person {

    @Id
    @GeneratedValue
    public Long id;

    public String name;
}
