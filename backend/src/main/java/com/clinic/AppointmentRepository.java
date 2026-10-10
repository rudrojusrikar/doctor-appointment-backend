package com.clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByDateDesc(Long id);
    List<Appointment> findByDoctorIdOrderByDateDesc(Long id);
    boolean existsByDoctorIdAndDateAndTimeAndStatusNot(Long d, LocalDate date, String time, String status);
}
