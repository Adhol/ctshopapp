package com.ardecs.ctshop.controller;

import com.ardecs.ctshop.exceptions.NotFoundException;
import com.ardecs.ctshop.persistence.entity.User;
import com.ardecs.ctshop.persistence.repository.UserRepository;
import com.ardecs.ctshop.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final RegistrationService registrationService;


    public UserController(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
    }

    @GetMapping("admin/user")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/user";
    }

    @GetMapping("admin/showFormForAddUser")
    public String showFormForAdd(Model model) {
        model.addAttribute("user", new User());
        return "admin/userForm";
    }

    @PostMapping("admin/saveUser")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {

        //Нельзя убирать или менять эту проверку, иначу будет не возможно обновить существующего пользователя.
        //Эта проверка специально стоит перед проверкой существования пользователя.
        if (user.getId() != null && !bindingResult.hasErrors()) {
            registrationService.registration(user);
            return "redirect:/admin/user";
        }

        if (bindingResult.hasErrors() || registrationService.isUserExists(user)) {
            return "admin/userForm";
        }

        registrationService.registration(user);
        return "redirect:/admin/user";
    }

    @GetMapping("admin/showFormForUpdateUser/{id}")
    public String showFormForUpdate(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        model.addAttribute("user", user);
        return "admin/userForm";
    }
    @GetMapping("admin/user/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userRepository.deleteById(id);
        return "redirect:/admin/user";
    }

    @GetMapping("user/userInfo/{id}")
    public String showUserInfo(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        model.addAttribute("user", user);
        return "user/userInfo";
    }

}
