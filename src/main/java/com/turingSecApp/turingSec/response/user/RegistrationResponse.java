package com.turingSecApp.turingSec.response.user;

import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;


public class RegistrationResponse {
    private CompanyEntity company;

    public RegistrationResponse(CompanyEntity company) {
        this.company = company;

    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }


}