package com.example.ps2.controller;

import com.example.ps2.model.Medicao;
import com.example.ps2.repository.MedicaoRepository;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("medicao")
public class MedicaoController {

    @Autowired
    private MedicaoRepository medicaoRepository;
    @Autowired
    MongoTemplate mongoTemplate;


    // CRUD

    @PostMapping
    public ResponseEntity<Medicao> cadastrar(@RequestBody @Valid Medicao medicao){

        try {
            medicaoRepository.save(medicao);
            return new ResponseEntity<>(medicao, HttpStatus.CREATED);
        }
        catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public ResponseEntity<List<Medicao>> listar (){
        List<Medicao> medicaos = medicaoRepository.findAll();

        return new ResponseEntity<>(medicaos, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Optional<Medicao>> buscar (@PathVariable String id){

        try {
            Optional<Medicao> medicao = medicaoRepository.findById(id);
            return new ResponseEntity<>(medicao,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Medicao> deletar (@PathVariable String id){
        try {
            medicaoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    // Metodo que  retorna uma lista distinta com os locais de medicao
    // sera usado para alimentar os filtros do app mobile
    @GetMapping("/locais")
    public ResponseEntity<List<String>> listarLocais (){
        List<String> categoryList = new ArrayList<>();
        DistinctIterable<String> locais = mongoTemplate
                .getCollection("medicao")
                .distinct("sensor.local.nome",String.class);
        MongoCursor cursor = locais.iterator();
        while (cursor.hasNext()) {
            String category = (String)cursor.next();
            categoryList.add(category);
        }

        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }

    // Metodo que recebe um local de medicao e retorna uma lista distinta com as datas de medicao
    // sera usado para alimentar os filtros do app mobile
    @GetMapping("/datas")
    public ResponseEntity<List<String>> listarDatasPorLocal (@RequestParam("local") String local ){
        local = local.replace("+"," ");
        Query query = new Query();
        query.addCriteria(Criteria.where("sensor.local.nome").is(local));
        List<String> medicaos = mongoTemplate.find(query,Medicao.class).stream().map(
                        Medicao::getMedicao_date
                ).distinct().sorted()
                .collect(Collectors.toList());
        return new ResponseEntity<>(medicaos, HttpStatus.OK);
    }


    // Relatorios


    // Real Time

    @GetMapping("/max_ph")
    public ResponseEntity<Medicao> maxPH (){
        List<Medicao> medicaos = medicaoRepository.findAll();
        Medicao maxPH = medicaos.stream().max(Comparator.comparing(Medicao::getPh_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(maxPH, HttpStatus.OK);
    }

    @GetMapping("/min_ph")
    public ResponseEntity<Medicao> minPH (){
        List<Medicao> medicaos = medicaoRepository.findAll();
        Medicao maxC02 = medicaos.stream().min(Comparator.comparing(Medicao::getPh_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(maxC02, HttpStatus.OK);
    }


    @GetMapping("/max_co2")
    public ResponseEntity<Medicao> maxCO2 (){
        List<Medicao> medicaos = medicaoRepository.findAll();
        Medicao maxC02 = medicaos.stream().max(Comparator.comparing(Medicao::getCo2_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(maxC02, HttpStatus.OK);
    }

    @GetMapping("/min_co2")
    public ResponseEntity<Medicao> minCO2 (){
        List<Medicao> medicaos = medicaoRepository.findAll();
        Medicao maxC02 = medicaos.stream().min(Comparator.comparing(Medicao::getCo2_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(maxC02, HttpStatus.OK);
    }


    /// Por data


    @GetMapping("/min-ph-date")
    public ResponseEntity<Medicao> minPHDate (@RequestParam("data") String data ) {
        List<Medicao> medicaos = medicaoRepository.medicaoPorData(data);
        Medicao minPh = medicaos.stream().min(Comparator.comparing(Medicao::getPh_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(minPh, HttpStatus.OK);
    }

    @GetMapping("/max-ph-date")
    public ResponseEntity<Medicao> maxPHDate (@RequestParam("data") String data ) {
        List<Medicao> medicaos = medicaoRepository.medicaoPorData(data);
        Medicao minPh = medicaos.stream().max(Comparator.comparing(Medicao::getPh_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(minPh, HttpStatus.OK);
    }

    @GetMapping("/min-co2-date")
    public ResponseEntity<Medicao> minCo2Date (@RequestParam("data") String data ) {
        List<Medicao> medicaos = medicaoRepository.medicaoPorData(data);
        Medicao minCo2 = medicaos.stream().min(Comparator.comparing(Medicao::getCo2_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(minCo2, HttpStatus.OK);
    }

    @GetMapping("/max-co2-date")
    public ResponseEntity<Medicao> maxCo2Date (@RequestParam("data") String data ) {
        List<Medicao> medicaos = medicaoRepository.medicaoPorData(data);
        Medicao maxCo2 = medicaos.stream().max(Comparator.comparing(Medicao::getCo2_vol)).orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(maxCo2, HttpStatus.OK);
    }

}
