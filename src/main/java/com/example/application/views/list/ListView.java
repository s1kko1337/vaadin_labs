package com.example.application.views.list;

import com.example.application.data.Contact;
import com.example.application.services.CrmService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Контакты | Vaadin CRM")
public class ListView extends VerticalLayout {
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();
    ContactForm form;
    CrmService service;

    public ListView(CrmService service) { 
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList(); 
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new ContactForm(service.findAllCompanies(), service.findAllStatuses()); 
        form.setWidth("25em");
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        
        grid.removeAllColumns();
        
        grid.addColumn(Contact::getFirstName).setHeader("Имя").setSortable(true);
        grid.addColumn(Contact::getLastName).setHeader("Фамилия").setSortable(true);
        grid.addColumn(Contact::getEmail).setHeader("Эл. почта").setSortable(true);
        grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Статус"); 
        grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Компания");
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true)); 
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Фильтр по имени...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList()); 

        Button addContactButton = new Button("Добавить контакт");

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() { 
        grid.setItems(service.findAllContacts(filterText.getValue()));
    }
}