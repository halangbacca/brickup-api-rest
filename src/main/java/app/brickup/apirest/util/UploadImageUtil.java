package app.brickup.apirest.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class UploadImageUtil {
    public static String imagePath;

    public static boolean uploadImage(MultipartFile image) {
        if (!image.isEmpty()) {
            String fileName = image.getOriginalFilename();
            try {
                String folderName = "src/main/resources/img-uploads";
                File dir = new File(folderName);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File serverFile = new File(dir.getCanonicalPath() + File.separator + fileName);

                imagePath = serverFile.getCanonicalPath();

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream((serverFile)));

                stream.write(image.getBytes());
                stream.close();

                System.out.println("File uploaded to: " + serverFile.getAbsolutePath());

                return true;
            } catch (Exception e) {
                System.out.println("File not uploaded!");
            }

        } else {
            System.out.println("The file is empty!");
        }
        return false;
    }
}
