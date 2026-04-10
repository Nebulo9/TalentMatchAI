package com.talentmatch.service;

import com.talentmatch.exception.NotFoundException;
import com.talentmatch.kafka.MatchingProducer;
import com.talentmatch.model.Candidate;
import com.talentmatch.model.JobOffer;
import com.talentmatch.model.MatchingResult;
import com.talentmatch.model.MatchingStatus;
import com.talentmatch.repository.MatchingResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchingResultRepository repository;

    @Mock
    private CandidateService candidateService;

    @Mock
    private JobOfferService jobOfferService;

    @Mock
    private MatchingProducer producer;

    @InjectMocks
    private MatchingService service;

    @Test
    void analyze_success() {
        UUID candidateId = UUID.randomUUID();
        UUID jobOfferId = UUID.randomUUID();

        when(candidateService.findById(candidateId)).thenReturn(new Candidate());
        when(jobOfferService.findById(jobOfferId)).thenReturn(new JobOffer());

        MatchingResult saved = new MatchingResult();
        saved.setCandidateId(candidateId);
        saved.setJobOfferId(jobOfferId);
        saved.setStatus(MatchingStatus.PENDING);
        when(repository.save(any())).thenReturn(saved);

        MatchingResult result = service.analyze(candidateId, jobOfferId);

        assertThat(result.getStatus()).isEqualTo(MatchingStatus.PENDING);
        verify(producer).publish(any(), eq(candidateId), eq(jobOfferId));
    }

    @Test
    void analyze_candidateNotFound() {
        UUID candidateId = UUID.randomUUID();
        UUID jobOfferId = UUID.randomUUID();

        when(candidateService.findById(candidateId)).thenThrow(new NotFoundException("Candidate not found"));

        assertThatThrownBy(() -> service.analyze(candidateId, jobOfferId))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(producer, repository);
    }

    @Test
    void updateResult_setsCompleted() {
        UUID id = UUID.randomUUID();
        MatchingResult result = new MatchingResult();
        result.setStatus(MatchingStatus.PROCESSING);

        when(repository.findById(id)).thenReturn(Optional.of(result));
        when(repository.save(result)).thenReturn(result);

        service.updateResult(id, 85, "Great match", MatchingStatus.COMPLETED, null);

        assertThat(result.getScore()).isEqualTo(85);
        assertThat(result.getStatus()).isEqualTo(MatchingStatus.COMPLETED);
        assertThat(result.getCompletedAt()).isNotNull();
        verify(repository).save(result);
    }

    @Test
    void updateResult_setsFailed() {
        UUID id = UUID.randomUUID();
        MatchingResult result = new MatchingResult();
        result.setStatus(MatchingStatus.PROCESSING);

        when(repository.findById(id)).thenReturn(Optional.of(result));
        when(repository.save(result)).thenReturn(result);

        service.updateResult(id, null, null, MatchingStatus.FAILED, "AI error");

        assertThat(result.getStatus()).isEqualTo(MatchingStatus.FAILED);
        assertThat(result.getErrorMessage()).isEqualTo("AI error");
        assertThat(result.getCompletedAt()).isNotNull();
    }

    @Test
    void getStats() {
        when(repository.count()).thenReturn(5L);
        when(repository.countByStatus(MatchingStatus.PENDING)).thenReturn(1L);
        when(repository.countByStatus(MatchingStatus.PROCESSING)).thenReturn(1L);
        when(repository.countByStatus(MatchingStatus.COMPLETED)).thenReturn(2L);
        when(repository.countByStatus(MatchingStatus.FAILED)).thenReturn(1L);

        Map<String, Long> stats = service.getStats();

        assertThat(stats.get("total")).isEqualTo(5L);
        assertThat(stats.get("completed")).isEqualTo(2L);
    }
}
