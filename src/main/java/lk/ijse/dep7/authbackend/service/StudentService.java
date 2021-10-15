package lk.ijse.dep7.authbackend.service;

import lk.ijse.dep7.authbackend.dto.StudentDTO;
import lk.ijse.dep7.authbackend.security.SecurityContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class StudentService {

    private Connection connection;

    public StudentService(Connection connection) {
        this.connection = connection;
    }

    public String saveStudent(StudentDTO student){

        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO student (name, address, username) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, student.getName());
            stm.setString(2, student.getAddress());
            stm.setString(3, SecurityContext.getPrincipal().getUsername());

            if (stm.executeUpdate() == 1){
                ResultSet rst = stm.getGeneratedKeys();

                rst.next();
                return String.format("SID-%03d",rst.getInt(1));
            }else{
                throw new RuntimeException("Failed to save the student");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StudentDTO> getAllStudents(){

        List<StudentDTO> students = new ArrayList<>();

        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT id, name, address FROM student");

            while(rst.next()){
                students.add(new StudentDTO(String.format("SID-%03d",rst.getInt("id")),
                        rst.getString("name"),
                        rst.getString("address")));
            }

            return students;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
