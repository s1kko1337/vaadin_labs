package com.example.application.views.list;

import com.example.application.data.Company;
import com.example.application.data.Contact;
import com.example.application.data.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class ContactForm extends FormLayout {
  private Contact contact;

  TextField firstName = new TextField("Имя");
  TextField lastName = new TextField("Фамилия");
  EmailField email = new EmailField("Эл. адрес");
  ComboBox<Status> status = new ComboBox<>("Статус");
  ComboBox<Company> company = new ComboBox<>("Компания");

  Button save = new Button("Сохранить");
  Button delete = new Button("Удалить");
  Button close = new Button("Выйти");

  Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

  public ContactForm(List<Company> companies, List<Status> statuses) {

    addClassName("contact-form");
    binder.bindInstanceFields(this);
    company.setItems(companies);
    company.setItemLabelGenerator(Company::getName);
    status.setItems(statuses);
    status.setItemLabelGenerator(Status::getName);

    add(firstName,
        lastName,
        email,
        company,
        status,
        createButtonsLayout());
  }

  public void setContact(Contact contact) {
    this.contact = contact;
    binder.readBean(contact);
  }

  private Component createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
  
    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);
  
    save.addClickListener(event -> validateAndSave()); 
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact))); 
    close.addClickListener(event -> fireEvent(new CloseEvent(this))); 
  
    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid())); 
    return new HorizontalLayout(save, delete, close);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(contact); 
      fireEvent(new SaveEvent(this, contact)); 
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  // Events
  public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
    private Contact contact;

    protected ContactFormEvent(ContactForm source, Contact contact) {
      super(source, false);
      this.contact = contact;
    }

    public Contact getContact() {
      return contact;
    }
  }

  public static class SaveEvent extends ContactFormEvent {
    SaveEvent(ContactForm source, Contact contact) {
      super(source, contact);
    }
  }

  public static class DeleteEvent extends ContactFormEvent {
    DeleteEvent(ContactForm source, Contact contact) {
      super(source, contact);
    }

  }

  public static class CloseEvent extends ContactFormEvent {
    CloseEvent(ContactForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
      ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}
