package com.rabobank.isd.ecom.mybatis.person;

import com.rabobank.isd.ecom.mybatis.product.Product;
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
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Jamie Craane
 */
public class ProductTest {
    private SqlSessionFactory sessionFactory;

    @Before
    public void setUp() throws IOException, SQLException, ClassNotFoundException {
        String resource = "SessionFactory.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        sessionFactory = new SqlSessionFactoryBuilder().build(reader);

        dropTables();
        createTables();
        insertProducts();
    }

    private void dropTables() {
        dropTable("product");
        dropTable("baseproduct");
        dropTable("dvd");
        dropTable("box");
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
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("create table product(" +
                "id integer not null, " +
                "type varchar(10) not null, " +
                "title varchar(40), " +
                "weight integer);");

        sql.execute("create table baseproduct(id integer not null, type varchar(10) not null);");
        sql.execute("create table dvd(parent_id integer not null, title varchar(40));");
        sql.execute("create table box(parent_id integer not null, weight integer);");
    }

    private void insertProducts() throws SQLException, ClassNotFoundException {
        Sql sql = Sql.newInstance("jdbc:hsqldb:mem:aname", "sa", "", "org.hsqldb.jdbcDriver");
        sql.execute("insert into product(id,type,title) values(?,?,?);", new Object[]{0, "DVD", "The Matrix"});
        sql.execute("insert into product(id,type,weight) values(?,?,?);", new Object[]{1, "BOX", 300});

        sql.execute("insert into baseproduct(id, type) values(?,?)", new Object[]{1, "DVD"});
        sql.execute("insert into dvd(parent_id, title) values(?,?)", new Object[]{1, "The Matrix"});
        sql.execute("insert into baseproduct(id, type) values(?,?)", new Object[]{2, "BOX"});
        sql.execute("insert into box(parent_id, weight) values(?,?)", new Object[]{2, 300});
    }


    @Test
    public void getProductsSingleTable() throws SQLException {
        SqlSession session = sessionFactory.openSession();

        try {
            List<Product> products = session.selectList("getProductsSingleTableHierarchy");
            assertEquals(2, products.size());
        } finally {
            session.close();
        }
    }

    @Test
    public void getProductsMultipleTables() {
        SqlSession session = sessionFactory.openSession();

        try {
            List<Product> products = session.selectList("selectProductsMultipleTablesHierarchy");
            assertEquals(2, products.size());
        } finally {
            session.close();
        }

    }
}
