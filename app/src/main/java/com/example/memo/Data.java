package com.example.memo;

public class Data {
    //id, 제목, 내용, 썸네일
    private String member_Id;
    private String member_Title;
    private String member_Body;
    private byte[] member_Thumbnail;

    public byte[] getMember_Thumbnail() {
        return member_Thumbnail;
    }

    public void setMember_Thumbnail(byte[] member_Thumbnail) {
        this.member_Thumbnail = member_Thumbnail;
    }


    public String getMember_Id() {
        return member_Id;
    }

    public void setMember_Id(String member_Id) {
        this.member_Id = member_Id;
    }

    public String getMember_Title() {
        return member_Title;
    }

    public void setMember_Title(String member_Title) {
        this.member_Title = member_Title;
    }

    public String getMember_Body() {
        return member_Body;
    }

    public void setMember_Body(String member_Body) {
        this.member_Body = member_Body;
    }

    public Data(String member_Id, String member_Title, String member_Body, byte[] member_Thumbnail){
        this.member_Id = member_Id;
        this.member_Title = member_Title;
        this.member_Body = member_Body;
        this.member_Thumbnail = member_Thumbnail;
    }
}
