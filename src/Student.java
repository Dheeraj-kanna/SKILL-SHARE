
public class Student {
    private int studentId;
    private String name;
    private String email;
    private String passwordHash;
    private String department;

    public Student(String name, String email, String passwordHash, String department) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.department = department;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDepartment() {
        return department;
    }
}
