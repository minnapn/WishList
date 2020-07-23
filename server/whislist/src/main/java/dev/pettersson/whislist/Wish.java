package dev.pettersson.whislist;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Wish {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String title;

    private Boolean completed = false;

    public void setTitle(String name) {
        this.title = name;
    }
}
