package edu.qs.attendance.service;

import edu.qs.attendance.dto.ActiveWorkerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * Active-workers cache (assignment Part 1.3).
 * - Each active worker is one Redis key: "active:worker:{id}" -> ActiveWorkerView (JSON).
 * - TTL of 16 hours acts as a safety net for missed clock-outs.
 * - GET /active reads exclusively from here, never the DB.
 * - Every Redis call is wrapped so that if Redis is down the app degrades instead of crashing
 *   (works together with the CacheErrorHandler from LF-202).
 */
@Service
public class ActiveWorkerCacheService {

    private static final Logger log = LoggerFactory.getLogger(ActiveWorkerCacheService.class);
    private static final String KEY_PREFIX = "active:worker:";
    private static final Duration TTL = Duration.ofHours(16);

    private final RedisTemplate<String, Object> redis;

    public ActiveWorkerCacheService(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    private String key(Long workerId) {
        return KEY_PREFIX + workerId;
    }

    public void addActive(ActiveWorkerView view) {
        try {
            redis.opsForValue().set(key(view.workerId()), view, TTL);
        } catch (Exception ex) {
            log.warn("Could not add active worker {} to Redis: {}", view.workerId(), ex.getMessage());
        }
    }

    public void removeActive(Long workerId) {
        try {
            redis.delete(key(workerId));
        } catch (Exception ex) {
            log.warn("Could not remove active worker {} from Redis: {}", workerId, ex.getMessage());
        }
    }

    // Called on profile update so cached name/designation never goes stale (cache invalidation rule).
    public void invalidate(Long workerId) {
        removeActive(workerId);
    }

    public boolean isActive(Long workerId) {
        try {
            return Boolean.TRUE.equals(redis.hasKey(key(workerId)));
        } catch (Exception ex) {
            log.warn("Redis hasKey failed for worker {}: {}", workerId, ex.getMessage());
            return false;
        }
    }

    public List<ActiveWorkerView> listActive() {
        List<ActiveWorkerView> result = new ArrayList<>();
        try {
            Set<String> keys = redis.keys(KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) return result;
            for (String k : keys) {
                Object v = redis.opsForValue().get(k);
                if (v instanceof ActiveWorkerView view) {
                    result.add(view);
                }
            }
        } catch (Exception ex) {
            log.warn("Redis listActive failed: {}", ex.getMessage());
        }
        return result;
    }
}
