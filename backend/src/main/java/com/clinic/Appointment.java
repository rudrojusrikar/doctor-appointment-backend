package com.clinic;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public Long patientId, doctorId;
    public String patientName, doctorName;
    public LocalDate date;
    public String time;            // e.g. "10:30"
    public String status = "BOOKED"; // BOOKED, COMPLETED, CANCELLED
    public String reason;
}
