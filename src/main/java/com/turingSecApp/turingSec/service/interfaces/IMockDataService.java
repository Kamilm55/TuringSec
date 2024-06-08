package com.turingSecApp.turingSec.service.interfaces;

public interface IMockDataService {
    void insertMockData();
    void insertRoles();
    void insertUsersAndHackers();
    void insertAdmins();
    void insertCompany();
    void insertProgram();
//    void insertReport();

    //void setAdminRoles(); // todo: it is not working , change role structure
}
