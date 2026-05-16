package service;

import domain.User;

public class SessionService {
    private static User currentUser;
    public SessionService(){}
    public void login(User user){
        this.currentUser = user;
    }

    public void logout(){
        this.currentUser = null;
    }
    public static boolean isAuthorized(){
        return currentUser != null;
    }
    public static User getCurrentUser(){
        if (!isAuthorized()){
            throw new IllegalArgumentException("Ошибка: Пользователь не авторизован");
        }
        return currentUser;
    }
    public static String getCurrentUsername(){
        return getCurrentUser().getLogin();
    }
    public static Long getCurrentUserId(){
        return getCurrentUser().getUserId();
    }

 }
