package com.pnc.etlpoc.repository;

import com.pnc.etlpoc.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    @Query(value = "SELECT TOP 1 Name FROM ( SELECT Name,Count(*) SpeechCount FROM Speaker WHERE YEAR(DATE)=:year GROUP BY Name )T ORDER BY SpeechCount DESC", nativeQuery = true)
    Optional<String> findBestSpeakerByYear(@Param("year") String year);

    @Query(value = "SELECT TOP 1 Name FROM Speaker WHERE Subject=:subject GROUP BY Name ORDER BY Count(*) DESC", nativeQuery = true)
    Optional<String> findBestSpeakerBySubject(@Param("subject") String subject);

    @Query(value = "SELECT  TOP 1 name FROM ( SELECT name,SUM(Words) WordsCnt FROM Speaker GROUP BY name ) T ORDER BY WordsCnt", nativeQuery = true)
    Optional<String> findSpeakerWithMinWordsOverAll();

}
