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
    var currentArea = areaRepo.findById(dto.id()).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "The area you're trying to update doesn't exist."));
    if (!company.getId().equals(currentArea.getCompany().getId())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The area you're trying update doesn't belong to you");
    }
    currentArea.setName(dto.name());
    areaRepo.save(currentArea);
  }
}
