package com.example.capstone.methods;

import android.util.Log;
import android.widget.ImageView;

import com.example.capstone.R;

import java.util.Objects;

public class DisplayPlatforms {

    public static boolean displayIcon(ImageView iv, String platform) {
        if (Objects.equals(platform, "Netflix")) {
            iv.setImageResource(R.drawable.netflix);
            return true;
        } else if (Objects.equals(platform, "Hulu")) {
            iv.setImageResource(R.drawable.hulu);
            return true;
        } else if (Objects.equals(platform, "Disney Plus")) {
            iv.setImageResource(R.drawable.disney_plus_100);
            return true;
        } else if (platform.contains("Amazon")) {
            iv.setImageResource(R.drawable.amazon_prime_24);
            return true;
        } else if (platform.equals("TNT")) {
            iv.setImageResource(R.drawable.tnt);
            return true;
        } else if (platform.equals("Peacock Premium")) {
            iv.setImageResource(R.drawable.peacock_premium);
            return true;
        } else if (platform.equals("DIRECTV")) {
            iv.setImageResource(R.drawable.directv);
            return true;
        }  else if (platform.equals("Paramount Plus")) {
            iv.setImageResource(R.drawable.paramount_plus);
            return true;
        } else if (platform.equals("Crunchyroll")) {
            iv.setImageResource(R.drawable.crunchyroll);
            return true;
        } else if (platform.equals("fuboTV")) {
            iv.setImageResource(R.drawable.fubotv);
            return true;
        } else if (platform == "HBO Now") {
            iv.setImageResource(R.drawable.hbo_now);
            return true;
        } else if (platform.contains("HBO")) {
            iv.setImageResource(R.drawable.hbo_50);
            return true;
        } else if (platform.equals("Spectrum On Demand")) {
            iv.setImageResource(R.drawable.spectrum_on_demand);
            return true;
        } else if(platform.equals("Hoopla")) {
            iv.setImageResource(R.drawable.hoopla);
            return true;
        } else {
            Log.i("DisplayingPlatforms", "idk: " + platform);
            return false;
        }

    }
}
