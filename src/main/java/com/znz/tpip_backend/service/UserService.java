// package com.znz.tpip_backend.service;

// import java.util.List;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.UserDto;
// import com.znz.tpip_backend.model.User;
// import com.znz.tpip_backend.repository.UserRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class UserService {

//     private final ModelMapper modelMapper;
//     private final UserRepository userRepository;


//     public List<UserDto> getAllUsers() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
//     }

//     public UserDto getUserById(Long id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
//     }

//     public UserDto register(UserDto dto) {
//         if (userRepository.existsByEmail(dto.getEmail())) {
//             throw new RuntimeException("Email already exists");
//         }

//         User user = new User();
//         user.setName(dto.getName());
//         user.setEmail(dto.getEmail());
//         user.setPassword(dto.getPassword());

//         User saved = userRepository.save(user);

//         dto.setId(saved.getId());
//         dto.setPassword(null); // hide password in response

//         return dto;
//         // return modelMapper.map(saved, UserDto.class);
//     }

    
//     public UserDto login(String email, String password) {
//         // User user = userRepository.findByEmail(email)
//         //     .orElseThrow(()-> new RuntimeException("User not found"));

//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//         if (!user.getPassword().equals(password)) {
//             throw new RuntimeException("Invalid password");
//         }
//         UserDto dto = new UserDto();
//         dto.setId(user.getId());
//         dto.setName(user.getName());
//         dto.setEmail(user.getEmail());

//         return dto;
//     }


// }

// // REGISTER
// // {
// //   "name": "Rahma",
// //   "email": "rahma@gmail.com",
// //   "password": "Amhar123@"
// // }
// // {
// //   "name": "Shuayb",
// //   "email": "shuayb@gmail.com",
// //   "password": "shuayb123@"
// // }
// // {
// //   "name": "Kauthar",
// //   "email": "kau@gmail.com",
// //   "password": "kau@123"
// // }