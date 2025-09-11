package com.surya.customerledger.basePack;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pack")
public class BasePackController {
  private final BasePackService basePackService;

  public BasePackController(BasePackService basePackService) {
    this.basePackService = basePackService;
  }

  @PostMapping
  public void createPack(@RequestBody @Valid BasePackDto dto) {
    basePackService.createBasePack(dto);
  }

  @GetMapping
  public List<BasePackConCount> getBasePacks() {
    return basePackService.getAllPacks();
  }

  @PutMapping
  public void updateBasePack(@RequestBody @Valid UpdateBasePackDto dto) {
    basePackService.updatePack(dto);
  }

  @DeleteMapping
  public void deleteBasePack(@RequestParam("id") Integer basePackId) {
    basePackService.deletePack(basePackId);
  }
}
