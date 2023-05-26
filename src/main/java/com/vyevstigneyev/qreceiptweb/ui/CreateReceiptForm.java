package com.vyevstigneyev.qreceiptweb.ui;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vyevstigneyev.qreceiptweb.dto.ReceiptCreateDto;
import com.vyevstigneyev.qreceiptweb.dto.ReceiptCreateFieldDto;
import com.vyevstigneyev.qreceiptweb.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Route("main/create-receipt")
public class CreateReceiptForm extends FormLayout {
    private final RestClientService restService;
    private final Binder<ReceiptCreateDto> binder = new BeanValidationBinder<>(ReceiptCreateDto.class);
    private List<ReceiptCreateFieldDto> products = new ArrayList<>();

    private TextField nameField = new TextField("Name");
    private TextField priceField = new TextField("Price");
    private TextField quantityField = new TextField("Quantity");
    private TextField totalPriceField = new TextField("Total Price");
    private Button save = new Button("Save");
    private Button close = new Button("Cancel");


    public CreateReceiptForm(@Autowired RestClientService restService) {
        this.restService = restService;
        if (!restService.isAuthenticated()) {
            UI.getCurrent().getPage().setLocation("/");
            return;
        }

        Button addProductButton = new Button("+");
        addProductButton.addClickListener(e -> {
            ReceiptCreateFieldDto product = new ReceiptCreateFieldDto();
            product.setName(nameField.getValue());
            product.setPrice(Double.parseDouble(priceField.getValue()));
            product.setQuantity(Integer.parseInt(quantityField.getValue()));
            product.setTotalPrice(Double.parseDouble(totalPriceField.getValue()));
            products.add(product);
            nameField.clear();
            priceField.clear();
            quantityField.clear();
            totalPriceField.clear();
        });

        add(
                nameField,
                priceField,
                quantityField,
                totalPriceField,
                addProductButton,
                createButtonsLayout()
        );
    }
    public void clearProducts() {
        this.products.clear();
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));


        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, close);
    }

    private void validateAndSave() {
        try {
            ReceiptCreateDto dto = new ReceiptCreateDto();
            dto.setProducts(products);
            binder.writeBean(dto);
            fireEvent(new SaveEvent(this, dto));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public static abstract class CreateReceiptFormEvent extends ComponentEvent<CreateReceiptForm> {
        private ReceiptCreateDto dto;

        protected CreateReceiptFormEvent(CreateReceiptForm source, ReceiptCreateDto dto) {
            super(source, false);
            this.dto = dto;
        }

        public ReceiptCreateDto getDto() {
            return dto;
        }
    }

    public static class SaveEvent extends CreateReceiptFormEvent {
        SaveEvent(CreateReceiptForm source, ReceiptCreateDto dto) {
            super(source, dto);
        }
    }


    public static class CloseEvent extends CreateReceiptFormEvent {
        CloseEvent(CreateReceiptForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
