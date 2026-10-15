package by.bank.security.site.controller;

import by.bank.security.site.entity.ContactsTreeNode;
import by.bank.security.site.service.ContactsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
@Validated
@NullMarked
public class ContactsController {

    private final ContactsService contactsService;

    @GetMapping()
    public ResponseEntity<ContactsTreeNode> get() {
        return ResponseEntity.ok(contactsService.getContacts());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    public ResponseEntity<ContactsTreeNode> update(@Valid @RequestBody ContactsTreeNode dto) {

        String username = "i.ignatovich";
        return ResponseEntity.ok(contactsService.updateContacts(dto, username));
    }
}