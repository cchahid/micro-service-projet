package com.buberdinner.reservationservice.infrastructure.repository;




import com.buberdinner.reservationservice.domain.module.Dinner;
import com.buberdinner.reservationservice.infrastructure.entity.DinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DinnerRepository extends JpaRepository<DinnerEntity, Long> {
    default Dinner save(Dinner dinner) {
        DinnerEntity entity = DinnerEntity.fromDomain(dinner);
        DinnerEntity savedEntity = save(entity);
        return savedEntity.toDomain();
    }

    default Optional<Dinner> findByIdDinner(Long id){
            Optional<DinnerEntity> dinnerEntity = findById(id);
            if(dinnerEntity.isPresent()){
                return Optional.of(dinnerEntity.get().toDomain());
            }
            throw new IllegalArgumentException("Dinner with id " + id + " not found");
    }


}
