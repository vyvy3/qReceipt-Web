package com.vyevstigneyev.qreceiptweb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vyevstigneyev.qreceiptweb.dto.LoginDto;
import com.vyevstigneyev.qreceiptweb.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/")
@CssImport("./styles/shared-styles.css")
public class LoginPage extends FormLayout {

    private final RestClientService restService;

    public LoginPage(@Autowired RestClientService restService) {
        this.restService = restService;
        if (restService.isAuthenticated()) {
            UI.getCurrent().navigate(MainPage.class);
        }
        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(event -> {
            boolean isAuthenticated = restService.authenticate(new LoginDto(event.getUsername(), event.getPassword()));
            if (isAuthenticated) {
                // Redirect to the main view
                UI.getCurrent().navigate(MainPage.class);
            } else {
                loginForm.setError(true);
            }
        });
        VerticalLayout pageLayout = new VerticalLayout();
        pageLayout.setClassName("login-page");
        pageLayout.add(loginForm);
        add(pageLayout);
    }

}
