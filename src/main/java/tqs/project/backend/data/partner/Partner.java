package tqs.project.backend.data.partner;

import lombok.AllArgsConstructor;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("collectionPoint")
public class Partner extends User {

    @OneToOne(mappedBy = "partner")
    private CollectionPoint collectionPoint;

    @Override
    public String toString() {
        return "Partner [collectionPoint=" + collectionPoint + ", username="+ this.getUsername() + ", password=" + this.getPassword() + "]" ;
    }

}
