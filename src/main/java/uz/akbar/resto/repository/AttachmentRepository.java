package uz.akbar.resto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.Attachment;

import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {}
