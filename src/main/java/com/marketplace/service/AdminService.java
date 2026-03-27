package com.marketplace.service;

import com.marketplace.repository.ItemRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers",      userRepository.count());
        stats.put("activeListings",  itemRepository.countActiveListings());
        stats.put("totalItems",      itemRepository.count());

        // Category distribution
        List<Object[]> categoryData = itemRepository.countByCategory();
        Map<String, Long> categories = categoryData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long)   row[1]
                ));
        stats.put("categoryDistribution", categories);

        return stats;
    }
}
