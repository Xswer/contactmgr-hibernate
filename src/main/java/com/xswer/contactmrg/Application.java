package com.xswer.contactmrg;

import com.xswer.contactmrg.model.Contact;
import com.xswer.contactmrg.model.Contact.ContactBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.imageio.spi.ServiceRegistry;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class Application {
    //Hold reusable reference ti SessionFactory (since we need only one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        //Create a StandardServiceRegistry
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }


    public static void main(String[] args) {
        Contact contact = new ContactBuilder("Kirill", "Pahl")
                                .withEmail("123@123")
                                .withPhone(1231231231L)
                                .build();

        int id = save(contact);

        //Display list of contacts before the update
        System.out.printf("%n%nBefore update%n%n");
        fetchAllContacts().stream().forEach(System.out::println);

        //Get the persistent contact
        Contact c = findContactById(id);


        //Update contact
        c.setFirstName("Oleg");

        //Persist the changes
        System.out.printf("%n%nUpdating...%n%n");
        update(c);
        System.out.printf("%n%nUpdate complete!%n%n");

        //Display a list of contacts after update
        System.out.printf("%n%nAfter update%n%n");
        fetchAllContacts().stream().forEach(System.out::println);

        System.out.printf("%n%nNow deleting...%n%n");
        delete(c);
        System.out.printf("%n%nDeleting complete%n%n");

        System.out.printf("%n%nAfter deleting%n%n");
        fetchAllContacts().stream().forEach(System.out::println);
    }

    private static Contact findContactById(int id) {
        //Open session
        Session session = sessionFactory.openSession();

        //Retrieve the persistent object
        Contact contact = session.get(Contact.class, id);

        //Close session
        session.close();

        //Return the object
        return contact;
    }

    private static void update(Contact contact) {
        //Open session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        // Use the session to update
        session.update(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();

    }

    private static List<Contact> fetchAllContacts() {
        //Open a session
        Session session = sessionFactory.openSession();

        //Create CriteriaBuilder

        CriteriaBuilder builder = session.getCriteriaBuilder();

        //Create CriteriaQuery
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        Root<Contact> contact = criteria.from(Contact.class);

        criteria.select(contact);

        TypedQuery<Contact> contactsQuery = session.createQuery(criteria);

        List<Contact> contacts = contactsQuery.getResultList();

        //Close the session

        session.close();

        return contacts;
    }

    private static int save(Contact contact) {
        //Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        //Use the session to save the contact
        int id = (int) session.save(contact);

        //Commit the transaction
        session.getTransaction().commit();

        //CLose the session
        session.close();

        return id;
    }

    private static void delete(Contact contact) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        session.delete(contact);

        session.getTransaction().commit();

        session.close();
    }
}
