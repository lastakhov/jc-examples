package com.rabobank.isd.ecom.mybatis.person;

import groovy.sql.Sql;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Jamie Craane
 */
public class PersonTest {
    private SqlSessionFactory sessionFactory;

    @Before
    public void setUp() throws IOException, SQLException, ClassNotFoundException {
        String resource = "SessionFactory.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        sessionFactory = new SqlSessionFactoryBuilder().build(reader);

        dropTables();
        createTables();
        insertAccounts();
        insertPersons();
        insertAddresses();
    }

    private void dropTables() {
        dropTable("person");
        dropTable("account");
        dropTable("address");
    }

    private void dropTable(String table) {
        try {
            Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
            sql.execute("drop table " + table);
        } catch (Exception e) {
            // Table does not exist.
        }
    }

    private void createTables() throws SQLException, ClassNotFoundException {
        createPersonTable();
        createAddressTtable();
        createAccountTable();
    }

    private void createPersonTable() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("create table person(" +
                "id integer not null, " +
                "first_name varchar(40) not null, " +
                "last_name varchar(40) not null, " +
                "account_id integer, " +
                "age integer not null);");
    }

    private void createAccountTable() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("create table account(id integer not null, name varchar(40) not null);");
    }

    private void createAddressTtable() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("create table address(id integer not null, street varchar(40) not null, person_id integer not null);");
    }


    private void insertAccounts() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("insert into account(id, name) values(?, ?);", new Object[]{0, "Test Account"});
    }

    private void insertPersons() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("insert into person(id, first_name, last_name, account_id, age) values(?, ?, ?, ?, ?);", new Object[]{0, "Jan", "Janssen", 0, 20});
        sql.execute("insert into person(id, first_name, last_name, account_id, age) values(?, ?, ?, ?, ?);", new Object[]{1, "Jan2", "Janssen2", 0, 40});
    }

    private void insertAddresses() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("insert into address(id, street, person_id) values(?, ?, ?);", new Object[]{0, "Kerkstraat", 0});
        sql.execute("insert into address(id, street, person_id) values(?, ?, ?);", new Object[]{1, "Loefplein", 0});
    }


    @Test
    public void retrievePersonByid() {
        SqlSession session = sessionFactory.openSession();
        try {
            Person person = (Person) session.selectOne("selectPerson", 0);
            assertNotNull(person);
            assertEquals(0, (int) person.getId());
            assertEquals("Jan", person.getFirstName());
            assertEquals("Janssen", person.getLastName());
        } finally {
            session.close();
        }
    }

    @Test
    public void dynamicStatement() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("firstName", "Jan");

        SqlSession session = sessionFactory.openSession();
        try {
            Person person = (Person) session.selectOne("searchPerson", params);
            assertNotNull(person);
            assertEquals("Jan", person.getFirstName());
        } finally {
            session.close();
        }
    }

    @Test
    public void personWithAccount() {
        SqlSession session = sessionFactory.openSession();
        try {
            Person person = (Person) session.selectOne("selectPersonWithAccount", 0);
            assertNotNull(person);
            assertNotNull(person.getAccount());
            assertEquals("Test Account", person.getAccount().getName());
        } finally {
            session.close();
        }
    }

    @Test
    public void personWithAddresses() throws SQLException {
        SqlSession session = sessionFactory.openSession();
        try {
            Person person = (Person) session.selectOne("personWithAddresses", 0);
            assertNotNull(person);
            assertNotNull(person.getAddresses());
            assertEquals(2, person.getAddresses().size());
        } finally {
            session.close();
        }
    }

    @Test
    public void personWithAddressesEager() throws SQLException {
        SqlSession session = sessionFactory.openSession();
        try {
            Person person = (Person) session.selectOne("personWithAddressesEager", 0);
            assertNotNull(person);
            assertNotNull(person.getAddresses());
            assertEquals(2, person.getAddresses().size());
        } finally {
            session.close();
        }
    }

    @Test
    public void calculateTotalAge() {
        SqlSession session = sessionFactory.openSession();

        try {
            TotalAgeCalculator calculator = new TotalAgeCalculator();
            session.select("selectPersons", calculator);
            assertEquals(60, calculator.getTotalAge());
        } finally {
            session.close();
        }
    }
}
