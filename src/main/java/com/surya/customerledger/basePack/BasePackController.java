package com.surya.customerledger.basePack;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/pack")
public class BasePackController {
  private final BasePackService basePackService;

  public BasePackController(BasePackService basePackService) {
    this.basePackService = basePackService;
  }

  @PostMapping
  public void createPack(@RequestBody @Valid BasePackDto dto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
    }
    basePackService.createBasePack(dto);
  }

  @GetMapping
  public List<BasePackPartial> getBasePacks() {
    return basePackService.getAllPacks();
  }

  @PutMapping
  public void updateBasePack(@RequestBody @Valid UpdateBasePackDto dto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, bindingResult.getFieldError().getDefaultMessage());
    }
    basePackService.updatePack(dto);
  }
}
