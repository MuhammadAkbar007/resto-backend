package uz.akbar.resto.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uz.akbar.resto.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

}
