package cms.controller;

import cms.model.dao.UserDAO;
import cms.model.entities.User;

public class AuthController {
    private UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

    public User login(String clinicCode, String username, String password) {
        return userDAO.validateLogin(clinicCode, username, password);
    }
}
