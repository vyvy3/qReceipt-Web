package com.vyevstigneyev.qreceiptweb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vyevstigneyev.qreceiptweb.dto.ReceiptRegistryDto;
import com.vyevstigneyev.qreceiptweb.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.UUID;
    
@Route("/main")
public class MainPage extends VerticalLayout {
    Grid<ReceiptRegistryDto> grid = new Grid<>(ReceiptRegistryDto.class);
    CreateReceiptForm form;

    private final RestClientService restService;


    public MainPage(@Autowired RestClientService restService) {
        this.restService = restService;
        if (!restService.isAuthenticated()) {
            UI.getCurrent().getPage().setLocation("/");
            return;
        }
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getReceipts());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getReceipts() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("receipts");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new CreateReceiptForm(this.restService);
        form.setWidth("25em");
        form.addListener(CreateReceiptForm.SaveEvent.class, this::saveReceipt);
        form.addListener(CreateReceiptForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("receipt-grid");
        grid.removeAllColumns();
        grid.setSizeFull();
        grid.addColumn(ReceiptRegistryDto::getId)
                .setHeader("ID")
                .setAutoWidth(true);
        grid.addColumn(ReceiptRegistryDto::getCreatedDate)
                .setHeader("Created Date")
                .setAutoWidth(true);
        grid.addColumn(ReceiptRegistryDto::getTotal)
                .setHeader("Total")
                .setAutoWidth(true);
        grid.addColumn(dto -> dto.getOrganization().getName())
                .setHeader("Organization")
                .setAutoWidth(true);

        grid.addColumn(new NativeButtonRenderer<ReceiptRegistryDto>("QR",
                        r -> {
                            showQrCodeModalWindow(restService.getQr(r.getId()));
                        }
                ))
                .setHeader("QR Code")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void showQrCodeModalWindow(byte[] picture) {
        // Create a dialog component
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        // Create an image component and set its source to the picture data
        Image image = new Image(new StreamResource("qr-code.png", () -> new ByteArrayInputStream(picture)), "QR Code");
        image.setWidth("200px");
        image.setHeight("200px");

        // Create a horizontal layout to center the image
        HorizontalLayout imageLayout = new HorizontalLayout(image);
        imageLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Create a close button and add it to a vertical layout with the image layout
        Button closeButton = new Button("Close", event -> dialog.close());
        VerticalLayout contentLayout = new VerticalLayout(imageLayout, closeButton);
        contentLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Set the content of the dialog to the vertical layout
        dialog.add(contentLayout);

        // Open the dialog
        dialog.open();
    }

    private HorizontalLayout getToolbar() {
        Button addContactButton = new Button("Create receipt");
        addContactButton.addClickListener(click -> createReceipt());

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveReceipt(CreateReceiptForm.SaveEvent event) {
        restService.saveReceipt(event.getDto());
        updateList();
        closeEditor();
    }


    private void createReceipt() {
        grid.asSingleSelect().clear();
        form.setVisible(true);
        addClassName("creating");
    }

    private void closeEditor() {
        form.clearProducts();
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(restService.getAllReceipts());
    }
}
