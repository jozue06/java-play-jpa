package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Person;
import play.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public Result addPerson(){
        JsonNode json = request().body().asJson();

        Person person = Json.fromJson(json, Person.class);

        if (person.toString().equals("")){
            return badRequest("Missing parameter");
        }

        JPA.em().persist(person);
        return ok();
    }

    @Transactional(readOnly = true)
    public Result listPerson(){
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);

        Root<Person> root = cq.from(Person.class);
        CriteriaQuery<Person> all = cq.select(root);

        TypedQuery<Person> allQuery = JPA.em().createQuery(all);

        JsonNode jsonNode = toJson(allQuery.getResultList());
        return ok(jsonNode);
    }


    @Transactional
    public Result getPerson(Long id){
        Person person = JPA.em().find(Person.class, id);

        if (person==null){
            return notFound("User not found");
        }

        return ok(toJson(person));
    }

    @Transactional
    public Result updatePerson(Long id){
        Person person = JPA.em().find(Person.class, id);

        if (person==null){
            return notFound("User not found");
        }

        JsonNode json= request().body().asJson();

        Person personToBe = Json.fromJson(json, Person.class);

        person.name = personToBe.name;

        return ok();
    }

    @Transactional
    public Result deletePerson(Long id){
        Person person = JPA.em().find(Person.class, id);

        if (person==null){
            return notFound("User not found");
        }

        JPA.em().remove(person);

        return ok();
    }

    @Transactional
    public Result searchPerson(String name){

        TypedQuery<Person> query = JPA.em().createQuery
                ("select p from Person p where p.name = :name", Person.class)
                .setParameter("name", name);

        try {
            Person person = query.getSingleResult();
            return ok(toJson(person));

        }catch (NoResultException e){
            return notFound("User not found");
        }

    }
}
