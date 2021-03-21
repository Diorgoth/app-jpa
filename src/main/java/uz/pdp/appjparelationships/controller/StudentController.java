package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.FacultyDto;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;

    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {

        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;


    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                     @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<Student> allByGroup_faculty_id = studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);

        return allByGroup_faculty_id;

    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                  @RequestParam int page) {


        Pageable pageable = PageRequest.of(page,10);

        Page<Student> allByGroup_id = studentRepository.findAllByGroup_Id(groupId, pageable);

        return allByGroup_id;

    }

    @PostMapping("/post")
    public String addStudents(@RequestBody StudentDto studentDto){

        Student student = new Student();

        student.setFirstName(studentDto.getFirstName());

        student.setLastName(studentDto.getLastName());

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroup_id());

        if (!optionalGroup.isPresent()){

            return "Such Group not found";

        }

        student.setGroup(optionalGroup.get());

        Address address = new Address();

        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());

        Address savedAddress = addressRepository.save(address);

        student.setAddress(savedAddress);

         List<Integer> subjectList = studentDto.getSubjectList();

         List<Subject> subjectSet = new ArrayList<>();

        for (Integer subjectId:subjectList) {

            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

            if (optionalSubject.isPresent()){

                 subjectSet.add(optionalSubject.get());

            }

        }

        student.setSubjects(subjectSet);

        studentRepository.save(student);

        return "Student added";



    }


    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (!optionalStudent.isPresent()){

            return "Student not found";
        }

        Student student = optionalStudent.get();


        student.setFirstName(studentDto.getFirstName());

        student.setLastName(studentDto.getLastName());

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroup_id());

        if (!optionalGroup.isPresent()){

            return "Such Group not found";

        }

        student.setGroup(optionalGroup.get());

        Address address = new Address();

        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());

        Address savedAddress = addressRepository.save(address);

        student.setAddress(savedAddress);

        List<Integer> subjectList = studentDto.getSubjectList();

        List<Subject> subjectSet = new ArrayList<>();

        for (Integer subjectId:subjectList) {

            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);

            if (optionalSubject.isPresent()){

                subjectSet.add(optionalSubject.get());

            }

        }

        student.setSubjects(subjectSet);

        studentRepository.save(student);

        return "Student edited";


    }


    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        try {
             studentRepository.deleteById(id);
            return "Student deleted";
        } catch (Exception e) {
            return "Error in deleting";
        }
    }






}
