package lk.ijse.dep7.authbackend.api;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "StudentServlet", value = "/students", loadOnStartup = 0)
public class StudentServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/cp")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Do POST");
    }
}
