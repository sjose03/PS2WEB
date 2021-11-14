package com.example.ps2.repository;

import com.example.ps2.model.Medicao;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicaoRepository extends MongoRepository<Medicao,String> {

    @Query("{ medicao_date : { $eq :  '?0'  } }")
    List<Medicao> medicaoPorData(String date);

}
