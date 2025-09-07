package com.example.backend.model.lawyer;

import com.example.backend.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name="lawyers")
public class Lawyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "court_colors", columnDefinition = "jsonb")
    private Map<String, String> courtColors;

    public Lawyer(){}

    public Lawyer(User user, Map<String,String> courtColors){
        this.user = user;
        this.courtColors = courtColors;
    }

    public Long getId() {return id;}
    public void setId(Long id){this.id= id;}

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Map<String, String> getCourtColors() { return courtColors; }
    public void setCourtColors(Map<String, String> courtColors) { this.courtColors = courtColors; }

}
