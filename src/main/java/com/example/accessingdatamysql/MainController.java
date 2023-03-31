package com.example.accessingdatamysql;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser (@RequestParam String name
            , @RequestParam String email,@RequestParam String surname, @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        User n = new User();
        n.setName(name);
        n.setEmail(email);
        n.setSurname(surname);
        n.setPassword(password);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @DeleteMapping("/deleteAllUsers")
    public  @ResponseBody String deleteAllUsers() {
        userRepository.deleteAll();

        return "Deleted all users";
    }
    @GetMapping(path = "/getUser/{id}")
    public @ResponseBody Object getUserByIs(@PathVariable("id") Integer id){

        return userRepository.findById(id);
    }

    @DeleteMapping("/deleteUser/{id}")
    public  @ResponseBody String deleteUserById(@PathVariable("id") Integer id) {
        Optional<User> user = userRepository.findById(id);
        userRepository.deleteById(id);
        String name = "";
        if (user.isPresent()) {
            name = user.get().getName();
            userRepository.deleteById(id);
            return "Deleted user " + name;
        } else {
            return "User with id " + id + " not found";
        }
    }

    @PostMapping("/login")
    public String getSessionVariable(HttpServletRequest request) {
        HttpSession isLoggedIn = request.getSession();
        isLoggedIn.setAttribute("isLoggedIn",false);
        return "logged succesfully";
    }

    @GetMapping("/logout")
    public String endUserSession(HttpServletRequest request) {
        HttpSession isLoggedIn = request.getSession();
        isLoggedIn.setAttribute("isLoggedIn",false);
        return "logged out user succesfully";
    }



}