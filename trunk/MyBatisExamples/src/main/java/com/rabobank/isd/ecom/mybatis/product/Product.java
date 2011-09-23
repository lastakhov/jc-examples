package com.rabobank.isd.ecom.mybatis.product;

/**
 * @author Jamie Craane
 */
public class Product {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Product");
        sb.append("{id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
