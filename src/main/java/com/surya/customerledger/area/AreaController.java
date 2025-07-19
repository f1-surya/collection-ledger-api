package com.surya.customerledger.area;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/area")
public class AreaController {
  private final AreaService areaService;

  public AreaController(AreaService areaService) {
    this.areaService = areaService;
  }

  @PostMapping
  public void createArea(@RequestBody @Valid CreateAreaDto dto) {
    areaService.create(dto);
  }

  @GetMapping
  public List<AreaNameIdOnly> getAllAreas() {
    return areaService.getAreas();
  }

  @PutMapping
  public void updateArea(@RequestBody @Valid UpdateAreaDto updateAreaDto) {
    areaService.update(updateAreaDto);
  }
}
