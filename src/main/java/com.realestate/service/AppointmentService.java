package com.realestate.service;

import com.realestate.model.Appointment;
import com.realestate.model.AppointmentStatus;
import com.realestate.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByUserId(Long userId) {
        return appointmentRepository.findByUserIdOrderByAppointmentDateDesc(userId);
    }

    public List<Appointment> getAppointmentsByAgentId(Long agentId) {
        return appointmentRepository.findByAgentIdOrderByAppointmentDateDesc(agentId);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment updatedAppointment) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        existing.setAppointmentDate(updatedAppointment.getAppointmentDate());
        existing.setAppointmentTime(updatedAppointment.getAppointmentTime());
        existing.setPurpose(updatedAppointment.getPurpose());
        existing.setPropertyType(updatedAppointment.getPropertyType());
        existing.setPropertyAddress(updatedAppointment.getPropertyAddress());
        existing.setBudgetRange(updatedAppointment.getBudgetRange());
        existing.setNotes(updatedAppointment.getNotes());
        existing.setStatus(updatedAppointment.getStatus());
        return appointmentRepository.save(existing);
    }

    public void updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public long countAll() {
        return appointmentRepository.count();
    }

    public long countByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    public long countByUserId(Long userId) {
        return appointmentRepository.countByUserId(userId);
    }
}
