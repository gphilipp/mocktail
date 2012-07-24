package org.mocktail.mock.jdbc.user;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.sql.Driver;

import javax.sql.DataSource;

import lombok.Cleanup;

import org.hsqldb.jdbcDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mocktail.MethodMocktail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class UserDaoTest {

    private JdbcTemplate jdbcTemplate;
    UserDao userDao;
    
    

    @Before
    public void setup() {
        userDao = new UserDao();
        jdbcTemplate = userDao.getJdbcTemplate();
        jdbcTemplate.update("insert into userdetail  values(1,10)");

    }

    @BeforeClass
    public static void cleanRecordings() {
        Driver driver = new jdbcDriver();
        DataSource dataSource = new SimpleDriverDataSource(driver,
                "jdbc:hsqldb:mem:mypersistence;user=sa");
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute("CREATE TABLE USERDETAIL (id INTEGER,age INTEGER)");

    }

    @Test
    public void testGetUser() {

        // search with recording mode
        UserDetail userDetail = userDao.get(1L);
        assertNotNull(userDetail);
        assertThat(10, is(userDetail.getAge()));

        // update userdetail record with new value
        jdbcTemplate.update("update userdetail set age=12 where id=1");

        // search again
        UserDetail recordedUserDetail = userDao.get(1L);
        assertEquals(10, recordedUserDetail.getAge());
    }

    @Test
    public void testInsertUser() {
        int count = getNumRows();

        assertEquals(1, count);
        UserDetail userDetail = new UserDetail();
        userDetail.setAge(20);
        userDetail.setId(2);

        // insert the row in recording mode
        userDao.save(userDetail);
        count = getNumRows();

        assertEquals(2, count);

        userDetail.setAge(20);
        userDetail.setId(3);
        userDao.save(userDetail);

        count = getNumRows();

        assertEquals(2, count);
    }

    @Test
    public void testUpdateUser() {
        int count = getNumRows();

        assertEquals(1, count);
        UserDetail userDetail = new UserDetail();
        userDetail.setAge(30);
        userDetail.setId(1);

        // update the row in recording mode
        int affectedRows = userDao.update(userDetail);

        assertEquals(1, affectedRows);

        int age = jdbcTemplate
                .queryForInt("select age from userdetail where id=1");
        assertEquals(30, age);

        userDetail.setAge(40);
        userDetail.setId(1);
        userDao.update(userDetail);

        age = jdbcTemplate.queryForInt("select age from userdetail where id=1");

        assertEquals(30, age);
    }

    @Test
    public void testDeleteUser() {
        int count = getNumRows();

        assertEquals(1, count);
        jdbcTemplate.update("insert into USERDETAIL values (2,20)");

        count = getNumRows();

        assertEquals(2, count);

        UserDetail userDetail = new UserDetail();
        userDetail.setId(2);
        // insert the row in recording mode
        userDao.delete(userDetail);

        count = getNumRows();

        assertEquals(1, count);

        userDetail.setId(1);

        userDao.delete(userDetail);

        count = getNumRows();

        assertEquals(1, count);
    }

    @Test
    public void testMethodBasedRecording() {
        @Cleanup MethodMocktail methodMocktail = new MethodMocktail();
        methodMocktail.setUp(this);
        
        UserDetail userDetail = userDao.get(1L);
        assertNotNull(userDetail);
        assertThat(10, is(userDetail.getAge()));

        userDetail = new UserDetail();
        userDetail.setAge(20);
        userDetail.setId(2);

        // insert the row in recording mode
        userDao.save(userDetail);

        userDetail = userDao.get(1L);
        assertNotNull(userDetail);
        assertThat(10, is(userDetail.getAge()));
    }
    
    private int getNumRows() {
        return jdbcTemplate.queryForInt("select count(*) from userdetail");
    }

    @After
    public void tearDown() {
        jdbcTemplate.update("delete from USERDETAIL");
    }
}
