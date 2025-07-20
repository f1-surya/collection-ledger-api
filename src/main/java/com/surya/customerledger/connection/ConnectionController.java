package com.surya.customerledger.connection;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connection")
public class ConnectionController {
  private final ConnectionService connectionService;

  public ConnectionController(ConnectionService connectionService) {
    this.connectionService = connectionService;
  }

  @PostMapping
  public void createConnection(@RequestBody @Valid CreateConnectionDto dto) {
    connectionService.create(dto);
  }

  @PutMapping
  public void updateConnection(@RequestBody @Valid UpdateConnectionDto dto) {
    connectionService.update(dto);
  }

  @GetMapping
  public List<ConnectionPartial> getAll() {
    return connectionService.getAllConnections();
  }

  @GetMapping("/{connectionId}")
  public ConnectionPartial getConnectionById(@PathVariable("connectionId") Integer id) {
    return connectionService.getConnectionById(id);
  }
}
