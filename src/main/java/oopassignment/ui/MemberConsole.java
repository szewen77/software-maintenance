package oopassignment.ui;

import java.util.List;
import oopassignment.domain.member.MemberRecord;
import oopassignment.service.MemberService;
import oopassignment.util.ApplicationContext;

import static oopassignment.ui.BootsDo.*;

public class MemberConsole {

    private static final MemberService service = ApplicationContext.MEMBER_SERVICE;

    public static void showMenu() {
        int opt;
        do {
            System.out.println("""
                           
                           MEMBER MENU
                           1. Display
                           2. Register
                           3. Search
                           4. Top Up  
                           5. Return to Main Menu
                           """);
            opt = readIntInRange("Option > ", 1, 5);
            switch (opt) {
                case 1 -> display();
                case 2 -> register();
                case 3 -> search();
                case 4 -> topUp();
                case 5 -> { }
                default -> System.out.println(ANSI_RED + "Invalid input, please enter a number between 1 to 5 !!!" + ANSI_BLACK);
            }
        } while (opt != 5);
    }
    

    private static void display() {
        List<MemberRecord> members = service.findAll();
        if (members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }
    
        String border   = "+----------+----------------+----------------------+------------+------------+";
        String headerFm = "| %-8s | %-14s | %-20s | %-10s | %-10s |%n";
        String rowFm    = "| %-8s | %-14s | %-20s | %10.2f | %-10s |%n";
    
        System.out.println();
        System.out.println("Members");
        System.out.println(border);
        System.out.printf(headerFm, "ID", "IC", "Name", "Credit", "Status");
        System.out.println(border);
    
        for (MemberRecord m : members) {
            System.out.printf(
                rowFm,
                m.getMemberId(),
                service.maskIc(m.getIcNumber()),
                m.getName(),
                m.getCreditBalance(),
                m.getStatus()
            );
        }
    
        System.out.println(border);
    }
    

    private static void register() {
        String name = readRequiredLine("\nEnter name : ");
        String ic = readRequiredLine("Enter IC No : ");
        double credit = readDouble("Enter initial credit (>=0) : ", 0.0);
        try {
            MemberRecord member = service.registerMember(name, ic, credit);
            System.out.println("Member registered with ID " + member.getMemberId());
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }

    private static void search() {
        System.out.println("""
               
               Select a field to search
               1. Member ID
               2. Member IC
               3. Name contains
               """);
        int field = readIntInRange("Field > ", 1, 3);
        switch (field) {
            case 1 -> {
                String id = readRequiredLine("Enter member ID: ");
                service.findById(id).ifPresentOrElse(MemberConsole::printMember,
                        () -> System.out.println(ANSI_RED + "Not found" + ANSI_BLACK));
            }
            case 2 -> {
                String ic = readRequiredLine("Enter IC: ");
                service.findByIc(ic).ifPresentOrElse(MemberConsole::printMember,
                        () -> System.out.println(ANSI_RED + "Not found" + ANSI_BLACK));
            }
            case 3 -> {
                String keyword = readRequiredLine("Enter keyword: ");
                List<MemberRecord> matches = service.searchByName(keyword);
                if (matches.isEmpty()) {
                    System.out.println("No matches");
                } else {
                    matches.forEach(MemberConsole::printMember);
                }
            }
            default -> System.out.println(ANSI_RED + "Invalid field" + ANSI_BLACK);
        }
    }

    private static void topUp() {
        String id = readRequiredLine("Enter member ID: ");
        double amount = readDouble("Enter amount to top up: ", 0.01);
        try {
            MemberRecord updated = service.topUpCredit(id, amount);
            System.out.printf("New balance: %.2f%n", updated.getCreditBalance());
        } catch (Exception e) {
            System.out.println(ANSI_RED + e.getMessage() + ANSI_BLACK);
        }
    }

    private static void printMember(MemberRecord m) {
        System.out.printf("ID: %s, Name: %s, IC: %s, Credit: %.2f, Status: %s%n",
                m.getMemberId(), m.getName(), service.maskIc(m.getIcNumber()),
                m.getCreditBalance(), m.getStatus());
    }
}
