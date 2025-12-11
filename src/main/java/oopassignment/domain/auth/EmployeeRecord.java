package oopassignment.domain.auth;

public class EmployeeRecord {
    private String id;
    private String username;
    private String passwordHash;
    private Role role;
    private double baseSalary;
    private double bonusRate;
    private String uplineId;
    private EmploymentStatus status;

    public EmployeeRecord(String id, String username, String passwordHash, Role role, double baseSalary,
                          double bonusRate, String uplineId, EmploymentStatus status) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.baseSalary = baseSalary;
        this.bonusRate = bonusRate;
        this.uplineId = uplineId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public double getBonusRate() {
        return bonusRate;
    }

    public String getUplineId() {
        return uplineId;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return EmploymentStatus.ACTIVE.equals(status);
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public void setBonusRate(double bonusRate) {
        this.bonusRate = bonusRate;
    }

    public void setUplineId(String uplineId) {
        this.uplineId = uplineId;
    }

    public void setStatus(EmploymentStatus status) {
        this.status = status;
    }
}
