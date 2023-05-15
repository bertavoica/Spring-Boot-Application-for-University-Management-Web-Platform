package com.cti.utils;

import com.cti.models.EEducationCycle;
import com.cti.models.ELanguage;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.io.*;

public class Utils {

    public static final String ASSIGNMENTS_FOLDER = "Assignments";

    public static Map<String, Map<ELanguage, String>> languageDictionary = new HashMap<String, Map<ELanguage, String>>() {{
        put("UserNotFound", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Error: User not found with username");
            put(ELanguage.ENGLISH, "Eroare: Utilizatorul nu a fost găsit");
        }});
        put("UsernameIsStudent", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Utilizatorul este un student");
            put(ELanguage.ENGLISH, "Username is a student");
        }});
        put("UsernameNotExist", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Utilizatorul nu există");
            put(ELanguage.ENGLISH, "Username does not exist");
        }});
        put("UsernameAlreadyResponsible", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Utilizatorul este deja responsabil pentru acest curs");
            put(ELanguage.ENGLISH, "Username is already responsible for this course");
        }});
        put("UpdatedLanguage", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Actualizare cu succes a limbii pentru");
            put(ELanguage.ENGLISH, "Successfully updated language for");
        }});
        put("UserNoLanguage", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Utilizatorul nu are setata o limba preferata");
            put(ELanguage.ENGLISH, "User does not have a certain language set");
        }});
        put("UserExist", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Utilizatorul există deja in baza de date");
            put(ELanguage.ENGLISH, "User already exist in the database");
        }});
        put("StudentExist", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Există deja un student cu numele de utilizator");
            put(ELanguage.ENGLISH, "There is already a student with username");
        }});
        put("StudentNotExists", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Studentul cu următorul nume de utilizator nu există:");
            put(ELanguage.ENGLISH, "Student with the following username does not exist:");
        }});
        put("StudentAlreadyEnrolled", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Studentul este deja inrolat in acest curs");
            put(ELanguage.ENGLISH, "Student is already enrolled in this course");
        }});
        put("StudentNotEnrolled", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Studentul nu este înscris la curs");
            put(ELanguage.ENGLISH, "Student is not enrolled in this course");
        }});
        put("StudentRemovedFromCourse", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Studentul a fost eliminat de la curs");
            put(ELanguage.ENGLISH, "Successfully removed student from course");
        }});
        put("StudentEnrolled", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Studentul a fost inrolat cu succes la acest curs");
            put(ELanguage.ENGLISH, "Successfully enrolled student in this course");
        }});

        put("TeacherExist", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Există deja un profesor cu numele de utilizator");
            put(ELanguage.ENGLISH, "There is already a teacher with username");
        }});
        put("TeacherAddedResponsible", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul a fost adaugat cu succes la cursul");
            put(ELanguage.ENGLISH, "Successfully added teacher as responsible for course");
        }});
        put("RoleNotFound", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Rolul nu există");
            put(ELanguage.ENGLISH, "Role is not found");
        }});
        put("CourseNotFound", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Cursul nu există");
            put(ELanguage.ENGLISH, "Course not found");
        }});
        put("CourseNoResponsible", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Cursul nu are niciun profesor ca responsabil");
            put(ELanguage.ENGLISH, "Course does not have teacher as responsible");
        }});
        put("CourseRemovedResponsible", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul a fost șters cu succes de la cursul");
            put(ELanguage.ENGLISH, "Successfully removed teacher as responsible from");
        }});
        put("ProjectNotFoundUser", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Proiectul nu a fost găsit pentru utilizatorul");
            put(ELanguage.ENGLISH, "Project not found for user");
        }});
        put("ProjectNotFound", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Proiectul nu a fost găsit");
            put(ELanguage.ENGLISH, "Project not found");
        }});
        put("ProjectTemplateAdded", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Sarcina schiță a fost adăugat cu succes");
            put(ELanguage.ENGLISH, "Successfully added project template");
        }});
        put("ProjectTemplateUpdated", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Sarcina schiță a fost actualiuzată cu succes");
            put(ELanguage.ENGLISH, "Successfully updated project template");
        }});
        put("ProjectTemplateDeleted", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Sarcina schiță a fost ștearsă cu succes");
            put(ELanguage.ENGLISH, "Successfully deleted project template");
        }});
        put("ErrorDownloadingAssignment", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Eroare în descărcarea sarcinii");
            put(ELanguage.ENGLISH, "Error downloading assignment");
        }});
        put("ProjectNoAssignment", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Proiectul nu are nicio sarcină încărcata");
            put(ELanguage.ENGLISH, "Project does not have any assignment uploaded");
        }});
        put("ProjectUpdated", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Proiectul a fost actualizat cu succes");
            put(ELanguage.ENGLISH, "Project was successfully updated");
        }});
        put("NoProjectForStudent", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Niciun proiect nu a fost găsit pentru studentul");
            put(ELanguage.ENGLISH, "No related project found for student");
        }});
        put("AssignmentRemoved", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Sarcina a fost ștearsă cu succes pentru");
            put(ELanguage.ENGLISH, "Successfully removed assignment from");
        }});
        put("StudentAssignedProject", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Studentul are deja asrcina asignata");
            put(ELanguage.ENGLISH, "Student already has the project assigned to him");
        }});
        put("AssignedProject", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Sarcina a fost asignata cu succes");
            put(ELanguage.ENGLISH, "Successfully assigned project to");
        }});
        put("NoStudentsInGroup", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Niciun student nu a fost gasit in grupul");
            put(ELanguage.ENGLISH, "No students found in group");
        }});
        put("StudentsGroupAlreadyAssigned", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Toți studenții din acest grup au această sarcină asignată");
            put(ELanguage.ENGLISH, "All students from this group have this project assigned to them");
        }});
        put("StudentsGroupAssigned", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Sarcina a fost asignată cu succes către un numar total de studenți");
            put(ELanguage.ENGLISH, "Successfully assigned project to the following number of students");
        }});
        put("SpecializationExists", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Există deja o specializare cu numele");
            put(ELanguage.ENGLISH, "There is already a specialization with the name");
        }});
        put("SpecializationAdded", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Specializarea a fost adaugata cu succes");
            put(ELanguage.ENGLISH, "Successfully added new specialization");
        }});
        put("SpecializationNotExists", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Specializarea nu există");
            put(ELanguage.ENGLISH, "Specialization does not exist");
        }});
        put("SpecializationUpdated", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Specializarea a fost actualizată cu succes");
            put(ELanguage.ENGLISH, "Specialization updated successsfully");
        }});
        put("SpecializationDeleted", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Specializarea a fost ștearsă cu succes");
            put(ELanguage.ENGLISH, "Specialization deleted successsfully");
        }});
        put("TeacherBelongsSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul aparține deja specilizării");
            put(ELanguage.ENGLISH, "Teacher already belongs to specialization");
        }});
        put("TeacherBelongsDifferentSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul aparține unei alte specilizări");
            put(ELanguage.ENGLISH, "Teacher belongs to a different specialization");
        }});
        put("TeacherNotBelongsSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul nu aparține specilizării");
            put(ELanguage.ENGLISH, "Teacher does not belong to specialization");
        }});
        put("TeacherNotExists", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul nu a fost găsit");
            put(ELanguage.ENGLISH, "Teacher does not exist");
        }});
        put("TeacherAddedSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul a fost adăugat cu succes specializării");
            put(ELanguage.ENGLISH, "Successfully added teacher to specialization");
        }});
        put("TeacherHasSuperiors", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul are urmatorul numar de oameni care il au pe el ca superior");
            put(ELanguage.ENGLISH, "Teacher has the following amount of people with him as superior");
        }});
        put("TeacherRemovedSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Profesorul a fost eliminat cu succes din specilizare");
            put(ELanguage.ENGLISH, "Successfully removed teacher from specialization");
        }});
        put("SuperiorNotBelongsSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Superiorul nu aparține niciunei specializări");
            put(ELanguage.ENGLISH, "Superior does not belong to any specialization");
        }});
        put("SuperiorDifferentSpecialization", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Superiorul aparține unei specilizari diferite");
            put(ELanguage.ENGLISH, "Superior belongs to a different specialization");
        }});
        put("SuperiorModified", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Superiorul a fost actualizat cu succes");
            put(ELanguage.ENGLISH, "Successfully modified teacher's superior");
        }});
        put("TitleExists", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Titlul există deja cu numele");
            put(ELanguage.ENGLISH, "Title already exists with name");
        }});
        put("TitleNotExists", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Titlul există in baza de date");
            put(ELanguage.ENGLISH, "Title does not exist in the database");
        }});
        put("TitleAdded", new HashMap<ELanguage, String>() {{
            put(ELanguage.ROMANIAN, "Titlul a fost adăugat cu succes");
            put(ELanguage.ENGLISH, "Title added successfully");
        }});
    }};


    public static void generateAssignmentsFolder() {
        File file = new File(ASSIGNMENTS_FOLDER);
        if (!file.exists())
            file.mkdirs();
    }

    public static String saveFile(MultipartFile file, String destination) throws IOException {

        File directory = new File(destination);
        if (!directory.exists())
            directory.mkdirs();

        if (file.isEmpty()) {
            return "";
        }

        byte[] bytes = file.getBytes();
        Path path = Paths.get(destination + File.separator + Utils.generateUniqueID() + "_" + file.getOriginalFilename());
        Files.write(path, bytes);
        return path.toString();
    }

    public static String generateUniqueID() {
        return UUID.randomUUID().toString();
    }

    public static EEducationCycle convertToCycle(String cycle) {
        cycle = cycle.toLowerCase(Locale.ROOT);
        if (cycle.equals("bachelor")) {
            return EEducationCycle.BACHELOR;
        } else if (cycle.equals("master")) {
            return EEducationCycle.MASTER;
        } else if (cycle.equals("doctoral")) {
            return EEducationCycle.DOCTORAL;
        }

        return EEducationCycle.BACHELOR;
    }

}
