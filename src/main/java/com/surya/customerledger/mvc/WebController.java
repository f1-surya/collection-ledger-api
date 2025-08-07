package com.surya.customerledger.mvc;

import com.surya.customerledger.connection.ConnectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
  private final ConnectionService connectionService;

  public WebController(ConnectionService connectionService) {
    this.connectionService = connectionService;
  }

  @GetMapping("/")
  public String home() {
    return "index";
  }

  @GetMapping("/createCompany")
  public String createCompany() {
    return "createCompany";
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    var connections = connectionService.getAllConnections();
    model.addAttribute("connections", connections);
    return "dashboard";
  }
}
