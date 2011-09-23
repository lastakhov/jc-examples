package com.rabobank.isd.ecom.mybatis.person;

/**
 * @author Jamie Craane
 */
public class Address {
    private Integer id;
    private String street;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Address");
        sb.append("{id=").append(id);
        sb.append(", street='").append(street).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
