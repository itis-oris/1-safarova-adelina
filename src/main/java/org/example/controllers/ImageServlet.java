package org.example.controllers;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.FileService;
import org.example.service.UserService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
@WebServlet("/image")
public class ImageServlet extends HttpServlet {

    FileService fileService;
    UserService userService;
    String path;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();

        userService = (UserService) context.getAttribute("userService");
        fileService = (FileService) context.getAttribute("fileService");
        path = (String) getServletContext().getAttribute("path");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String file = request.getParameter("file");

        fileService.downloadFile(file, response);

    }

}

