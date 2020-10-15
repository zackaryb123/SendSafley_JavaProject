package com.sendsafely;

import com.sendsafely.dto.PackageURL;
import com.sendsafely.dto.UserInformation;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        Prompt prompt = new Prompt();
        Command command = new Command();

        /* Welcome Message */
        System.out.println("-- ----------------------------------------- --");
        System.out.println("-- Welcome to SendSafely Package Management! --");
        System.out.println("-- ----------------------------------------- --");

        /* Initialize the API */
        SendSafely sendSafely = Helper.initializeAPI();

        /* Verify that the API credentials are valid */
        UserInformation userInformation = Helper.verifyCredentials(sendSafely);

        if (Objects.nonNull(userInformation)) {
            /* Create a new empty package */
            Package pkgInfo = Helper.createNewPackage(sendSafely);

            if (Objects.nonNull(pkgInfo)) {
                /* -- Loop number of recipients -- */
                int numRecipients = prompt.numberRecipients();
                while (numRecipients > 0) {

                    /* Add a new recipient to the package */
                    String email = prompt.recipient();
                    Recipient recipient = Helper.addRecipient(sendSafely, pkgInfo, email);
                    if (Objects.nonNull(recipient)) {
                        /* Track actions and deincrement recipients*/
                        command.trackAction(Command.EventTypes.RECIPIENT);
                        numRecipients -= 1;

                        /* Add an SMS number for the new recipient */
                        String phonenumber = prompt.recipientPhoneNumber();
                        Helper.addRecipientPhoneNumber(sendSafely, pkgInfo, recipient, phonenumber);
                    }

                    /* prompt undo recipient */
                    if (prompt.action(Prompt.ActionTypes.UNDO.name())) {
                        command.undo(sendSafely, pkgInfo, recipient, null);
                    }
                }

                /* -- Loop number of Files to add -- */
                boolean moreFiles = true;
                while (moreFiles) {

                    /* Prompt user for file */
                    String filepath = prompt.chooseFile();
                    if (Objects.nonNull(filepath)) {

                        /* Add a local file to the package */
                        File file = Helper.addFileToPackage(sendSafely, pkgInfo, filepath);
                        if (Objects.nonNull(file)) {
                            /* Track actions */
                            command.trackAction(Command.EventTypes.FILE);

                            /* Prompt user undo file */
                            if (prompt.action(Prompt.ActionTypes.UNDO.name())) {
                                command.undo(sendSafely, pkgInfo, null, file);
                            }
                        }

                        /* Prompt user to add more files */
                        moreFiles = prompt.action(Prompt.ActionTypes.MORE_FILES.name());
                    } else {
                        moreFiles = true;
                    }
                }

                /* -- Prompt user for notifications  -- */
                boolean doNotify = prompt.action(Prompt.ActionTypes.NOTIFICATION.name());
                doNotify = !prompt.action(Prompt.ActionTypes.UNDO.name());

                /* -- Prompt user to finalize package with optional notification -- */
                PackageURL pURL = null;
                if (prompt.action(Prompt.ActionTypes.FINALIZE.name())) {
                    pURL = Helper.finalizePackage(sendSafely, pkgInfo, doNotify);
                }

                /* -- Prompt User to delete package --
                if (Objects.nonNull(pURL)) {
                    if (prompt.action(Prompt.ActionTypes.DELETE.name())) {
                        Helper.deletePackage(sendSafely, pkgInfo, pURL);
                    }
                } */
            }
        }
    }
}
