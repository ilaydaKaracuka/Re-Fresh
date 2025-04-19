package com.example.re_fresh;

public class Product {
    private String productName;
    private String productCategory;
    private String productDayLeft;
    private int productImage;

    public Product(String productName, String productCategory, String productDayLeft, int productImage) {
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDayLeft = productDayLeft;
        this.productImage = productImage;
    }

    // Getter metodları
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
}
