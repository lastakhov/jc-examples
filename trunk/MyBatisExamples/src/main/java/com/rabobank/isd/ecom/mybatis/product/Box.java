package com.rabobank.isd.ecom.mybatis.product;

/**
 * @author Jamie Craane
 */
public class Box extends Product {
    private int weight;

    public int getWeight() {
        return weight;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("Box");
        sb.append("{weight=").append(weight);
        sb.append('}');
        return sb.toString();
    }
}
