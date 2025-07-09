package com.ufscar.formularoute.controllers;

import com.ufscar.formularoute.dto.Lap;
import com.ufscar.formularoute.repository.LapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class DataCleanupService {

    @Autowired
    private LapRepository lapRepository;

    @Scheduled(cron = "@weekly")
    @Transactional
    public void cleanupOldLaps() {
        ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(10);

        List<Lap> oldLaps = lapRepository.findAllByAddedBefore(cutoffDate);

        if (!oldLaps.isEmpty()) {
            System.out.println("Encontradas " + oldLaps.size() + " laps antigas para exclusão.");
            lapRepository.deleteAll(oldLaps);
            System.out.println("Limpeza de dados antigos concluída.");
        }
    }
}