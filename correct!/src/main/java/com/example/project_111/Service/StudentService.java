package com.example.project_111.Service;





import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.project_111.Controller.ChangeTimingRequest;
import com.example.project_111.Entity.Student;
import com.example.project_111.Entity.Teacher;
import com.example.project_111.Repository.StudentRepository;
import com.example.project_111.Repository.TeacherRepository;



@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    private static final DateTimeFormatter TIME_FORMATTER_24H = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter TIME_FORMATTER_12H = DateTimeFormatter.ofPattern("h:mm a");

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public List<String> getTeacherAvailability(Long teacherId) {
        Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            return teacher.getAvailability();
        }
        return List.of();
    }

    public boolean isTimingAvailable(String course, Long teacherId, String timing) {
        List<Student> students = studentRepository.findByCourseAndTeacherIdAndTiming(course, teacherId, timing);
        return students.isEmpty();
    }

    public boolean changeTiming(ChangeTimingRequest request) {
        Optional<Student> studentOpt = studentRepository.findById(request.getStudentId());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            LocalDateTime lastUpdatedTime = student.getLastUpdatedTime();
            
            String[] currentTimingParts = student.getTiming().split("-");
            LocalTime currentTimingStart = LocalTime.parse(currentTimingParts[0].trim(), TIME_FORMATTER_24H);

            if (isChangeAllowed(lastUpdatedTime, currentTimingStart)) {
                student.setTiming(request.getNewTiming());
                student.setLastUpdatedTime(LocalDateTime.now());
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }

   /* private boolean isChangeAllowed(LocalDateTime lastUpdatedTime) {
        return lastUpdatedTime == null || Duration.between(lastUpdatedTime, LocalDateTime.now()).toHours() >= 12;
    }*/
    private boolean isChangeAllowed(LocalDateTime lastUpdatedTime, LocalTime currentTiming) {
        if (lastUpdatedTime == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classDateTime = now.with(currentTiming);
       // if (classDateTime.isBefore(now)) {
        //	classDateTime = classDateTime.plusDays(1);
        //}
        long hoursUntilClass = Duration.between(now, classDateTime).toHours();
        
        System.out.println("Last Updated Time: " + lastUpdatedTime);
        System.out.println("Current Time: " + now);
        System.out.println("Class Start Time: " + currentTiming);
        System.out.println("Hours Before Class: " + hoursUntilClass);
        //long hoursUntilClass = Duration.between(LocalDateTime.now(), currentTiming).toHours();
        return hoursUntilClass >= 12;
    }



    public String save(Student student) {
        if (isTimingAvailable(student.getCourse(), student.getTeacherId(), student.getTiming())) {
            studentRepository.save(student);
            return "Student registered successfully!";
        } else {
            return "Selected timing is not available. Kindly select another timing.";
        }
    }

    public String changeStudentTiming(Long studentId, String newTiming) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            LocalTime currentTime = LocalTime.now();

            try {
                String[] timingParts = student.getTiming().trim().split("-");
                LocalTime studentTiming = LocalTime.parse(timingParts[0].trim(), TIME_FORMATTER_24H);

                String[] newTimingParts = newTiming.trim().split("-");
                LocalTime newTimingParsed = LocalTime.parse(newTimingParts[0].trim(), TIME_FORMATTER_24H);

                long hoursUntilClass = java.time.Duration.between(currentTime, studentTiming).toHours();

                if (hoursUntilClass < 12) {
                    return "Sorry, only possible before 12 hours.";
                }

                if (isTimingAvailable(student.getCourse(), student.getTeacherId(), newTiming)) {
                    student.setTiming(newTiming);
                    studentRepository.save(student);
                    return "Timing updated successfully!";
                } else {
                    return "No other timing available.";
                }
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return "Error parsing timing: " + e.getMessage();
            }
        } else {
            return "Student not found.";
        }
    }

    public String convertTo12HourFormat(String timing) {
        try {
            String[] timingParts = timing.trim().split("-");
            LocalTime startTime = LocalTime.parse(timingParts[0].trim(), TIME_FORMATTER_24H);
            LocalTime endTime = LocalTime.parse(timingParts[1].trim(), TIME_FORMATTER_24H);
            return startTime.format(TIME_FORMATTER_12H) + " - " + endTime.format(TIME_FORMATTER_12H);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return timing;
        }
    }
}
