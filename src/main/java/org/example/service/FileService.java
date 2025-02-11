package org.example.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.UserDao;

import java.io.*;
import java.nio.file.Paths;
import java.util.UUID;


public class FileService {

    private final String path;

    public FileService(UserDao userDao, String path) {
        this.path = path;
    }

    public void downloadFile(String fileName, HttpServletResponse response) throws IOException, ServletException {
        String filePath = path + File.separator + fileName;
        File imageFile = new File(filePath);
        FileInputStream fis = new FileInputStream(imageFile);
        OutputStream os = response.getOutputStream();
        try {
            response.setContentType("image/*");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }catch (FileNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + e.getMessage());
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading file: " + e.getMessage());
        } finally {
            try {
                fis.close();
                os.close();
            } catch (IOException ignore) {}
        }

    }


    public void deleteFile(String fileName){
        File imageFile = new File(path + File.separator + fileName);
        if(imageFile.exists()) {
            imageFile.delete();
        }

    }
}

