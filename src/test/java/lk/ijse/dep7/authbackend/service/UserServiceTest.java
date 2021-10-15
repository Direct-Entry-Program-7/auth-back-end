package lk.ijse.dep7.authbackend.service;

import lk.ijse.dep7.authbackend.dto.UserDTO;
import lk.ijse.dep7.authbackend.security.SecurityContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static Connection connection;

    @BeforeAll
    static void beforeAll() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep7_auth_sms", "root", "mysql");
        userService = new UserService(connection);
    }

    @AfterAll
    static void afterAll() throws SQLException {
        connection.close();
    }

    @Test
    void saveUser() {
        assertDoesNotThrow(()-> {
            userService.saveUser(new UserDTO("guest", "guest", "Guest User"));
        });
    }

    @Test
    void authenticate() {
        assertDoesNotThrow(()->{
            assertNotNull(userService.authenticate("admin", "admin"));
            assertNotNull(userService.authenticate("guest", "guest"));
        });
        assertThrows(RuntimeException.class, ()-> userService.authenticate("test", "something"));
    }
}