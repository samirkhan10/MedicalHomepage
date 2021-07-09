package com.example.medicalhomepage;

public class ImageUploadModel {
    String imagename;
    String imageurl;
    String address;
    String phonenumber;

    public ImageUploadModel(String s, String toString) {
    }
    public ImageUploadModel(String imagename, String imageurl, String address, String phonenumber) {
        this.imagename = imagename;
        this.imageurl = imageurl;
        this.address = address;
        this.phonenumber = phonenumber;
    }

    public String getImagename() {
        return imagename;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getAddress() {
        return address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
}

