package com.mballem.curso.security.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mballem.curso.security.domain.Especialidade;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long>{

	//busca uma especialidade digitada pelo usuário no campo de busca
	@Query("SELECT e FROM Especialidade e WHERE e.titulo LIKE :search%")
	Page<Especialidade> findAllByTitulo(@Param("search") String search, Pageable pageable);

}
