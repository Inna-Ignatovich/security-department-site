package by.bank.security.site.entity.converter;

import by.bank.security.site.entity.ContactsTreeNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Converter
public class ContactsTreeNodeConverter implements AttributeConverter<ContactsTreeNode, String> {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public String convertToDatabaseColumn(ContactsTreeNode attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JacksonException e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }

    @Override
    public ContactsTreeNode convertToEntityAttribute(String data) {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            return mapper.readValue(data, ContactsTreeNode.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Ошибка десериализации JSON", e);
        }
    }
}