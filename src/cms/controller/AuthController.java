package cms.controller;

import cms.model.dao.UserDAO;

public class AuthController {
    private UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

    public AuthResult login(String clinicCode, String username, String password) {
        return userDAO.validateLogin(clinicCode, username, password);
    }
}
