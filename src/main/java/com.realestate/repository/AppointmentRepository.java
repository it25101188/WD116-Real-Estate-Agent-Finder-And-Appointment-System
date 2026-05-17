package com.realestate.repository;

import com.realestate.model.Appointment;
import com.realestate.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByAgentId(Long agentId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByUserIdOrderByAppointmentDateDesc(Long userId);
    List<Appointment> findByAgentIdOrderByAppointmentDateDesc(Long agentId);

    @Query("SELECT a FROM Appointment a WHERE a.agent.id = :agentId AND a.appointmentDate = :date")
    List<Appointment> findByAgentIdAndDate(@Param("agentId") Long agentId, @Param("date") LocalDate date);

    long countByStatus(AppointmentStatus status);
    long count();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
