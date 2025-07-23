package api.gibat.uz.profile.repository;

import api.gibat.uz.profile.entity.ProfileEntity;
import api.gibat.uz.security.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity,String> {

    @Query("SELECT p FROM ProfileEntity p WHERE p.username = :username")
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(@Param("username") String username);

    @Query("SELECT p FROM ProfileEntity p WHERE p.id = :id AND p.visible = true ")
    Optional<ProfileEntity> findByIdAndVisibleTrue(String id);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET status = :status WHERE id = :id")
    void changeStatus(@Param("id") String id, @Param("status") GeneralStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET password = :encode WHERE id = :id")
    void updatePassword(@Param("id") String id, @Param("encode") String password);


    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET name = :name, surname = :surname WHERE id = :id")
    void updateDetail(@Param("id") String id, @Param("name") String name, @Param("surname") String surname);

    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET username = :username WHERE id = :id")
    void updateUsername(@Param("id") String id, @Param("username") String username);


    @Modifying
    @Transactional
    @Query("UPDATE ProfileEntity SET tempUsername = :tempUsername WHERE id = :id")
    void updateTempUsername(@Param("id") String id, @Param("tempUsername") String tempUsername);
}
