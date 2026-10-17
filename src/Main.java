import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StudentOperations so = new StudentOperations(sc);
        SkillOperations sk = new SkillOperations(sc);
        MatchingEngine me = new MatchingEngine();
        ExchangeOperations eo = new ExchangeOperations(sc);
        SessionOperations se = new SessionOperations(sc);
        ReviewOperations ro = new ReviewOperations(sc);

        int loggedInStudentId = -1;

        while (true) {
            System.out.println("\n================================");
            System.out.println("     CAMPUS SKILL EXCHANGE");
            System.out.println("================================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Add Skills I Can Teach");
            System.out.println("4. Add Skills I Want to Learn");
            System.out.println("5. Find Skill Matches");
            System.out.println("6. Exchange Requests");
            System.out.println("7. Exit");
            System.out.println("8. Schedule a Session");
            System.out.println("9. View Sessions");
            System.out.println("10. Submit a Review");
            System.out.println("11. View Reviews");
            System.out.println("12. Logout");
            System.out.println("================================");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter a number from 1 to 12.");
                continue;
            }

            switch (choice) {
                case 1:
                    so.registerStudent();
                    break;

                case 2:
                    int loginId = so.loginStudent();
                    if (loginId != -1) {
                        loggedInStudentId = loginId;
                    }
                    break;

                case 3:
                    if (loggedInStudentId != -1) {
                        sk.addSkill(loggedInStudentId, "TEACH");
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 4:
                    if (loggedInStudentId != -1) {
                        sk.addSkill(loggedInStudentId, "LEARN");
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 5:
                    if (loggedInStudentId != -1) {
                        me.findMatches(loggedInStudentId);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 6:
                    if (loggedInStudentId == -1) {
                        System.out.println("Please login first.");
                        break;
                    }

                    System.out.println("\n--- EXCHANGE REQUESTS ---");
                    System.out.println("1. Send Request");
                    System.out.println("2. View Incoming Requests");
                    System.out.println("3. Accept or Reject Request");
                    System.out.print("Choose an option: ");

                    String requestInput = sc.nextLine().trim();

                    switch (requestInput) {
                        case "1":
                            eo.sendRequest(loggedInStudentId);
                            break;
                        case "2":
                            eo.viewRequests(loggedInStudentId);
                            break;
                        case "3":
                            eo.respondToRequest(loggedInStudentId);
                            break;
                        default:
                            System.out.println("Invalid request option.");
                    }
                    break;

                case 7:
                    System.out.println("Thank you for using Campus Skill Exchange!");
                    sc.close();
                    return;

                case 8:
                    if (loggedInStudentId != -1) {
                        se.scheduleSession(loggedInStudentId);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 9:
                    if (loggedInStudentId != -1) {
                        se.viewSessions(loggedInStudentId);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 10:
                    if (loggedInStudentId != -1) {
                        ro.submitReview(loggedInStudentId);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 11:
                    if (loggedInStudentId != -1) {
                        ro.viewReviews(loggedInStudentId);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 12:
                    if (loggedInStudentId != -1) {
                        loggedInStudentId = -1;
                        System.out.println("Logged out successfully.");
                    } else {
                        System.out.println("You are not logged in.");
                    }
                    break;

                default:
                    System.out.println("Invalid choice! Enter a number from 1 to 12.");
            }
        }

        System.out.println("Thank you for using Campus Skill Exchange!");
        sc.close();
    }
}
