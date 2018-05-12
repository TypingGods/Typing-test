package typingtest.typingtest.data.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "text")
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String text;

    @NotBlank
    private String author;

    @OneToMany(mappedBy = "text")
    private Set<UserText> userTexts;

    public Text() {
    }

    public Text(@NotBlank String text, @NotBlank String author) {
        this.text = text;
        this.author = author;
    }

    public Text(@NotBlank String text, @NotBlank String author, Set<UserText> userTexts) {
        this.text = text;
        this.author = author;
        this.userTexts = userTexts;
    }

    public int getSize() {
        return text.length();
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Set<UserText> getUserTexts() {
        return userTexts;
    }

    public void setUserTexts(Set<UserText> userTexts) {
        this.userTexts = userTexts;
    }
}
