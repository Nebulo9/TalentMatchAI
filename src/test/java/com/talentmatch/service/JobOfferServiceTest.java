package com.talentmatch.service;

import com.talentmatch.exception.NotFoundException;
import com.talentmatch.model.JobOffer;
import com.talentmatch.repository.JobOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobOfferServiceTest {

    @Mock
    private JobOfferRepository repository;

    @InjectMocks
    private JobOfferService service;

    @Test
    void create_success() {
        JobOffer offer = jobOffer("Senior Java Dev");
        when(repository.save(offer)).thenReturn(offer);

        JobOffer result = service.create(offer);

        assertThat(result).isEqualTo(offer);
        verify(repository).save(offer);
    }

    @Test
    void findAll() {
        JobOffer offer = jobOffer("Senior Java Dev");
        when(repository.findAll()).thenReturn(List.of(offer));

        List<JobOffer> result = service.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        JobOffer existing = jobOffer("Old Title");
        JobOffer updated = jobOffer("New Title");
        updated.setCompany("NewCorp");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        JobOffer result = service.update(id, updated);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getCompany()).isEqualTo("NewCorp");
    }

    private JobOffer jobOffer(String title) {
        JobOffer offer = new JobOffer();
        offer.setTitle(title);
        offer.setCompany("Acme Corp");
        offer.setRequiredSkills(List.of("Java"));
        offer.setDescription("A great job");
        offer.setLocation("Paris");
        return offer;
    }
}
