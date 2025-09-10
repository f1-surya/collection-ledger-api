package com.surya.customerledger.area;

import com.surya.customerledger.company.CompanyRepo;
import com.surya.customerledger.db.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AreaService {
  private final CompanyRepo companyRepo;
  private final AreaRepo areaRepo;

  public AreaService(CompanyRepo companyRepo, AreaRepo areaRepo) {
    this.companyRepo = companyRepo;
    this.areaRepo = areaRepo;
  }

  public void create(CreateAreaDto dto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to create areas."));
    var newArea = new Area();
    newArea.setCompany(company);
    newArea.setName(dto.name());
    areaRepo.save(newArea);
  }

  public List<AreaDto> getAreas() {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have areas."));
    return areaRepo.findAll(company.getId());
  }

  public void update(UpdateAreaDto dto) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have areas."));
    var currentArea = areaRepo.findByIdAndCompany(dto.id(), company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The area you're trying to update doesn't exist."));

    currentArea.setName(dto.name());
    areaRepo.save(currentArea);
  }

  public void delete(Integer areaId) {
    var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var company = companyRepo.findByOwner(user).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You need to have a company to have areas."));
    var currentArea = areaRepo.findByIdAndCompany(areaId, company).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The area you're trying to update doesn't exist."));

    areaRepo.delete(currentArea);
  }
}
