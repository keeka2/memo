package com.example.memo;

public class imageData {
    //이미지
    private byte[] member_Image;

    public byte[] getMember_Image() {
        return member_Image;
    }

    public void setMember_Image(byte[] member_Image) {
        this.member_Image = member_Image;
    }
    public imageData(byte[] member_Image){
        this.member_Image = member_Image;
    }

}
