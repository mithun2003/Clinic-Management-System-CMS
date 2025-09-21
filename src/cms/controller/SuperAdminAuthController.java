package cms.controller;

import cms.model.dao.SuperAdminDAO;
import cms.model.entities.SuperAdmin;

public class SuperAdminAuthController {
    private SuperAdminDAO dao;

    public SuperAdminAuthController() {
        dao = new SuperAdminDAO();
    }

    public SuperAdmin login(String username, String password) {
        return dao.validateLogin(username, password);
    }
}