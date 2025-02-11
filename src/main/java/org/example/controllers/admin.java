package org.example.controllers;

import com.oracle.wls.shaded.org.apache.bcel.generic.IfInstruction;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.PreferenceDao;
import org.example.dao.RecipeDao;
import org.example.dao.UserDao;
import org.example.entity.Preference;
import org.example.entity.Recipe;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.util.DbException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin")
public class admin extends HttpServlet {
    private RecipeDao recipeDao;

    private PreferenceDao preferenceDao;
    private UserService userService;
    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        recipeDao = (RecipeDao) getServletContext().getAttribute("recipeDao");
        preferenceDao = (PreferenceDao) getServletContext().getAttribute("preferenceDao");
        userService = (UserService) getServletContext().getAttribute("userService");
        userDao = (UserDao) getServletContext().getAttribute("userDao");

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> preferences = preferenceDao.getPreferences();
        request.setAttribute("preferences", preferences);

        List<User> users = userDao.getUsers();
        String query = request.getParameter("query");
        if (query != null){
            users = userDao.search(query);
        }
        request.setAttribute("users", users);

        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String newCategory = request.getParameter("newCategory");
        String deleteCategoryName = request.getParameter("deleteCategoryName");
        String revokeRightsId = request.getParameter("revokeRights");
        String grantRightsId = request.getParameter("grantRights");
        if (newCategory != null) {
            Preference preference = preferenceDao.save(newCategory);
            if (preference == null) {
                request.setAttribute("error", "Такая категория уже существует!");
            }
            List<String> preferences = preferenceDao.getPreferences();
            request.setAttribute("preferences", preferences);
            List<User> users = userDao.getUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
        } else if (deleteCategoryName != null) {
            try {
                preferenceDao.deletePreference(deleteCategoryName);
                List<String> preferences = preferenceDao.getPreferences();
                request.setAttribute("preferences", preferences);
                List<User> users = userDao.getUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);

            } catch (DbException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else if (revokeRightsId != null) {

            User admin = userDao.findById(Long.parseLong(revokeRightsId));
            User admin1 = userDao.revokeGrantRights(admin);
            List<String> preferences = preferenceDao.getPreferences();
            request.setAttribute("preferences", preferences);
            List<User> users = userDao.getUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
        }else if (grantRightsId != null) {
            User admin = userDao.findById(Long.parseLong(grantRightsId));
            User admin1 = userDao.revokeGrantRights(admin);
            List<String> preferences = preferenceDao.getPreferences();
            request.setAttribute("preferences", preferences);
            List<User> users = userDao.getUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
        }


    }
}
