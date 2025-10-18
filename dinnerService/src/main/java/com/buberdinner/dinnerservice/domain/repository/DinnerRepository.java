package com.buberdinner.dinnerservice.domain.repository;

import com.buberdinner.dinnerservice.domain.entity.Dinner;
import com.buberdinner.dinnerservice.domain.valueobject.DinnerStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Dinner entity.
 */
public interface DinnerRepository {
    /**
     * Saves a dinner entity.
     * 
     * @param dinner the dinner to save
     * @return the saved dinner
     */
    Dinner save(Dinner dinner);

    /**
     * Saves multiple dinner entities.
     *
     * @param dinners the dinners to save
     * @return the saved dinners
     * List<Dinner> saveAll(List<Dinner> dinners);
     */


    /**
     * Finds a dinner by its ID.
     * 
     * @param id the dinner ID
     * @return an Optional containing the dinner if found, or empty if not found
     */
    Optional<Dinner> findById(Long id);

    /**
     * Finds all dinners.
     * 
     * @return a list of all dinners
     */
    List<Dinner> findAll();

    /**
     * Finds all dinners by host ID.
     * 
     * @param hostId the host ID
     * @return a list of dinners for the given host
     */
    List<Dinner> findByHostId(Long hostId);

    /**
     * Finds all dinners by menu ID.
     * 
     * @param menuId the menu ID
     * @return a list of dinners for the given menu
     */
    List<Dinner> findByMenuId(Long menuId);

    /**
     * Deletes a dinner by its ID.
     * 
     * @param id the dinner ID
     */
    void deleteById(Long id);

    List<Dinner> findByMenuIdAndStatus(long menuId, DinnerStatus dinnerStatus);

    List<Dinner> saveAll(List<Dinner> dinners);
}
