package com.example.re_fresh;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Product {
    private String productName;
    private String productCategory;
    private String productDayLeft;  // Kalan gün bilgisi (örn: "10 GÜN", "Süresi doldu")
    private int productImage;
    private String productId;

    // expiryDate: Firestore'dan gelen tarih string (yyyy-MM-dd)
    public Product(String productName, String productCategory, String expiryDate, int productImage, String productId) {
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDayLeft = calculateDaysLeft(expiryDate);
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

    public String calculateDaysLeft(String expiryDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // gg/aa/yyyy formatı
        try {
            Date expiry = sdf.parse(expiryDate);
            Date today = new Date();

            long diffInMillis = expiry.getTime() - today.getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (daysLeft < 0) {
                return "Süresi doldu";
            } else {
                return daysLeft + " GÜN KALDI";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Geçersiz tarih";
        }
    }
}
