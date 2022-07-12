package com.example.capstone.methods;

import android.util.Log;
import android.widget.ImageView;

import com.example.capstone.R;

import java.util.Objects;

public class DisplayPlatforms {

    public static void displayIcon(ImageView iv, String platform) {
        if (Objects.equals(platform, "Netflix")) {
            iv.setImageResource(R.drawable.netflix);
        } else if (Objects.equals(platform, "Hulu")) {
            iv.setImageResource(R.drawable.hulu);
        } else if (Objects.equals(platform, "Disney Plus")) {
            iv.setImageResource(R.drawable.disney_plus_100);
        } else if (platform.contains("Amazon")) {
            iv.setImageResource(R.drawable.amazon_prime_24);
        } else if (platform.equals("TNT")) {
            iv.setImageResource(R.drawable.tnt);
        } else if (platform.equals("Peacock Premium")) {
            iv.setImageResource(R.drawable.peacock_premium);
        } else if (platform.equals("DIRECTV")) {
            iv.setImageResource(R.drawable.directv);
        }  else if (platform.equals("Paramount Plus")) {
            iv.setImageResource(R.drawable.paramount_plus);
        } else if (platform.equals("Crunchyroll")) {
            iv.setImageResource(R.drawable.crunchyroll);
        } else if (platform.equals("fuboTV")) {
            iv.setImageResource(R.drawable.fubotv);
        } else if (platform == "HBO Now") {
            iv.setImageResource(R.drawable.hbo_now);
        } else if (platform.contains("HBO")) {
            iv.setImageResource(R.drawable.hbo_50);
        } else if (platform.equals("Spectrum On Demand")) {
            iv.setImageResource(R.drawable.spectrum_on_demand);
        } else {
            Log.i("DIsplayingPlatforms", "idk: " + platform);
        }



    }
}
