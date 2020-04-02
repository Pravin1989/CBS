package com.imagination.cbs.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.imagination.cbs.domain.Contractor;
import com.imagination.cbs.domain.ContractorEmployee;
import com.imagination.cbs.domain.ContractorEmployeeDefaultRate;
import com.imagination.cbs.domain.ContractorEmployeeRole;
import com.imagination.cbs.domain.ContractorEmployeeSearch;
import com.imagination.cbs.domain.RoleDm;
import com.imagination.cbs.dto.ContractorDto;
import com.imagination.cbs.dto.ContractorEmployeeDto;
import com.imagination.cbs.dto.ContractorEmployeeRequest;
import com.imagination.cbs.dto.ContractorEmployeeSearchDto;
import com.imagination.cbs.dto.ContractorRequest;
import com.imagination.cbs.exception.ResourceNotFoundException;
import com.imagination.cbs.mapper.ContractorEmployeeMapper;
import com.imagination.cbs.mapper.ContractorMapper;
import com.imagination.cbs.repository.ContractorEmployeeRepository;
import com.imagination.cbs.repository.ContractorEmployeeSearchRepository;
import com.imagination.cbs.repository.ContractorRepository;
import com.imagination.cbs.repository.RoleRepository;
import com.imagination.cbs.service.ContractorService;
import com.imagination.cbs.service.LoggedInUserService;

@Service("contractorService")
public class ContractorServiceImpl implements ContractorService {

	@Autowired
	private ContractorRepository contractorRepository;

	@Autowired
	private ContractorEmployeeSearchRepository contractorEmployeeSearchRepository;

	@Autowired
	private ContractorEmployeeRepository contractorEmployeeRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private ContractorEmployeeMapper contractorEmployeeMapper;

	@Autowired
	private ContractorMapper contractorMapper;

	@Autowired
	private LoggedInUserService loggedInUserService;
	
	@Override
	public Page<ContractorDto> getContractorDeatils(int pageNo, int pageSize, String sortingField,
			String sortingOrder) {
		Pageable pageable = createPageable(pageNo, pageSize, sortingField, sortingOrder);
		return toContractorDtoPage(contractorRepository.findAll(pageable));
	}

	@Override
	public Page<ContractorDto> getContractorDeatilsContainingName(String contractorName, int pageNo, int pageSize,
			String sortingField, String sortingOrder) {
		Pageable pageable = createPageable(pageNo, pageSize, sortingField, sortingOrder);
		return toContractorDtoPage(contractorRepository.findByContractorNameContains(contractorName, pageable));
	}

	@Override
	public Page<ContractorEmployeeSearchDto> geContractorEmployeeDetailsByRoleId(Long roleId, int pageNo, int pageSize,
			String sortingField, String sortingOrder) {
		Pageable pageable = createPageable(pageNo, pageSize, sortingField, sortingOrder);
		Page<ContractorEmployeeSearch> contractorEmployeePage = contractorEmployeeSearchRepository.findByRoleId(roleId,
				pageable);

		return toContractorEmployeeDtoPage(contractorEmployeePage);
	}

	@Override
	public Page<ContractorEmployeeSearchDto> geContractorEmployeeDetailsByRoleIdAndName(Long roleId,
			String contractorName, int pageNo, int pageSize, String sortingField, String sortingOrder) {
		Pageable pageable = createPageable(pageNo, pageSize, sortingField, sortingOrder);
		Page<ContractorEmployeeSearch> contractorEmployeePage = contractorEmployeeSearchRepository
				.findByRoleIdAndContractorEmployeeNameContains(roleId, contractorName, pageable);

		return toContractorEmployeeDtoPage(contractorEmployeePage);
	}

	@Override
	public ContractorDto getContractorByContractorId(Long id) {

		Optional<Contractor> optionalContractor = contractorRepository.findById(id);

		if (!optionalContractor.isPresent()) {
			throw new ResourceNotFoundException("Contactor Not Found with Id:- " + id);
		}

		return contractorMapper.toContractorDtoFromContractorDomain(optionalContractor.get());
	}

	@Override
	public ContractorEmployeeDto getContractorEmployeeByContractorIdAndEmployeeId(Long contractorId, Long employeeId) {

		ContractorEmployee contractorEmployee = contractorEmployeeRepository
				.findContractorEmployeeByContractorIdAndEmployeeId(contractorId, employeeId);

		return contractorEmployeeMapper.toContractorEmployeeDtoFromContractorEmployee(contractorEmployee);
	}

	@Transactional
	@Override
	public ContractorDto addContractorDetails(ContractorRequest contractorRequest) {

		String loggedInUser = loggedInUserService.getLoggedInUserDetails().getDisplayName();

		Contractor contractorDomain = contractorMapper.toContractorDomainFromContractorRequest(contractorRequest);
		contractorDomain.setChangedBy(loggedInUser);
		
		Contractor savedContractor = contractorRepository.save(contractorDomain);
		
		return contractorMapper.toContractorDtoFromContractorDomain(savedContractor);
	}
	

