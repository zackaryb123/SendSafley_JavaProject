package com.sendsafely;

import com.sendsafely.dto.PackageURL;
import com.sendsafely.dto.UserInformation;
import com.sendsafely.enums.CountryCode;
import com.sendsafely.exceptions.ApproverRequiredException;
import com.sendsafely.exceptions.CreatePackageFailedException;
import com.sendsafely.exceptions.DeletePackageException;
import com.sendsafely.exceptions.FileOperationFailedException;
import com.sendsafely.exceptions.FinalizePackageFailedException;
import com.sendsafely.exceptions.LimitExceededException;
import com.sendsafely.exceptions.RecipientFailedException;
import com.sendsafely.exceptions.UpdateRecipientFailedException;
import com.sendsafely.exceptions.UploadFileException;
import com.sendsafely.exceptions.UserInformationFailedException;
import com.sendsafely.file.DefaultFileManager;
import com.sendsafely.file.FileManager;

import java.io.IOException;

public class Helper {
    public static SendSafely initializeAPI() {
        return new SendSafely("https://app.sendsafely.com", "vDl02vL_FbYVcMLoGvAAZA", "fWl5VRuA65W-CcqzA2Ct4w");
    }

    public static UserInformation verifyCredentials(SendSafely sendSafely) {
        UserInformation userInformation = null;
        try {
            userInformation = sendSafely.getUserInformation();
            System.out.println("\n-- Connected to SendSafely as user " + userInformation.getEmail());
        } catch (UserInformationFailedException e) {
            System.out.println("\n~~ Unable to verify user information!" +
                    "\n-- " + e.getError());
        }
        return userInformation;
    }

    public static Package createNewPackage(SendSafely sendSafely) {
        Package pkgInfo = null;
        try {
            pkgInfo = sendSafely.createPackage();
            System.out.println("\n-- Created new empty package with Package ID" + pkgInfo.getPackageId());
        } catch (CreatePackageFailedException | LimitExceededException e) {
            System.out.println("\n~~ Unable to Created new empty package!" +
                    "\n~~ " + e.getMessage());
        }
        return pkgInfo;
    }

    public static Recipient addRecipient(SendSafely sendSafely, Package pkgInfo, String email) {
        Recipient recipient = null;
        if (Utils.resolve(() -> pkgInfo.getPackageId()).isPresent()) {
            try {
                recipient = sendSafely.addRecipient(pkgInfo.getPackageId(), email);
                System.out.println("\n-- Added new recipient (Id# " + recipient.getRecipientId() + ")");
            } catch (LimitExceededException | RecipientFailedException e) {
                System.out.println("\n~~ Unable to add new recipient!" +
                        "\n~~ " + e.getMessage());
            }
        }
        return recipient;
    }

    public static void addRecipientPhoneNumber(SendSafely sendSafely, Package pkgInfo, Recipient newRecipient, String phonenumber) {
        if (Utils.resolve(() -> newRecipient.getRecipientId()).isPresent()) {
            try {
                sendSafely.addRecipientPhonenumber(pkgInfo.getPackageId(), newRecipient.getRecipientId(), phonenumber, CountryCode.US);
                System.out.println("\n-- Added SMS number for Recipient Id# " + newRecipient.getRecipientId() + ")");
            } catch (UpdateRecipientFailedException e) {
                System.out.println("\n~~ Unable to add recipient phone number!" +
                        "\n~~ " + e.getError());
            }
        }
    }

    public static void removeRecipient(SendSafely sendSafely, Package pkgInfo, Recipient recipient) {
        try {
            sendSafely.removeRecipient(pkgInfo.getPackageId(), recipient.getRecipientId());
        } catch (RecipientFailedException e) {
            System.out.println("\n~~ Unable to add new recipient!" +
                    "\n~~ " + e.getError());
        }
    }

    public static File addFileToPackage(SendSafely sendSafely, Package pkgInfo, String filepath) {
        File addedFile = null;
        try {
            FileManager fileManager = new DefaultFileManager(new java.io.File(filepath));
            addedFile = sendSafely.encryptAndUploadFile(pkgInfo.getPackageId(), pkgInfo.getKeyCode(), fileManager, new Progress());
            System.out.println("\n-- File was added with Id# " + addedFile.getFileId());
        } catch (IOException | UploadFileException | LimitExceededException e) {
            System.out.println("\n~~ Unable to add file to package!" +
                    "\n~~ " + e.getMessage());
        }
        return addedFile;
    }

    public static void removeFileFromPackage(SendSafely sendSafely, Package pkgInfo, File file) {
        try {
            sendSafely.deleteFile(pkgInfo.getPackageId(), pkgInfo.getRootDirectoryId(), file.getFileId());
        } catch (FileOperationFailedException e) {
            System.out.println("\n~~ Unable to delete file from package!" +
                    "\n~~ " + e.getError());        }
    }

    public static PackageURL finalizePackage(SendSafely sendSafely, Package pkgInfo, boolean doNotify) {
        PackageURL pURL = null;
        try {
            pURL = sendSafely.finalizePackage(pkgInfo.getPackageId(), pkgInfo.getKeyCode(), doNotify);
            System.out.println("\n-- Package was finalized. The package can be downloaded from the following URL: \n" + pURL.getSecureLink());
            System.out.println("\n-- Notify package recipients status: " + pURL.getNotificationStatus());
        } catch (LimitExceededException | ApproverRequiredException | FinalizePackageFailedException e) {
            System.out.println("\n~~ Unable to finalize package!" +
                    "\n~~ " + e.getMessage());
        }
        return pURL;
    }

    public static void deletePackage(SendSafely sendSafely, Package pkgInfo, PackageURL pURL) {
        try {
            sendSafely.deleteTempPackage(pkgInfo.getPackageId());
            System.out.println("\n-- Temp package " + pURL.getSecureLink() + " was deleted.");
        } catch (DeletePackageException e) {
            System.out.println("\n~~ Unable to delete package!" +
                    "\n~~ " + e.getError());
        }
    }
}
