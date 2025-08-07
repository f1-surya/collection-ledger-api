package com.surya.customerledger.auth;

import com.surya.customerledger.auth.dto.SignupFormData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthWebController {

  private final AuthService authService;

  public AuthWebController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("formData", new SignupFormData());
    return "signup";
  }

  @PostMapping("/signup")
  public String signupSubmit(
      @Valid @ModelAttribute("formData") SignupFormData formData,
      BindingResult bindingResult,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    if (bindingResult.hasErrors()) {
      return "signup";
    }

    if (!formData.getPassword().equals(formData.getPasswordRepeat())) {
      bindingResult.rejectValue("passwordRepeat", "error.passwordRepeat", "Passwords do not match");
      return "signup";
    }

    return authService.register(formData, bindingResult, request, response);
  }
}
