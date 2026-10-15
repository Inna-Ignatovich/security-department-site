package by.bank.security.site.service.impl;

import by.bank.security.site.entity.ContactPerson;
import by.bank.security.site.entity.Contacts;
import by.bank.security.site.entity.ContactsTreeNode;
import by.bank.security.site.repository.ContactsRepository;
import by.bank.security.site.service.ContactsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactsServiceImpl implements ContactsService {

    private final ContactsRepository contactsRepository;

    private static final String CONTACTS_ID = "contacts";

    @Transactional(readOnly = true)
    @Override
    public ContactsTreeNode getContacts() {
        log.debug("Запрос контактов");
        return contactsRepository.findById(CONTACTS_ID)
                .map(contacts -> sortRecursive(contacts.getData()))
                .orElseGet(ContactsTreeNode::basic);
    }

    @Transactional
    @Override
    public ContactsTreeNode updateContacts(ContactsTreeNode data, String username) {
        log.debug("Запрос на обновление контактов от администратора [{}]", username);
        ContactsTreeNode sorted = sortRecursive(data);

        Contacts contacts = contactsRepository.findById(CONTACTS_ID)
                .orElseGet(Contacts::new);

        contacts.setData(sorted);
        contacts.setUpdatedBy(username);
        log.info("Контакты обновлены администратором [{}]", username);
        return contactsRepository.save(contacts).getData();
    }

    private ContactsTreeNode sortRecursive(ContactsTreeNode data) {
        List<ContactPerson> sortedPeople = data.people().stream()
                .sorted(Comparator
                        .comparing(ContactPerson::isLeader).reversed()
                        .thenComparing(p -> getPositionWeight(p.position()))
                        .thenComparing(ContactPerson::fullName))
                .toList();

        List<ContactsTreeNode> sortedChildren = data.children().stream()
                .map(this::sortRecursive)
                .toList();

        return new ContactsTreeNode(
                data.id(),
                data.label(),
                sortedPeople,
                sortedChildren
        );
    }

    private int getPositionWeight(String position) {
        String pos = position.toLowerCase().trim();
        if ((pos.contains("начальник") || pos.contains("директор"))
                && !pos.contains("заместитель")) {
            return 1;
        }
        if (pos.contains("заместитель")) {
            return 2;
        }
        return 10;
    }
}
