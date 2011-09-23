package com.rabobank.isd.ecom.mybatis.product;

/**
 * @author Jamie Craane
 */
public class Dvd extends Product {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("Dvd");
        sb.append("{title='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
