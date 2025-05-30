package com.example.re_fresh;

public class Product {
    private String productName;
    private String productCategory;
    private String productDayLeft;
    private int productImage;
    private String productId;

    public Product(String productName, String productCategory, String productDayLeft, int productImage, String productId) {
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDayLeft = productDayLeft;
        this.productImage = productImage;
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductDayLeft() {
        return productDayLeft;
    }

    public int getProductImage() {
        return productImage;
    }

    public String getProductId() {
        return productId;
    }
}
