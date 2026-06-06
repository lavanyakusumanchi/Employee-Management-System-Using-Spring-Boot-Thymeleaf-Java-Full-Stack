package edu.qs.attendance.service;

import edu.qs.attendance.entity.Worker;
import edu.qs.attendance.exception.ApiException;
import edu.qs.attendance.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkerService {

    private final WorkerRepository workerRepo;
    private final ActiveWorkerCacheService activeCache;

    public WorkerService(WorkerRepository workerRepo, ActiveWorkerCacheService activeCache) {
        this.workerRepo = workerRepo;
        this.activeCache = activeCache;
    }

    @Transactional
    public Worker create(Worker worker) {
        worker.setId(null);
        return workerRepo.save(worker);
    }

    @Transactional
    public Worker update(Long id, Worker changes) {
        Worker existing = workerRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("WORKER_NOT_FOUND", "No worker with id " + id));
        existing.setName(changes.getName());
        existing.setDesignation(changes.getDesignation());
        existing.setDailyWageRate(changes.getDailyWageRate());
        existing.setActive(changes.isActive());
        Worker saved = workerRepo.save(existing);

        // Cache invalidation: stale name/designation/wage must not linger in Redis.
        activeCache.invalidate(id);
        return saved;
    }
}
