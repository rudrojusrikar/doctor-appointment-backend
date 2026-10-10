package com.clinic;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api")
public class ApiController {
    private final UserRepository users; private final AppointmentRepository appts; private final BCryptPasswordEncoder enc;
    public ApiController(UserRepository u, AppointmentRepository a, BCryptPasswordEncoder e) { users = u; appts = a; enc = e; }

    private ResponseEntity<?> err(HttpStatus s, String m) { return ResponseEntity.status(s).body(Map.of("error", m)); }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody User u) {
        if (u.email == null || u.password == null || u.name == null) return err(HttpStatus.BAD_REQUEST, "Name, email and password are required");
        if (users.findByEmail(u.email).isPresent()) return err(HttpStatus.CONFLICT, "Email already registered");
        u.role = "DOCTOR".equalsIgnoreCase(u.role) ? "DOCTOR" : "PATIENT";
        u.password = enc.encode(u.password);
        return ResponseEntity.ok(users.save(u));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> b) {
        Optional<User> u = users.findByEmail(b.get("email"));
        if (u.isEmpty() || !enc.matches(b.getOrDefault("password", ""), u.get().password))
            return err(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return ResponseEntity.ok(u.get());
    }

    @GetMapping("/doctors")
    public List<User> doctors(@RequestParam(required = false) String specialization) {
        List<User> all = users.findByRole("DOCTOR");
        if (specialization == null || specialization.isBlank()) return all;
        return all.stream().filter(d -> d.specialization != null && d.specialization.toLowerCase().contains(specialization.toLowerCase())).toList();
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> book(@RequestBody Appointment a) {
        if (a.date == null || a.time == null || a.doctorId == null || a.patientId == null) return err(HttpStatus.BAD_REQUEST, "Missing fields");
        if (a.date.isBefore(java.time.LocalDate.now())) return err(HttpStatus.BAD_REQUEST, "Date cannot be in the past");
        if (appts.existsByDoctorIdAndDateAndTimeAndStatusNot(a.doctorId, a.date, a.time, "CANCELLED"))
            return err(HttpStatus.CONFLICT, "That slot is already booked");
        a.id = null; a.status = "BOOKED";
        a.doctorName = users.findById(a.doctorId).map(d -> d.name).orElse("Unknown");
        a.patientName = users.findById(a.patientId).map(p -> p.name).orElse("Unknown");
        return ResponseEntity.ok(appts.save(a));
    }

    @GetMapping("/appointments/patient/{id}")
    public List<Appointment> byPatient(@PathVariable Long id) { return appts.findByPatientIdOrderByDateDesc(id); }

    @GetMapping("/appointments/doctor/{id}")
    public List<Appointment> byDoctor(@PathVariable Long id) { return appts.findByDoctorIdOrderByDateDesc(id); }

    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> b) {
        return appts.findById(id).map(a -> { a.status = b.get("status"); return ResponseEntity.ok((Object) appts.save(a)); })
                .orElse(ResponseEntity.notFound().build());
    }
}
