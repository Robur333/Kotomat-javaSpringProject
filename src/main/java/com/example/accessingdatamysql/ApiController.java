package com.example.accessingdatamysql;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Controller // This means that this class is a Controller


@RequestMapping(path="/api") // This means URL's start with /demo (after Application path)
public class ApiController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser (@RequestParam String name
            , @RequestParam String email,@RequestParam String surname, @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        StringBuilder returnValue = new StringBuilder();

        userRepository.findAll().forEach(
                user -> {
                    if (user.getEmail() == email)  {
                        returnValue.append("user with that email already exists");
                    }
                }
        );
        User n = new User();
        n.setName(name);
        n.setEmail(email);
        n.setSurname(surname);
        n.setPassword(password);
        n.setFavoriteCats(new String[0]);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
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
    public @ResponseBody Boolean getSessionVariable(@RequestParam String email, @RequestParam String password, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Iterable<User> allUsers = userRepository.findAll();
        boolean isLoggedIn = false;

        for (User user: allUsers
             ) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                isLoggedIn = true;
                break;
            }

        }
    if (isLoggedIn) {
            session.setAttribute("isLoggedIn", true);
            return true;
        } else {
            return false;
        }
    }
    @GetMapping("/logout")
    public String endUserSession(HttpServletRequest request) {
        HttpSession isLoggedIn = request.getSession();
        isLoggedIn.setAttribute("isLoggedIn",false);
        return "logged out user succesfully";
    }

    @PostMapping("/addFavorite")
    public @ResponseBody String addCatToFavorite(@RequestParam String catId, @RequestParam Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {


            User user = userOptional.get();

            // Get the current favorite cats array
            String[] currentFavoriteCats = user.getFavoriteCats();

            // Create a new array with one additional element to hold the new favorite cat
            String[] newFavoriteCats = Arrays.copyOf(currentFavoriteCats, currentFavoriteCats.length + 1);

            // Add the new favorite cat to the end of the array
            newFavoriteCats[newFavoriteCats.length - 1] = catId;

            // Set the updated array as the user's favorite cats
            user.setFavoriteCats(newFavoriteCats);

            // Save the updated user object to the database
            userRepository.save(user);

            return "success";
        } else {
            return "user not found";
        }
    }
    @PostMapping("/removeFavorite")
    public @ResponseBody String removeCatFromFavorite(@RequestParam String catId, @RequestParam Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Get the current favorite cats array
            String[] currentFavoriteCats = user.getFavoriteCats();

            // Find the index of the cat to remove in the current array
            int indexToRemove = -1;
            for (int i = 0; i < currentFavoriteCats.length; i++) {
                if (currentFavoriteCats[i].equals(catId)) {
                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -1) {
                // Create a new array with one fewer element to hold the updated favorite cats
                String[] newFavoriteCats = new String[currentFavoriteCats.length - 1];

                // Copy the elements from the old array to the new array, skipping the removed cat
                int j = 0;
                for (int i = 0; i < currentFavoriteCats.length; i++) {
                    if (i != indexToRemove) {
                        newFavoriteCats[j++] = currentFavoriteCats[i];
                    }
                }

                // Set the updated array as the user's favorite cats
                user.setFavoriteCats(newFavoriteCats);

                // Save the updated user object to the database
                userRepository.save(user);

                return "success";
            } else {
                return "cat not found in favorites";
            }
        } else {
            return "user not found";
        }
    }
    @GetMapping("/favoriteCats")
    public @ResponseBody String[] getFavoriteCats(@RequestParam Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getFavoriteCats();
        } else {
            return new String[0];
        }
    }
}
