// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.UserDto;
// import com.znz.tpip_backend.service.UserService;



// @RestController
// @RequestMapping("/api/v1/tpip/users")
// public class UserController {

//     private UserService userService;

//     public UserController(UserService userService) {
//         this.userService = userService;
//     }

//      @GetMapping
//     public ResponseEntity<List<UserDto>> getAllUsers() {
//         List<UserDto> dtos = userService.getAllUsers();
//         return new ResponseEntity<>(dtos, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
//         UserDto dto = userService.getUserById(id);
//         return ResponseEntity.ok(dto);
//     }


//     // REGISTER
//     @PostMapping("/register")
//     public ResponseEntity<UserDto> register(@RequestBody UserDto dto) {
//         UserDto user = userService.register(dto);
//         return ResponseEntity.ok(user);
//     }
//      // Login
//     @PostMapping("/login")
//     public ResponseEntity<UserDto> login(@RequestBody UserDto dto) {
//         UserDto userDto = userService.login(dto.getEmail(), dto.getPassword());
//         // UserDto userDto = userService.login(dto);
//         return ResponseEntity.ok(userDto);
//     }
    
    
    
// }
