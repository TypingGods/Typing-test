package typingtest.typingtest.data.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_text")
public class UserText implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "person_id")
    private User person;

    @Id
    @ManyToOne
    @JoinColumn(name = "text_id")
    private Text text;

    @Column(name = "score")
    private Double score;

    public UserText() {
    }

    public UserText(User person, Text text, Double score) {
        this.person = person;
        this.text = text;
        this.score = score;
    }

    public User getPerson() {
        return person;
    }

    public void setPerson(User person) {
        this.person = person;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
