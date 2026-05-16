package service;

public class Generator {
    public static Long file_id = 0L;
    public static Long attachment_id = 0L;
    public static Long user_id = 0L;

    public static Long getFileNextId(){
        return file_id++;
    }
    public static Long getAttNextId(){
        return attachment_id++;
    }
    public static Long nextUserId(){return user_id++;}

}
