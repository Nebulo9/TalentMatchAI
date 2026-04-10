package com.talentmatch.service;

import com.talentmatch.exception.NotFoundException;
import com.talentmatch.exception.ValidationException;
import com.talentmatch.integration.github.GithubService;
import com.talentmatch.model.Candidate;
import com.talentmatch.repository.CandidateRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository repository;

    @Mock
    private GithubService githubService;

    @InjectMocks
    private CandidateService service;

    @Test
    void create_success() {
        Candidate candidate = candidate("alice@example.com");
        when(repository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(repository.save(candidate)).thenReturn(candidate);

        Candidate result = service.create(candidate);

        assertThat(result).isEqualTo(candidate);
        verify(repository).save(candidate);
    }

    @Test
    void create_duplicateEmail_throws() {
        Candidate candidate = candidate("alice@example.com");
        when(repository.findByEmail("alice@example.com")).thenReturn(Optional.of(candidate));

        assertThatThrownBy(() -> service.create(candidate))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("alice@example.com");
    }

    @Test
    void findById_found() {
        UUID id = UUID.randomUUID();
        Candidate candidate = candidate("alice@example.com");
        when(repository.findById(id)).thenReturn(Optional.of(candidate));

        Candidate result = service.findById(id);

        assertThat(result).isEqualTo(candidate);
    }

    @Test
    void findById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        Candidate existing = candidate("old@example.com");
        Candidate updated = candidate("new@example.com");
        updated.setFirstName("Bob");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Candidate result = service.update(id, updated);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void delete_callsRepo() {
        UUID id = UUID.randomUUID();
        doNothing().when(repository).deleteById(id);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    private Candidate candidate(String email) {
        Candidate c = new Candidate();
        c.setFirstName("Alice");
        c.setLastName("Smith");
        c.setEmail(email);
        c.setSkills(List.of("Java"));
        c.setYearsOfExperience(3);
        return c;
    }
}
