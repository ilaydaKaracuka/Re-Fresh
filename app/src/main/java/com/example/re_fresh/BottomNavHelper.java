package com.example.re_fresh;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {
    public static void setup(final Activity activity, BottomNavigationView bottomNavigationView, int activeItemId) {
        bottomNavigationView.setItemIconTintList(null);

        // Tüm menü öğelerini pasif (siyah) yap
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem item = bottomNavigationView.getMenu().getItem(i);
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                item.setIcon(R.mipmap.menu_main); // siyah ikon
            } else if (itemId == R.id.nav_add) {
                item.setIcon(R.mipmap.menu_add);  // siyah ikon
            } else if (itemId == R.id.nav_profile) {
                item.setIcon(R.mipmap.menu_profile); // siyah ikon
            }
        }

        // Aktif öğeyi bitmap ile büyüt
        MenuItem activeItem = bottomNavigationView.getMenu().findItem(activeItemId);
        if (activeItem != null) {
            int iconResId = -1;

            if (activeItemId == R.id.nav_home) {
                iconResId = R.mipmap.menu_main_blue;
            } else if (activeItemId == R.id.nav_add) {
                iconResId = R.mipmap.menu_add_blue;
            } else if (activeItemId == R.id.nav_profile) {
                iconResId = R.mipmap.menu_profile_blue;
            }

            if (iconResId != -1) {
                Bitmap original = BitmapFactory.decodeResource(activity.getResources(), iconResId);

                int newSizeInDp = 32; // büyüklük burada ayarlanır
                float scale = activity.getResources().getDisplayMetrics().density;
                int newSizeInPx = (int) (newSizeInDp * scale + 0.5f);

                Bitmap scaled = Bitmap.createScaledBitmap(original, newSizeInPx, newSizeInPx, true);
                Drawable drawable = new BitmapDrawable(activity.getResources(), scaled);
                activeItem.setIcon(drawable);
            }

            activeItem.setChecked(true);
        }

        // Menü tıklamaları
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == activeItemId) return true;

            if (id == R.id.nav_home) {
                activity.startActivity(new Intent(activity, MainActivity.class));
            } else if (id == R.id.nav_add) {
                activity.startActivity(new Intent(activity, AddProductActivity.class));
            } else if (id == R.id.nav_profile) {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
            }

            activity.overridePendingTransition(0, 0);
            activity.finish(); // geri dönmemesi için
            return true;
        });
    }
}