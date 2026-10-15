package by.bank.security.site.entity;

import by.bank.security.site.entity.converter.ContactsTreeNodeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.java.ObjectJavaType;

import java.time.Instant;

@Entity
@Table(name = "contacts_tree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contacts {

    @Id
    @Builder.Default
    private String id = "contacts";

    @Convert(converter = ContactsTreeNodeConverter.class)
    @JavaType(ObjectJavaType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private ContactsTreeNode data;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contacts contacts)) return false;
        return id != null && id.equals(contacts.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}