package com.clinic;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public String name;
    @Column(unique = true, nullable = false) public String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) public String password;
    public String role;            // PATIENT or DOCTOR
    public String specialization;  // doctors only
    public String availability;    // doctors only, e.g. "Mon-Fri 09:00-17:00"
}
