package lk.ijse.dep7.authbackend.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep7.authbackend.dto.UserDTO;
import lk.ijse.dep7.authbackend.service.UserService;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "UserServlet", value = "/users")
public class UserServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/cp")
    private DataSource dataSource;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Request (Only support JSON)");
            return;
        }

        try {
            UserDTO userDTO = JsonbBuilder.create().fromJson(request.getReader(), UserDTO.class);

            if (userDTO.getUsername() == null || userDTO.getUsername().trim().length() < 3) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid username");
                return;
            } else if (userDTO.getFullName() == null || !userDTO.getFullName().trim().matches("^[A-Za-z ]+$")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid full name");
                return;
            } else if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid password");
                return;
            }

            try (Connection connection = dataSource.getConnection()) {
                UserService userService = new UserService(connection);
                userService.saveUser(userDTO);

                response.setStatus(HttpServletResponse.SC_CREATED);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (JsonbException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
        }

    }
}
