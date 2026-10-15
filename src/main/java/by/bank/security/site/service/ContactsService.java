package by.bank.security.site.service;

import by.bank.security.site.entity.ContactsTreeNode;

public interface ContactsService {

    ContactsTreeNode getContacts();

    ContactsTreeNode updateContacts(ContactsTreeNode data, String username);
}
