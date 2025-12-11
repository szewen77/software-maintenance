package oopassignment.domain.member;

import java.time.LocalDate;

public class MemberRecord {
    private String memberId;
    private String name;
    private String icNumber;
    private double creditBalance;
    private LocalDate joinDate;
    private MemberStatus status;

    public MemberRecord(String memberId, String name, String icNumber, double creditBalance,
                        LocalDate joinDate, MemberStatus status) {
        this.memberId = memberId;
        this.name = name;
        this.icNumber = icNumber;
        this.creditBalance = creditBalance;
        this.joinDate = joinDate;
        this.status = status;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public double getCreditBalance() {
        return creditBalance;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }
}
