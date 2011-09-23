package com.rabobank.isd.ecom.mybatis.person;

/**
 * @author Jamie Craane
 */
public class Account {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer aId) {
        id = aId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String aName) {
        name = aName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Account");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
