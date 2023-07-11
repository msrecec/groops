package hr.tvz.groops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "user", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = "id"),
        @UniqueConstraint(name = "username", columnNames = "username")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class User extends BaseEntity {
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    @Email(message = "Value is not a valid email")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "description")
    private String description;
    @Column(name = "profile_picture_key")
    private String profilePictureKey;
    @Column(name = "confirmed")
    private Boolean confirmed;
}
