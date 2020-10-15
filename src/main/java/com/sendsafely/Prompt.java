package com.sendsafely;

import javax.swing.*;
import java.io.File;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class Prompt {
    private final Scanner scanner = new Scanner(System.in);
    private final JFileChooser chooser = new JFileChooser();

    public enum ActionTypes {
        UNDO,
        MORE_FILES,
        NOTIFICATION,
        FINALIZE,
        DELETE
    }

    public Integer numberRecipients() {
        int count = -1;
        System.out.println("\n-> Please Enter the number of recipients to add to the package");
        while (!scanner.hasNextInt() || (count <= 0 || count > 99) ) {
            try {
                count = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
            }
            if ((count >= 0 && count < 99)) {
                scanner.nextLine();
                break;
            }
            System.out.println("-> Invalid entry! Enter a number between 0 and 99 : ");
        }
        System.out.println("\n-- You entered " + count + " recipients");
        return count;
    }

    public String recipient(){
        System.out.println("\n-> Please enter a recipient email : ");
        String recipient = null;
        boolean isValid = false;
        while (scanner.hasNextLine() && !isValid || recipient == null) {
            recipient = scanner.nextLine();
            isValid = Utils.isValidEmail(recipient);
            if (isValid)  {
                break;
            }

            System.out.println("\n-> Invalid entry! Make sure this is a valid email address");
        }
        System.out.println("\n-- You entered recipient : " + recipient);
        return recipient;
    }

    public String recipientPhoneNumber() {
        System.out.println("\n-> Please enter recipient phone number : ");
        String phonenumber = null;
        boolean isValid = false;
        while (!scanner.hasNextLong() && !isValid || phonenumber == null) {
            try {
                phonenumber = String.valueOf(scanner.nextLong());
                isValid = Utils.isValidPhone(phonenumber);
            } catch (InputMismatchException e) {
                scanner.nextLine();
                isValid = false;
            }
            if (isValid) {
                scanner.nextLine();
                break;
            }
            System.out.println("\n-> Invalid entry! Make sure this is a valid phone number " +
                    "\n(10 digits no separators)");
        }
        System.out.println("\n-- You entered recipient : " + phonenumber);
        return phonenumber;
    }

    public String chooseFile() {
        System.out.println("\n-> Please select a file to upload");
        chooser.showOpenDialog(null);
        String filepath = null;
        if (Utils.resolve(() -> chooser.getSelectedFile()).isPresent()) {
            File file = chooser.getSelectedFile();
            System.out.println("\n-- You have selected file : " + file.getName());
            filepath = file.getPath();
        }
        return filepath;
    }

    public boolean action(String type) {
        switch (ActionTypes.valueOf(type)) {
            case UNDO:
                System.out.println("\n-> Would you like to undo last action? (Yes/No)");
                break;
            case MORE_FILES:
                System.out.println("\n-> Would you like to add another file? (Yes/No)");
                break;
            case NOTIFICATION:
                System.out.println("\n-> Would you like to send recipient email notification? (Yes/No)");
                break;
            case FINALIZE:
                System.out.println("\n-> Would you like to finalize the package? (Yes/No)");
                break;
            case DELETE:
                System.out.println("\n-> Would you like to delete package? (Yes/No)");
                break;
            default:
                System.out.println("\n-- Unrecognized action!");
                break;
        }
        String action = null;
        boolean isValid = true;
        while (scanner.hasNextLine() && !isValid || action == null) {
            action = scanner.nextLine();
            isValid = Objects.nonNull(action) && (action.equalsIgnoreCase("YES") || action.equalsIgnoreCase("NO"));
            if (isValid) break;
        }
        return action.equalsIgnoreCase("YES");
    }
}
