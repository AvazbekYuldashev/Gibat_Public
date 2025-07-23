package api.gibat.uz.profile.repository;

import api.gibat.uz.profile.entity.ProfileRoleEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRoleRepository extends CrudRepository<ProfileRoleEntity, String> {

    @Query("SELECT p FROM ProfileRoleEntity p WHERE p.profileId = :profileId AND p.visible = TRUE ")
    List<ProfileRoleEntity> getAllByProfileIdAndVisibleTrue(String profileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProfileRoleEntity WHERE profileId= :profileId")
    void deleteByProfileId(@Param("profileId")String profileId);

    Optional<ProfileRoleEntity> findByProfileId(String id);
}
