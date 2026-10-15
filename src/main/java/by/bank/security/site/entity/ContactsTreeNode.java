package by.bank.security.site.entity;

import java.util.List;

public record ContactsTreeNode(String id,
                               String label,
                               List<ContactPerson> people,
                               List<ContactsTreeNode> children) {

    public String getId() {
        return id;
    }

    public List<ContactsTreeNode> getChildren() {
        return children;
    }

    public static ContactsTreeNode basic() {
        return new ContactsTreeNode("contacts", "Контакты", null, List.of());
    }
}