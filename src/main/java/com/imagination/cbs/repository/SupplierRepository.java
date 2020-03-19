package com.imagination.cbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imagination.cbs.domain.SupplierTypeDm;

@Repository("supplierRepository")
public interface SupplierRepository extends JpaRepository<SupplierTypeDm, Long> {

	List<SupplierTypeDm> findByNameContains(String name);

}
