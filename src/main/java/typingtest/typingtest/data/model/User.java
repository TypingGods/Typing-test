package typingtest.typingtest.data.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="person")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserText> userTexts;

    public User() {
    }

    public User(@NotBlank String name) {
        this.name = name;
        this.userTexts = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserText> getUserTexts() {
        return userTexts;
    }

    public void setUserTexts(Set<UserText> userTexts) {
        this.userTexts = userTexts;
    }
}
