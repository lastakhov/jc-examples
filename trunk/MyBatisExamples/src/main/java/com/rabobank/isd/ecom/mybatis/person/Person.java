package com.rabobank.isd.ecom.mybatis.person;

import java.util.List;

/**
 * @author Jamie Craane
 */
public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private Account account;
    private int age;

    private List<Address> addresses;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer aId) {
        id = aId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String aFirstName) {
        firstName = aFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String aLastName) {
        lastName = aLastName;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account aAccount) {
        account = aAccount;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Person");
        sb.append("{id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", account=").append(account);
        sb.append('}');
        return sb.toString();
    }
}
