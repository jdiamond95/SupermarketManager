package com.example.pearlie.checkoutsuper;

/**
 * Created by nicol_000 on 03-Oct-17.
 */

public class Branch {
    private int id;
    private String branchName;
    private String address;
    private String telephone;
    private int company;
    private String companyName;
    private int manager;
    private String managerName;

    public Branch(int id, String branchName, String address, String telephone, int company, String companyName, int manager, String managerName) {
        this.id = id;
        this.branchName = branchName;
        this.address = address;
        this.telephone = telephone;
        this.company = company;
        this.companyName = companyName;
        this.manager = manager;
        this.managerName = managerName;
    }

    public int getId() {
        return id;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public int getCompany() {
        return company;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getManager() {
        return manager;
    }

    public String getManagerName() {
        return managerName;
    }
}
