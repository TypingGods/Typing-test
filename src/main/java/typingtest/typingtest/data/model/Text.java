package typingtest.typingtest.data.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "text")
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;

    @OneToMany(mappedBy = "text")
    private Set<UserText> userTexts;

    public Text() {
    }

    public Text(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<UserText> getUserTexts() {
        return userTexts;
    }

    public void setUserTexts(Set<UserText> userTexts) {
        this.userTexts = userTexts;
    }
}
