package ies.project.backend.data.partner;

import ies.project.backend.data.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
public class Partner extends User {
}
