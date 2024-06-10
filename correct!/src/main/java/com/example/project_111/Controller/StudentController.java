package com.example.project_111.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.project_111.Entity.Student;
import com.example.project_111.Service.StudentService;

@RestController
@RequestMapping("/api/registration")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/register/student")
    public String registerStudent(@RequestBody Student student) {
        return studentService.save(student);
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/student/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/teacher/{teacherId}/availability")
    public List<String> getTeacherAvailability(@PathVariable Long teacherId) {
        return studentService.getTeacherAvailability(teacherId);
    }

    @PostMapping("/student/{id}/changeTiming")
    public String changeStudentTiming(@PathVariable Long id, @RequestBody String newTiming) {
        return studentService.changeStudentTiming(id, newTiming);
    }

    @GetMapping("/convertTime")
    public String convertTo12HourFormat(@RequestParam String time24) {
        return studentService.convertTo12HourFormat(time24);
    }
    @PostMapping("/student/change-timing")
    public ResponseEntity<String> changeTiming(@RequestBody ChangeTimingRequest request) {
        boolean isUpdated = studentService.changeTiming(request);
        if (isUpdated) {
            return ResponseEntity.ok("Timing changed successfully");
        } else {
            return ResponseEntity.badRequest().body("you've already change timing wait for the next day");
        }
    }
}