	@Override
	public ContractorEmployeeDto addContractorEmployee(Long contractorId, ContractorEmployeeRequest request) {

		String loggedInUser = loggedInUserService.getLoggedInUserDetails().getDisplayName();
		ContractorEmployee contractorEmpDomain = new ContractorEmployee();
		contractorEmpDomain.setEmployeeName(request.getContractorEmployeeName());
		contractorEmpDomain.setKnownAs(request.getKnownAs());
		contractorEmpDomain.setChangedBy(loggedInUser);
		
		Optional<Contractor> optionalContractor = contractorRepository.findById(contractorId);
		if (!optionalContractor.isPresent()) {
			throw new ResourceNotFoundException("Contactor Not Found with Id:- " + contractorId);
		}
		contractorEmpDomain.setContractor(optionalContractor.get());

		Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
		ContractorEmployeeRole contractorEmployeeRole = new ContractorEmployeeRole();
		Optional<RoleDm> optionalRoleDm = roleRepository.findById(request.getRoleId());
		if (!optionalRoleDm.isPresent()) {
			throw new ResourceNotFoundException("Role Not Found with Id:- " + request.getRoleId());
		}
		contractorEmployeeRole.setRoleDm(optionalRoleDm.get());
		contractorEmployeeRole.setDateFrom(currentTimeStamp);
		contractorEmployeeRole.setChangedBy(loggedInUser);
		contractorEmployeeRole.setContractorEmployee(contractorEmpDomain);
		contractorEmpDomain.setContractorEmployeeRole(contractorEmployeeRole);
		
		ContractorEmployeeDefaultRate contractorEmployeeDefaultRate = new ContractorEmployeeDefaultRate();
		contractorEmployeeDefaultRate.setCurrencyId(request.getCurrencyId());
		contractorEmployeeDefaultRate.setRate(request.getDayRate());
		contractorEmployeeDefaultRate.setDateFrom(currentTimeStamp);
		contractorEmployeeDefaultRate.setChangedBy(loggedInUser);
		contractorEmployeeDefaultRate.setContractorEmployee(contractorEmpDomain);
		contractorEmpDomain.setContractorEmployeeDefaultRate(contractorEmployeeDefaultRate);
		
		ContractorEmployee savedcontractorEmployee = contractorEmployeeRepository.save(contractorEmpDomain);
		return contractorEmployeeMapper.toContractorEmployeeDtoFromContractorEmployee(savedcontractorEmployee);
	}

	private Pageable createPageable(int pageNo, int pageSize, String sortingField, String sortingOrder) {
		Sort sort = null;
		if (sortingOrder.equals("ASC")) {
			sort = Sort.by(Direction.ASC, sortingField);
		}
		if (sortingOrder.equals("DESC")) {
			sort = Sort.by(Direction.DESC, sortingField);
		}

		return PageRequest.of(pageNo, pageSize, sort);
	}

	private Page<ContractorEmployeeSearchDto> toContractorEmployeeDtoPage(
			Page<ContractorEmployeeSearch> contractorEmployeePage) {
		return contractorEmployeePage.map((contractorEmployeeSearched) -> {
			ContractorEmployeeSearchDto contractorEmployeeDto = new ContractorEmployeeSearchDto();
			contractorEmployeeDto.setContractorEmployeeId(contractorEmployeeSearched.getContractorEmployeeId());
			contractorEmployeeDto.setContractorEmployeeName(contractorEmployeeSearched.getContractorEmployeeName());
			contractorEmployeeDto.setDayRate(contractorEmployeeSearched.getDayRate());
			contractorEmployeeDto.setRoleId(contractorEmployeeSearched.getRoleId());
			contractorEmployeeDto.setRole(contractorEmployeeSearched.getRole());
			contractorEmployeeDto.setContractorId(contractorEmployeeSearched.getContractorId());
			contractorEmployeeDto.setCompany(contractorEmployeeSearched.getCompany());
			contractorEmployeeDto.setNoOfBookingsInPast(contractorEmployeeSearched.getNoOfBookingsInPast());

			return contractorEmployeeDto;
		});
	}

	private Page<ContractorDto> toContractorDtoPage(Page<Contractor> contractorPage) {
		return contractorPage.map((contractor) -> {
			ContractorDto contractorDto = new ContractorDto();

			contractorDto.setContractorId(contractor.getContractorId());
			contractorDto.setContractorName(contractor.getContractorName());
			contractorDto.setCompanyType(contractor.getCompanyType());
			contractorDto.setContactDetails(contractor.getContactDetails());
			contractorDto.setChangedDate(contractor.getChangedDate());
			contractorDto.setChangedBy(contractor.getChangedBy());
			contractorDto.setStatus(contractor.getStatus());
			contractorDto.setMaconomyVendorNumber(contractor.getMaconomyVendorNumber());
			contractorDto.setAddressLine1(contractor.getAddressLine1());
			contractorDto.setAddresLine2(contractor.getAddresLine2());
			contractorDto.setAddresLine3(contractor.getAddresLine3());
			contractorDto.setPostalDistrict(contractor.getPostalDistrict());
			contractorDto.setPostalCode(contractor.getPostalCode());
			contractorDto.setCountry(contractor.getCountry());
			contractorDto.setAttention(contractor.getAttention());
			contractorDto.setEmail(contractor.getEmail());
			contractorDto.setOnPreferredSupplierList(contractor.getOnPreferredSupplierList());

			return contractorDto;
		});
	}

}
