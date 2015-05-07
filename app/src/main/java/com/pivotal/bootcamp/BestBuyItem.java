package com.pivotal.bootcamp;

/**
 * Created by administrator on 2015-05-07.
 */
public class BestBuyItem {
    String name;
    Double price;
    String shortDesc;
    String longDesc;
    String sku;
    String thumbUrl;
    String imageUrl;

    public BestBuyItem(String name, Double price, String shortDesc, String longDesc, String sku, String thumbUrl, String imageUrl) {
        this.name = name;
        this.price = price;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.sku = sku;
        this.thumbUrl = thumbUrl;
        this.imageUrl = imageUrl;
    }


    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getSku() {
        return sku;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }
}
