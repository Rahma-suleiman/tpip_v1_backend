package com.znz.tpip_backend.validation;

import com.znz.tpip_backend.model.ProgrammeChoice;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ProgrammeChoiceValidator {

    private static final int MAX_CHOICES = 3;

    public void validateCreate(
            List<ProgrammeChoice> existing,
            Integer rank,
            Long programmeId
    ) {

        validateCommon(existing, rank, programmeId, null);

        if (existing.size() >= MAX_CHOICES) {
            throw new IllegalStateException("Maximum 3 programme choices allowed");
        }
    }

    public void validateUpdate(
            List<ProgrammeChoice> existing,
            Integer rank,
            Long programmeId,
            Long currentId
    ) {

        validateCommon(existing, rank, programmeId, currentId);
    }

    // ================= COMMON RULES =================
    private void validateCommon(
            List<ProgrammeChoice> existing,
            Integer rank,
            Long programmeId,
            Long currentId
    ) {

        if (rank == null || rank < 1 || rank > 3) {
            throw new IllegalStateException("Preference rank must be between 1 and 3");
        }

        boolean rankTaken = existing.stream()
                .anyMatch(pc ->
                        !isCurrent(pc, currentId) &&
                        pc.getPreferenceRank().equals(rank)
                );

        if (rankTaken) {
            throw new IllegalStateException("Preference rank already taken");
        }

        boolean programmeTaken = existing.stream()
                .anyMatch(pc ->
                        !isCurrent(pc, currentId) &&
                        pc.getProgramme().getId().equals(programmeId)
                );

        if (programmeTaken) {
            throw new IllegalStateException("Programme already selected");
        }
    }

    private boolean isCurrent(ProgrammeChoice pc, Long currentId) {
        return currentId != null && pc.getId().equals(currentId);
    }
}