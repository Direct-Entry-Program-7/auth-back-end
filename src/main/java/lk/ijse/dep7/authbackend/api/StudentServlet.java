package lk.ijse.dep7.authbackend.api;

import jakarta.annotation.Resource;
import jakarta.json.Json;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep7.authbackend.dto.StudentDTO;
import lk.ijse.dep7.authbackend.security.SecurityContext;
import lk.ijse.dep7.authbackend.service.StudentService;
import lk.ijse.dep7.authbackend.service.UserService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentServlet", value = "/students", loadOnStartup = 0)
public class StudentServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/cp")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try (Connection connection = dataSource.getConnection()) {

            /* Todo: remove in the future */
            SecurityContext.setPrincipal(new UserService(connection).authenticate("admin", "admin"));

            StudentService studentService = new StudentService(connection);
            List<StudentDTO> students = studentService.getAllStudents();

            response.setContentType("application/json");
            response.getWriter().println(JsonbBuilder.create().toJson(students));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getContentType()== null || !request.getContentType().startsWith("application/json")){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid request (Only accept JSON)");
            return;
        }

        try {
            StudentDTO studentDTO = JsonbBuilder.create().fromJson(request.getReader(), StudentDTO.class);

            if (studentDTO.getId() != null ){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID can't be specified when saving");
                return;
            }else if (studentDTO.getName() == null || !studentDTO.getName().trim().matches("^[A-Za-z ]+$")){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student name");
                return;
            }else if(studentDTO.getAddress() == null || studentDTO.getAddress().trim().length() < 3){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student address");
                return;
            }

            try (Connection connection = dataSource.getConnection()) {

                /* Todo: remove in the future */
                SecurityContext.setPrincipal(new UserService(connection).authenticate("admin", "admin"));

                StudentService studentService = new StudentService(connection);
                String id = studentService.saveStudent(studentDTO);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().println(JsonbBuilder.create().toJson(id));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }catch (JsonbException e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");;
            e.printStackTrace();
        }


    }
}
