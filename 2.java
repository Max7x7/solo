import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveDownload {
    private static final String APPLICATION_NAME = "Your Application Name";
    private static final JsonFactory JSON_FACTORY = com.google.api.client.json.JsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_JSON_FILE = "/home/rompvmrix/cedar-gift-398323-f00c59996387.json"; 
    private static final String DOWNLOAD_DIRECTORY = "/home/rompvmrix/; 

    public static void main(String[] args) throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                GoogleDriveDownload.class.getResourceAsStream(SERVICE_ACCOUNT_JSON_FILE)
        ).createScoped(Collections.singleton(DriveScopes.DRIVE));

        Drive driveService = new Drive.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String folderId = "1-0IF5v1HvGEzxYZ3iW5AfeZYhwm-tPvB"; // Замените на ваш ID папки
        downloadFilesFromFolder(driveService, folderId);
    }

    private static void downloadFilesFromFolder(Drive service, String folderId) throws IOException {
        FileList result = service.files().list()
                .setQ("'" + folderId + "' in parents")
                .setFields("files(id, name)")
                .execute();

        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            for (File file : files) {
                String fileId = file.getId();
                String fileName = file.getName();
                OutputStream outputStream = new FileOutputStream(DOWNLOAD_DIRECTORY + fileName);

                service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

                System.out.println("Downloaded: " + fileName);
            }
        }
    }
}
