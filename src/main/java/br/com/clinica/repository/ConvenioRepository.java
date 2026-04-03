package br.com.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Convenio;

@Repository
public interface ConvenioRepository extends JpaRepository<Convenio, Long>{
    
}
