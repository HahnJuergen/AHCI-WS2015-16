package com.ahci.meme_recommender.model;

import java.util.List;

/**
 * Created by jonbr on 12.01.2016.
 */
public class Rating {


    public static String toUrlParam(List<Rating> ratings, boolean addAmpersandBefore) {
        StringBuilder urlParam = new StringBuilder();



        return urlParam.length() == 0? "" : (addAmpersandBefore? "&" + urlParam.toString() : urlParam.toString());
    }

}
