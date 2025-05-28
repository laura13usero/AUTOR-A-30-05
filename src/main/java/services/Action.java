package services;

import jakarta.servlet.http.HttpServletRequest; // CAMBIADO A JAKARTA
import jakarta.servlet.http.HttpServletResponse; // CAMBIADO A JAKARTA

public interface Action {
    public String execute(
            HttpServletRequest request,
            HttpServletResponse response);
}