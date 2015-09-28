package link.omny.custmgmt.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
public class CustomAccountField extends CustomField {

    private static final long serialVersionUID = -4837634414877249693L;

    public CustomAccountField(String key, String value) {
        super(key, value);
    }

    // @ManyToOne(optional = false, targetEntity = Contact.class)
    // // (fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
    // // CascadeType.MERGE })
    // private Account account;

}